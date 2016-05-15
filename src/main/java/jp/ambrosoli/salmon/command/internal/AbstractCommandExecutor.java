package jp.ambrosoli.salmon.command.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import jp.ambrosoli.salmon.command.CommandExecutor;
import jp.ambrosoli.salmon.command.CommandParameter;
import jp.ambrosoli.salmon.command.CommandState;
import jp.ambrosoli.salmon.event.EventHandlingException;
import jp.ambrosoli.salmon.messages.Messages;

public abstract class AbstractCommandExecutor implements CommandExecutor {

    @Override
    public CommandState execute(final CommandParameter parameter) throws IOException, InterruptedException {
        CommandStateManager manager = new CommandStateManager(parameter);
        ProcessBuilder builder = new ProcessBuilder();
        setCommand(builder, parameter);
        setDirectory(builder, parameter);
        setEnvironment(builder, parameter);
        setRedirectFile(builder, parameter);
        if (parameter.isAsync()) {
            Thread thread = new Thread(() -> {
                try {
                    runProcess(builder, parameter, manager);
                } catch (IOException | InterruptedException e) {
                    // notifyError()で通知されるため何もしない
                }
            });
            thread.start();
        } else {
            runProcess(builder, parameter, manager);
        }
        return manager.getState();
    }

    protected void runProcess(final ProcessBuilder builder, final CommandParameter parameter,
            final CommandStateManager manager) throws IOException, InterruptedException {
        try {
            Process process = manager.startProcess(builder);
            if (parameter.getTimeoutUnit() != null) {
                if (!process.waitFor(parameter.getTimeout(), parameter.getTimeoutUnit())) {
                    process.destroyForcibly();
                    TimeoutException exception = new TimeoutException(
                            Messages.getString("CommandExecutor.command.timeout", parameter.getTimeout(), //$NON-NLS-1$
                                    Messages.getString(parameter.getTimeoutUnit().name())));
                    manager.notifyCancelled(process.exitValue(), exception);
                } else {
                    manager.notifyEnded(process.exitValue());
                }
            } else {
                process.waitFor();
                manager.notifyEnded(process.exitValue());
            }
        } catch (EventHandlingException e) {
            // この例外が発生した場合はすでにイベント処理が実行済みのため、そのまま例外をスローする
            throw e;
        } catch (Throwable thrown) {
            manager.notifyError(thrown);
            throw thrown;
        } finally {
            manager.notifyFinished();
        }
    }

    protected void setCommand(final ProcessBuilder builder, final CommandParameter parameter) {
        List<String> command = new ArrayList<>();
        command.add(parameter.getCommand());
        Collection<String> options = parameter.getOptions();
        if (options != null) {
            command.addAll(options);
        }
        builder.command(command);
    }

    protected void setDirectory(final ProcessBuilder builder, final CommandParameter parameter) {
        File directory = parameter.getDirectory();
        if (directory != null) {
            builder.directory(directory);
        }
    }

    protected void setEnvironment(final ProcessBuilder builder, final CommandParameter parameter) {
        Consumer<Map<String, String>> environment = parameter.getEnvironment();
        if (environment != null) {
            environment.accept(builder.environment());
        }
    }

    protected void setRedirectFile(final ProcessBuilder builder, final CommandParameter parameter) {
        File outputFile = parameter.getRedirectOutput();
        if (outputFile != null) {
            builder.redirectOutput(outputFile);
        }
        File errorFile = parameter.getRedirectError();
        if (errorFile != null) {
            builder.redirectError(errorFile);
        }
    }
}
