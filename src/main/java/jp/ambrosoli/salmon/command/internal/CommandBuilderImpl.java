package jp.ambrosoli.salmon.command.internal;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

import jp.ambrosoli.salmon.command.CommandBuilder;
import jp.ambrosoli.salmon.command.CommandExecutorRegistry;
import jp.ambrosoli.salmon.command.CommandState;
import jp.ambrosoli.salmon.command.CommandState.Status;
import jp.ambrosoli.salmon.event.EventHandler;
import jp.ambrosoli.salmon.messages.Messages;
import jp.ambrosoli.salmon.utils.PlatformUtil;
import jp.ambrosoli.salmon.utils.StringUtil;

class CommandBuilderImpl implements CommandBuilder {

    private final CommandParameterImpl parameter;

    public CommandBuilderImpl(final String command) {
        if (StringUtil.isEmpty(command)) {
            throw new IllegalArgumentException(Messages.getString("CommandBuilder.error.command.empty")); //$NON-NLS-1$
        }
        parameter = new CommandParameterImpl();
        parameter.setCommand(command);
    }

    @Override
    public CommandState execute() throws IOException, InterruptedException {
        return CommandExecutorRegistry.get().execute(parameter);
    }

    @Override
    public CommandBuilder options(final Collection<String> options) {
        parameter.setOptions(options);
        return this;
    }

    @Override
    public CommandBuilder options(final String... options) {
        return options(Arrays.asList(options));
    }

    @Override
    public CommandBuilder onDone(final EventHandler<CommandState> handler) {
        parameter.getDoneHandlers().add(handler);
        return this;
    }

    @Override
    public CommandBuilder onReady(final EventHandler<CommandState> handler) {
        parameter.setOnStateChanged(Status.READY, handler);
        return this;
    }

    @Override
    public CommandBuilder onRunning(final EventHandler<CommandState> handler) {
        parameter.setOnStateChanged(Status.RUNNING, handler);
        return this;
    }

    @Override
    public CommandBuilder onSucceeded(final EventHandler<CommandState> handler) {
        parameter.setOnStateChanged(Status.SUCCEEDED, handler);
        return this;
    }

    @Override
    public CommandBuilder onFailed(final EventHandler<CommandState> handler) {
        parameter.setOnStateChanged(Status.FAILED, handler);
        return this;
    }

    @Override
    public CommandBuilder onError(final EventHandler<CommandState> handler) {
        parameter.setOnStateChanged(Status.ERROR, handler);
        return this;
    }

    @Override
    public CommandBuilder onCancelled(final EventHandler<CommandState> handler) {
        parameter.setOnStateChanged(Status.CANCELLED, handler);
        return this;
    }

    @Override
    public CommandBuilder successCondition(final IntPredicate condition) {
        parameter.setSuccessCondition(condition);
        return this;
    }

    @Override
    public CommandBuilder timeout(final long milliseconds) {
        return timeout(milliseconds, TimeUnit.MILLISECONDS);
    }

    @Override
    public CommandBuilder timeout(final long time, final TimeUnit unit) {
        parameter.setTimeout(time);
        parameter.setTimeoutUnit(unit);
        return this;
    }

    @Override
    public CommandBuilder directory(final String directory) {
        return directory(new File(directory));
    }

    @Override
    public CommandBuilder directory(final Path directory) {
        return directory(directory.toFile());
    }

    @Override
    public CommandBuilder directory(final File directory) {
        try {
            File canonicalFile = directory.getCanonicalFile();
            if (!canonicalFile.exists()) {
                throw new IllegalArgumentException(
                        Messages.getString("CommandBuilder.error.directory.notfound", canonicalFile)); //$NON-NLS-1$
            }
            parameter.setDirectory(canonicalFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    @Override
    public CommandBuilder environment(final Consumer<Map<String, String>> consumer) {
        parameter.setEnvironment(consumer);
        return this;
    }

    @Override
    public CommandBuilder async(final boolean async) {
        parameter.setAsync(async);
        return this;
    }

    @Override
    public CommandBuilder redirectStdout(final File redirect) {
        parameter.setRedirectOutput(redirect);
        return this;
    }

    @Override
    public CommandBuilder redirectStdoutToDevNull() {
        parameter.setRedirectOutput(PlatformUtil.devNull());
        return this;
    }

    @Override
    public CommandBuilder redirectStderr(final File redirect) {
        parameter.setRedirectError(redirect);
        return this;
    }

    @Override
    public CommandBuilder redirectStderrToDevNull() {
        parameter.setRedirectError(PlatformUtil.devNull());
        return this;
    }

}
