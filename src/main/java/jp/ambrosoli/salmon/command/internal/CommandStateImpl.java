package jp.ambrosoli.salmon.command.internal;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jp.ambrosoli.salmon.command.CommandState;
import jp.ambrosoli.salmon.event.Observer;

class CommandStateImpl implements CommandState {

    private Integer exitCode;
    private InputStream stdout;
    private InputStream stderr;
    private Throwable thrown;
    private Status status;
    private List<Observer> observers = new ArrayList<>();

    @Override
    public Integer getExitCode() {
        return exitCode;
    }

    public void setExitCode(final Integer exitCode) {
        this.exitCode = exitCode;
    }

    @Override
    public InputStream getStdout() {
        return stdout;
    }

    public void setStdout(final InputStream stdout) {
        this.stdout = stdout;
    }

    @Override
    public InputStream getStderr() {
        return stderr;
    }

    public void setStderr(final InputStream stderr) {
        this.stderr = stderr;
    }

    @Override
    public Throwable getThrown() {
        return thrown;
    }

    public void setThrown(final Throwable thrown) {
        this.thrown = thrown;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
        notifyObservers();
    }

    @Override
    public boolean isReady() {
        return getStatus() == Status.READY;
    }

    @Override
    public boolean isRunning() {
        return getStatus() == Status.RUNNING;
    }

    @Override
    public boolean isSucceeded() {
        return getStatus() == Status.SUCCEEDED;
    }

    @Override
    public boolean isFailed() {
        return getStatus() == Status.FAILED;
    }

    @Override
    public boolean isError() {
        return getStatus() == Status.ERROR;
    }

    @Override
    public boolean isCancelled() {
        return getStatus() == Status.CANCELLED;
    }

    @Override
    public void addObserver(final Observer observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this);
        }
    }
}
