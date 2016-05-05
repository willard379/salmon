package jp.ambrosoli.salmon.event;

/**
 * <p>
 * イベント処理中に発生した例外を表すクラスです。
 * </p>
 *
 * @author willard379
 * @since 0.1.0
 *
 */
public class EventHandlingException extends RuntimeException {

    /**
     * <p>
     * イベント処理中に発生した例外をラップします。
     * </p>
     *
     * @param cause
     *            イベント処理中に発生した例外
     */
    public EventHandlingException(final Exception cause) {
        super(cause);
    }
}
