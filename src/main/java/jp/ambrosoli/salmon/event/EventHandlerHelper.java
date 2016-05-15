package jp.ambrosoli.salmon.event;

import java.util.List;

public class EventHandlerHelper {

    public static <T> void fireEvent(final List<EventHandler<T>> handlers, final T target) {
        Exception thrown = null;
        for (EventHandler<T> handler : handlers) {
            try {
                handler.handle(target);
            } catch (Exception e) {
                // 例外が発生しても最後までイベントハンドラの処理を続行します。
                thrown = e;
            }
        }
        // 最後に発生した例外をスローします
        if (thrown != null) {
            throw new EventHandlingException(thrown);
        }
    }
}
