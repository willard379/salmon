package jp.ambrosoli.salmon.command;

import java.io.InputStream;

import jp.ambrosoli.salmon.event.Observable;

/**
 * <p>
 * コマンドの実行結果を表すインタフェースです。
 * </p>
 *
 * @author willard379
 * @since 0.1.0
 *
 */
public interface CommandState extends Observable {

    int RC_SUCCESS_DEFAULT = 0;

    enum Status {
        READY, RUNNING, SUCCEEDED, FAILED, ERROR, CANCELLED;
    }

    Integer getExitCode();

    InputStream getStdout();

    InputStream getStderr();

    Throwable getThrown();

    Status getStatus();

    boolean isReady();

    boolean isRunning();

    boolean isSucceeded();

    boolean isFailed();

    boolean isError();

    boolean isCancelled();
}
