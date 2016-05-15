package jp.ambrosoli.salmon.event;

/**
 * <p>
 * Observableオブジェクトに変更があった場合に通知されるインタフェースです。
 * </p>
 *
 * @author willard379
 *
 * @param <T>
 */
public interface Observer {

    void update(Observable observable);
}
