package jp.ambrosoli.salmon.command.internal;

import static jp.ambrosoli.salmon.command.CommandState.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

import jp.ambrosoli.salmon.command.CommandParameter;
import jp.ambrosoli.salmon.command.CommandState;
import jp.ambrosoli.salmon.command.CommandState.Status;
import jp.ambrosoli.salmon.event.EventHandler;

class CommandParameterImpl implements CommandParameter {

    private String command;
    private File directory;
    private long timeout;
    private TimeUnit timeoutUnit;
    private boolean async;
    private File redirectOutput;
    private File redirectError;
    private Consumer<Map<String, String>> environment;
    private Map<Status, List<EventHandler<CommandState>>> handlers = new HashMap<>();
    private List<EventHandler<CommandState>> doneHandlers = new ArrayList<>();
    private IntPredicate successCondition = rc -> rc == RC_SUCCESS_DEFAULT;

    private Collection<String> options;

    @Override
    public String getCommand() {
        return command;
    }

    void setCommand(final String command) {
        this.command = command;
    }

    @Override
    public Collection<String> getOptions() {
        return options;
    }

    void setOptions(final Collection<String> options) {
        this.options = options;
    }

    @Override
    public List<EventHandler<CommandState>> getDoneHandlers() {
        return doneHandlers;
    }

    void setSuccessCondition(final IntPredicate successCondition) {
        this.successCondition = successCondition;
    }

    @Override
    public IntPredicate getSuccessCondition() {
        return successCondition;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    void setTimeout(final long timeout) {
        this.timeout = timeout;
    }

    @Override
    public TimeUnit getTimeoutUnit() {
        return timeoutUnit;
    }

    void setTimeoutUnit(final TimeUnit timeoutUnit) {
        this.timeoutUnit = timeoutUnit;
    }

    @Override
    public File getDirectory() {
        return directory;
    }

    void setDirectory(final File directory) {
        this.directory = directory;
    }

    @Override
    public Consumer<Map<String, String>> getEnvironment() {
        return environment;
    }

    void setEnvironment(final Consumer<Map<String, String>> environment) {
        this.environment = environment;
    }

    @Override
    public boolean isAsync() {
        return async;
    }

    void setAsync(final boolean async) {
        this.async = async;
    }

    @Override
    public File getRedirectOutput() {
        return redirectOutput;
    }

    void setRedirectOutput(final File redirectOutput) {
        this.redirectOutput = redirectOutput;
    }

    @Override
    public File getRedirectError() {
        return redirectError;
    }

    void setRedirectError(final File redirectError) {
        this.redirectError = redirectError;
    }

    @Override
    public Map<Status, List<EventHandler<CommandState>>> getHandlers() {
        return handlers;
    }

    void setOnStateChanged(final Status status, final EventHandler<CommandState> handler) {
        List<EventHandler<CommandState>> handlerList = handlers.computeIfAbsent(status, key -> new ArrayList<>());
        handlerList.add(handler);
    }
}
