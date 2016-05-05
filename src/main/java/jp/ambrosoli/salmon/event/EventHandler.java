package jp.ambrosoli.salmon.event;

/**
 * イベントハンドラを表すインタフェース
 *
 * @author willard379
 *
 * @param <T>
 *            このハンドラが処理できるイベントクラス
 */
@FunctionalInterface
public interface EventHandler<T> {

    /**
     * このハンドラが登録された型のイベントが発生した時にお呼び出されます。
     *
     * @param event
     *            発生したイベント
     */
    void handle(T event) throws Exception;
}
