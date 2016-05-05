package jp.ambrosoli.salmon.test.util;

import static org.mockito.Mockito.*;

import jp.ambrosoli.salmon.event.EventHandler;

public class SpiedEventHandler<T> implements EventHandler<T> {

    private final EventHandler<T> handler;

    private SpiedEventHandler(final EventHandler<T> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(final T event) throws Exception {
        handler.handle(event);
    }

    public static <T> EventHandler<T> wrap(final EventHandler<T> handler) {
        return spy(new SpiedEventHandler<>(handler));
    }

}
