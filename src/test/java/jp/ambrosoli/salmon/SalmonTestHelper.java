package jp.ambrosoli.salmon;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.gen5.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Locale;
import java.util.function.Consumer;

import jp.ambrosoli.salmon.command.CommandState;
import jp.ambrosoli.salmon.event.EventHandler;
import jp.ambrosoli.salmon.test.util.RunnableToThrowException;
import jp.ambrosoli.salmon.test.util.SalmonConfigAccessor;
import jp.ambrosoli.salmon.test.util.SpiedEventHandler;

@SuppressWarnings("nls")
class SalmonTestHelper {

    static void msdos(final RunnableToThrowException runnable) throws Exception {
        SalmonConfigAccessor.withTemporaryConfig("auto.msdos", true, runnable);
    }

    static void verifySucceeded(final CommandState state) {
        assertAll(() -> {
            assertThat(state.isRunning(), is(false));
            assertThat(state.isSucceeded(), is(true));
            assertThat(state.isFailed(), is(false));
            assertThat(state.isError(), is(false));
            assertThat(state.isCancelled(), is(false));
            assertThat(state.getThrown(), is(nullValue()));
        });
    }

    static void verifyFailed(final CommandState state) {
        assertAll(() -> {
            assertThat(state.isRunning(), is(false));
            assertThat(state.isSucceeded(), is(false));
            assertThat(state.isFailed(), is(true));
            assertThat(state.isError(), is(false));
            assertThat(state.isCancelled(), is(false));
            assertThat(state.getThrown(), is(nullValue()));
        });
    }

    static void verifyError(final CommandState state) {
        assertAll(() -> {
            assertThat(state.isRunning(), is(false));
            assertThat(state.isSucceeded(), is(false));
            assertThat(state.isFailed(), is(false));
            assertThat(state.isError(), is(true));
            assertThat(state.isCancelled(), is(false));
            assertThat(state.getThrown(), is(notNullValue()));
        });
    }

    static void verifyCancelled(final CommandState state) {
        assertAll(() -> {
            assertThat(state.isRunning(), is(false));
            assertThat(state.isSucceeded(), is(false));
            assertThat(state.isFailed(), is(false));
            assertThat(state.isError(), is(false));
            assertThat(state.isCancelled(), is(true));
            assertThat(state.getThrown(), is(notNullValue()));
        });
    }

    static EventHandler<CommandState> verifyableEmptyHandler() {
        return verifyableHandler(state -> {
        });
    }

    static EventHandler<CommandState> verifyableHandler(final Consumer<CommandState> consumer) {
        return SpiedEventHandler.wrap(consumer::accept);
    }

    @SafeVarargs
    static void verifyHandled(final EventHandler<CommandState>... handlers) {
        assertAll(() -> {
            for (EventHandler<CommandState> handler : handlers) {
                verify(handler, only()).handle(any(CommandState.class));
            }
        });
    }

    static void verifySucceededHandled(final EventHandler<CommandState> succeeded,
            final EventHandler<CommandState> failed, final EventHandler<CommandState> error,
            final EventHandler<CommandState> cancelled, final EventHandler<CommandState> completed) {
        assertAll(() -> {
            verify(succeeded, only()).handle(any(CommandState.class));
            verify(failed, never()).handle(any(CommandState.class));
            verify(error, never()).handle(any(CommandState.class));
            verify(cancelled, never()).handle(any(CommandState.class));
            verify(completed, only()).handle(any(CommandState.class));
        });
    }

    static void verifyFailedHandled(final EventHandler<CommandState> succeeded, final EventHandler<CommandState> failed,
            final EventHandler<CommandState> error, final EventHandler<CommandState> cancelled,
            final EventHandler<CommandState> completed) {
        assertAll(() -> {
            verify(succeeded, never()).handle(any(CommandState.class));
            verify(failed, only()).handle(any(CommandState.class));
            verify(error, never()).handle(any(CommandState.class));
            verify(cancelled, never()).handle(any(CommandState.class));
            verify(completed, only()).handle(any(CommandState.class));
        });
    }

    static void verifyErrorHandled(final EventHandler<CommandState> succeeded, final EventHandler<CommandState> failed,
            final EventHandler<CommandState> error, final EventHandler<CommandState> cancelled,
            final EventHandler<CommandState> completed) {
        assertAll(() -> {
            verify(succeeded, never()).handle(any(CommandState.class));
            verify(failed, never()).handle(any(CommandState.class));
            verify(error, only()).handle(any(CommandState.class));
            verify(cancelled, never()).handle(any(CommandState.class));
            verify(completed, only()).handle(any(CommandState.class));
        });
    }

    static void verifyCancelledHandled(final EventHandler<CommandState> succeeded,
            final EventHandler<CommandState> failed, final EventHandler<CommandState> error,
            final EventHandler<CommandState> cancelled, final EventHandler<CommandState> completed) {
        assertAll(() -> {
            verify(succeeded, never()).handle(any(CommandState.class));
            verify(failed, never()).handle(any(CommandState.class));
            verify(error, never()).handle(any(CommandState.class));
            verify(cancelled, only()).handle(any(CommandState.class));
            verify(completed, only()).handle(any(CommandState.class));
        });
    }

    static String i18n(final String ja, final String en) {
        return Locale.getDefault().equals(Locale.JAPAN) ? ja : en;
    }

    static void verifyThrown(final CommandState state, final Throwable thrown) {
        assertAll(() -> {
            assertThat(state.getThrown(), is(notNullValue()));
            assertThat(state.getThrown(), is(instanceOf(thrown.getClass())));
            assertThat(state.getThrown().getMessage(), is(thrown.getMessage()));
        });
    }
}
