package jp.ambrosoli.salmon.command.internal;

import java.io.IOException;
import java.util.List;

import jp.ambrosoli.salmon.command.CommandParameter;
import jp.ambrosoli.salmon.command.CommandState;
import jp.ambrosoli.salmon.command.CommandState.Status;
import jp.ambrosoli.salmon.event.EventHandler;
import jp.ambrosoli.salmon.event.EventHandlerHelper;
import jp.ambrosoli.salmon.event.Observable;
import jp.ambrosoli.salmon.event.Observer;

public class CommandStateManager implements Observer {

    private final CommandParameter parameter;
    private final CommandStateImpl state;
    private boolean handled;

    CommandStateManager(final CommandParameter parameter) {
        this.parameter = parameter;
        state = new CommandStateImpl();
        state.addObserver(this);
        state.setStatus(Status.READY);
    }

    CommandState getState() {
        return state;
    }

    boolean isHandled() {
        return handled;
    }

    Process startProcess(final ProcessBuilder builder) throws IOException {
        Process process = builder.start();
        state.setStdout(process.getInputStream());
        state.setStderr(process.getErrorStream());
        state.setStatus(Status.RUNNING);
        return process;
    }

    synchronized void notifyEnded(final Integer exitCode) {
        if (isHandled()) {
            return;
        }
        state.setExitCode(exitCode);
        state.setStatus(judgeStatus(exitCode));
    }

    synchronized void notifyError(final Throwable thrown) {
        if (isHandled()) {
            return;
        }
        state.setThrown(thrown);
        state.setStatus(Status.ERROR);
    }

    synchronized void notifyCancelled(final Integer exitCode, final Throwable thrown) {
        if (isHandled()) {
            return;
        }
        state.setExitCode(exitCode);
        state.setThrown(thrown);
        state.setStatus(Status.CANCELLED);
    }

    synchronized void notifyFinished() {
        EventHandlerHelper.fireEvent(parameter.getDoneHandlers(), state);
    }

    private Status judgeStatus(final Integer exitCode) {
        Status status = null;
        if (parameter.getSuccessCondition().test(exitCode)) {
            status = Status.SUCCEEDED;
        } else {
            status = Status.FAILED;
        }
        return status;
    }

    @Override
    public void update(final Observable observable) {
        CommandState state = (CommandState) observable;
        List<EventHandler<CommandState>> handlers = parameter.getHandlers().get(state.getStatus());
        if (handlers != null) {
            EventHandlerHelper.fireEvent(handlers, state);
        }
    }

}
