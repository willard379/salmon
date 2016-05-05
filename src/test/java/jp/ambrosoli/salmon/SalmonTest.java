package jp.ambrosoli.salmon;

import static jp.ambrosoli.salmon.Salmon.*;
import static jp.ambrosoli.salmon.SalmonTestHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.gen5.api.Assertions.*;
import static org.junit.gen5.api.Assumptions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

import org.junit.gen5.api.BeforeAll;
import org.junit.gen5.api.Disabled;
import org.junit.gen5.api.DisplayName;
import org.junit.gen5.api.Test;
import org.junit.gen5.junit4.runner.JUnit5;
import org.junit.runner.RunWith;

import jp.ambrosoli.salmon.command.CommandState;
import jp.ambrosoli.salmon.event.EventHandler;
import jp.ambrosoli.salmon.event.EventHandlingException;
import jp.ambrosoli.salmon.utils.IOUtil;
import jp.ambrosoli.salmon.utils.PlatformUtil;
import jp.ambrosoli.salmon.utils.StringUtil;

@RunWith(JUnit5.class)
@SuppressWarnings("nls")
public class SalmonTest {

    @BeforeAll
    public static void beforeAll() {
        String osName = System.getProperty("os.name");
        assumeTrue(osName.toLowerCase().contains("windows"));
    }

    @Test
    public void commandの引数にnullを渡すとIllegalArgumentExceptionが発生すること() throws Exception {
        // Setup
        String command = null;

        // Exercise
        IllegalArgumentException thrown = expectThrows(IllegalArgumentException.class, () -> {
            command(command);
        });

        // Verify
        String expectedMessage = i18n("コマンド名にnullや空文字は指定できません。", "The command name must not be null or empty.");
        assertThat(thrown.getMessage(), is(expectedMessage));
    }

    @Test
    public void オプションなしでコマンドを実行する() throws Exception {
        msdos(() -> {

            // Setup
            String command = "cd";

            // Exercise
            CommandState actual = command(command).execute();

            // Verify
            verifySucceeded(actual);
            String expected = new File(".").getCanonicalPath();
            assertThat(StringUtil.chomp(IOUtil.readAll(actual.getStdout())), is(expected));
        });
    }

    @Test
    public void オプションを1つ配列で指定してコマンドを実行する() throws Exception {
        msdos(() -> {

            // Setup
            String command = "echo";
            String[] options = { "hoge" };

            // Exercise
            CommandState actual = command(command).options(options).execute();

            // Verify
            verifySucceeded(actual);
            assertThat(StringUtil.chomp(IOUtil.readAll(actual.getStdout())), is("hoge"));
        });
    }

    @Test
    public void オプションを複数配列で指定してコマンドを実行する() throws Exception {
        msdos(() -> {

            // Setup
            String command = "echo";
            String[] options = { "hoge", "foo", "bar" };

            // Exercise
            CommandState actual = command(command).options(options).execute();

            // Verify
            verifySucceeded(actual);
            assertThat(StringUtil.chomp(IOUtil.readAll(actual.getStdout())), is("hoge foo bar"));
        });
    }

    @Test
    public void オプションを1つコレクションで指定してコマンドを実行する() throws Exception {
        msdos(() -> {

            // Setup
            String command = "echo";
            List<String> options = Arrays.asList("hoge");

            // Exercise
            CommandState actual = command(command).options(options).execute();

            // Verify
            verifySucceeded(actual);
            assertThat(StringUtil.chomp(IOUtil.readAll(actual.getStdout())), is("hoge"));
        });
    }

    @Test
    public void オプションを複数コレクションで指定してコマンドを実行する() throws Exception {
        msdos(() -> {

            // Setup
            String command = "echo";
            List<String> options = Arrays.asList("hoge", "foo", "bar");

            // Exercise
            CommandState actual = command(command).options(options).execute();

            // Verify
            verifySucceeded(actual);
            assertThat(StringUtil.chomp(IOUtil.readAll(actual.getStdout())), is("hoge foo bar"));
        });
    }

    @Test
    public void コマンドが正常終了() throws Exception {
        msdos(() -> {
            // Setup
            EventHandler<CommandState> succeeded = verifyableEmptyHandler();
            EventHandler<CommandState> failed = verifyableEmptyHandler();
            EventHandler<CommandState> error = verifyableEmptyHandler();
            EventHandler<CommandState> cancelled = verifyableEmptyHandler();
            EventHandler<CommandState> done = verifyableEmptyHandler();

            // Exercise
            CommandState state = command("exit").options("0").onSucceeded(succeeded).onFailed(failed).onError(error)
                    .onCancelled(cancelled).onDone(done).execute();

            // Verify
            verifySucceeded(state);
            verifySucceededHandled(succeeded, failed, error, cancelled, done);
        });
    }

    @Test
    public void コマンドが失敗() throws Exception {
        msdos(() -> {
            // Setup
            EventHandler<CommandState> succeeded = verifyableEmptyHandler();
            EventHandler<CommandState> failed = verifyableEmptyHandler();
            EventHandler<CommandState> error = verifyableEmptyHandler();
            EventHandler<CommandState> cancelled = verifyableEmptyHandler();
            EventHandler<CommandState> done = verifyableEmptyHandler();

            // Exercise
            CommandState state = command("exit").options("1").onSucceeded(succeeded).onFailed(failed).onError(error)
                    .onCancelled(cancelled).onDone(done).execute();

            // Verify
            verifyFailed(state);
            verifyFailedHandled(succeeded, failed, error, cancelled, done);
        });
    }

    @Test
    public void コマンドがエラー終了() throws Exception {
        // Setup
        EventHandler<CommandState> succeeded = verifyableEmptyHandler();
        EventHandler<CommandState> failed = verifyableEmptyHandler();
        EventHandler<CommandState> error = verifyableHandler(SalmonTestHelper::verifyError);
        EventHandler<CommandState> cancelled = verifyableEmptyHandler();
        EventHandler<CommandState> done = verifyableEmptyHandler();

        // Exercise
        assertThrows(IOException.class, () -> {
            command("存在しないコマンド").onSucceeded(succeeded).onFailed(failed).onError(error).onCancelled(cancelled)
                    .onDone(done).execute();
        });

        // Verify
        verifyErrorHandled(succeeded, failed, error, cancelled, done);
    }

    @Test
    public void コマンドがキャンセル終了() throws Exception {
        msdos(() -> {
            // Setup
            EventHandler<CommandState> succeeded = verifyableEmptyHandler();
            EventHandler<CommandState> failed = verifyableEmptyHandler();
            EventHandler<CommandState> error = verifyableEmptyHandler();
            EventHandler<CommandState> cancelled = verifyableEmptyHandler();
            EventHandler<CommandState> done = verifyableEmptyHandler();

            // Exercise
            CommandState state = command("exit").options("0").timeout(-1).onSucceeded(succeeded).onFailed(failed)
                    .onError(error).onCancelled(cancelled).onDone(done).execute();

            // Verify
            verifyCancelled(state);
            verifyCancelledHandled(succeeded, failed, error, cancelled, done);
            Exception ecpectedThrown = new TimeoutException(
                    i18n("コマンドが-1ミリ秒でタイムアウトしました。", "The command has timed out at -1 milliseconds."));
            verifyThrown(state, ecpectedThrown);
        });
    }

    @Test
    public void successHandlerで例外発生() throws Exception {
        msdos(() -> {
            // Setup
            EventHandler<CommandState> succeeded1 = verifyableHandler(state -> {
                throw new RuntimeException();
            });
            EventHandler<CommandState> succeeded2 = verifyableHandler(SalmonTestHelper::verifySucceeded);
            EventHandler<CommandState> failed = verifyableEmptyHandler();
            EventHandler<CommandState> error = verifyableEmptyHandler();
            EventHandler<CommandState> cancelled = verifyableEmptyHandler();
            EventHandler<CommandState> done = verifyableEmptyHandler();

            // Exercise
            expectThrows(EventHandlingException.class, () -> {
                command("exit").options("0").onSucceeded(succeeded1).onSucceeded(succeeded2).onFailed(failed)
                        .onError(error).onCancelled(cancelled).onDone(done).execute();
            });

            // Verify
            verifySucceededHandled(succeeded1, failed, error, cancelled, done);
            verify(succeeded2, only()).handle(any(CommandState.class));
        });
    }

    @Test
    public void failedHandlerで例外発生() throws Exception {
        msdos(() -> {
            // Setup
            EventHandler<CommandState> succeeded = verifyableEmptyHandler();
            EventHandler<CommandState> failed1 = verifyableHandler(state -> {
                throw new RuntimeException();
            });
            EventHandler<CommandState> failed2 = verifyableHandler(SalmonTestHelper::verifyFailed);
            EventHandler<CommandState> error = verifyableEmptyHandler();
            EventHandler<CommandState> cancelled = verifyableEmptyHandler();
            EventHandler<CommandState> done = verifyableEmptyHandler();

            // Exercise
            expectThrows(EventHandlingException.class, () -> {
                command("exit").options("1").onSucceeded(succeeded).onFailed(failed1).onFailed(failed2).onError(error)
                        .onCancelled(cancelled).onDone(done).execute();
            });

            // Verify
            verifyFailedHandled(succeeded, failed1, error, cancelled, done);
            verify(failed2, only()).handle(any(CommandState.class));
        });
    }

    @Test
    public void errorHandlerで例外発生() throws Exception {
        // Setup
        EventHandler<CommandState> succeeded = verifyableEmptyHandler();
        EventHandler<CommandState> failed = verifyableEmptyHandler();
        EventHandler<CommandState> error1 = verifyableHandler(state -> {
            throw new RuntimeException();
        });
        EventHandler<CommandState> error2 = verifyableHandler(SalmonTestHelper::verifyError);
        EventHandler<CommandState> cancelled = verifyableEmptyHandler();
        EventHandler<CommandState> done = verifyableEmptyHandler();

        // Exercise
        assertThrows(EventHandlingException.class, () -> {
            command("存在しないコマンド").onSucceeded(succeeded).onFailed(failed).onError(error1).onError(error2)
                    .onCancelled(cancelled).onDone(done).execute();
        });

        // Verify
        verifyErrorHandled(succeeded, failed, error1, cancelled, done);
        verify(error2, only()).handle(any(CommandState.class));
    }

    @Test
    public void cancelledHandlerで例外発生() throws Exception {
        msdos(() -> {
            // Setup
            EventHandler<CommandState> succeeded = verifyableEmptyHandler();
            EventHandler<CommandState> failed = verifyableEmptyHandler();
            EventHandler<CommandState> error = verifyableEmptyHandler();
            EventHandler<CommandState> cancelled1 = verifyableHandler(state -> {
                throw new RuntimeException();
            });
            EventHandler<CommandState> cancelled2 = verifyableHandler(SalmonTestHelper::verifyCancelled);
            EventHandler<CommandState> done = verifyableEmptyHandler();

            // Exercise
            expectThrows(EventHandlingException.class, () -> {
                command("exit").options("0").timeout(-1).onSucceeded(succeeded).onFailed(failed).onError(error)
                        .onCancelled(cancelled1).onCancelled(cancelled2).onDone(done).execute();
            });

            // Verify
            verifyCancelledHandled(succeeded, failed, error, cancelled1, done);
            verify(cancelled2, only()).handle(any(CommandState.class));
        });
    }

    @Test
    public void doneHandlerで例外発生() throws Exception {
        msdos(() -> {
            // Setup
            EventHandler<CommandState> succeeded = verifyableEmptyHandler();
            EventHandler<CommandState> failed = verifyableEmptyHandler();
            EventHandler<CommandState> error = verifyableEmptyHandler();
            EventHandler<CommandState> cancelled = verifyableEmptyHandler();
            EventHandler<CommandState> done1 = verifyableHandler(state -> {
                throw new RuntimeException();
            });
            EventHandler<CommandState> done2 = verifyableHandler(SalmonTestHelper::verifySucceeded);

            // Exercise
            expectThrows(EventHandlingException.class, () -> {
                command("exit").options("0").onSucceeded(succeeded).onFailed(failed).onError(error)
                        .onCancelled(cancelled).onDone(done1).onDone(done2).execute();
            });

            // Verify
            verifySucceededHandled(succeeded, failed, error, cancelled, done1);
            verify(done2, only()).handle(any(CommandState.class));
        });
    }

    @Test
    public void 終了コード1を正常終了の条件とする_終了コード0でコマンド失敗() throws Exception {
        msdos(() -> {
            // Setup
            EventHandler<CommandState> succeeded = verifyableEmptyHandler();
            EventHandler<CommandState> failed = verifyableEmptyHandler();
            EventHandler<CommandState> error = verifyableEmptyHandler();
            EventHandler<CommandState> cancelled = verifyableEmptyHandler();
            EventHandler<CommandState> done = verifyableEmptyHandler();

            // Exercise
            CommandState state = command("exit").options("0").successCondition(rc -> rc == 1).onSucceeded(succeeded)
                    .onFailed(failed).onError(error).onCancelled(cancelled).onDone(done).execute();

            // Verify
            verifyFailed(state);
            verifyFailedHandled(succeeded, failed, error, cancelled, done);
        });
    }

    @Test
    public void 終了コード1を正常終了の条件とする_終了コード1でコマンド成功() throws Exception {
        msdos(() -> {
            // Setup
            EventHandler<CommandState> succeeded = verifyableEmptyHandler();
            EventHandler<CommandState> failed = verifyableEmptyHandler();
            EventHandler<CommandState> error = verifyableEmptyHandler();
            EventHandler<CommandState> cancelled = verifyableEmptyHandler();
            EventHandler<CommandState> done = verifyableEmptyHandler();

            // Exercise
            CommandState state = command("exit").options("1").successCondition(rc -> rc == 1).onSucceeded(succeeded)
                    .onFailed(failed).onError(error).onCancelled(cancelled).onDone(done).execute();

            // Verify
            verifySucceeded(state);
            verifySucceededHandled(succeeded, failed, error, cancelled, done);
        });

    }

    @Test
    public void successConditionにnullを渡すとエラー終了() throws Exception {
        msdos(() -> {
            // Setup
            IntPredicate successCondition = null;
            EventHandler<CommandState> succeeded = verifyableEmptyHandler();
            EventHandler<CommandState> failed = verifyableEmptyHandler();
            EventHandler<CommandState> error = verifyableHandler(SalmonTestHelper::verifyError);
            EventHandler<CommandState> cancelled = verifyableEmptyHandler();
            EventHandler<CommandState> done = verifyableEmptyHandler();

            // Exercise
            expectThrows(NullPointerException.class, () -> {
                command(":").successCondition(successCondition).onSucceeded(succeeded).onFailed(failed).onError(error)
                        .onCancelled(cancelled).onDone(done).execute();
            });

            // Verify
            verifyErrorHandled(succeeded, failed, error, cancelled, done);
        });
    }

    @Test
    public void 環境変数に新規変数を追加() throws Exception {
        msdos(() -> {
            // Setup
            Consumer<Map<String, String>> consumer = map -> {
                map.put("key", "value");
            };

            // Exercise
            CommandState state = command("set").environment(consumer).execute();

            // Verify
            verifySucceeded(state);

            try (BufferedReader reader = IOUtil.toBufferedReader(state.getStdout())) {
                Map<String, String> result = new HashMap<>();
                for (String line = null; (line = reader.readLine()) != null;) {
                    String[] split = line.split("=");
                    result.put(split[0], split[1]);
                }
                assertThat(result.get("key"), is("value"));
            }
        });
    }

    @Test
    public void 既存の環境変数の値を更新() throws Exception {
        msdos(() -> {
            // Setup
            String pwd = Paths.get(getClass().getResource(".").toURI()).toString();
            Consumer<Map<String, String>> consumer = map -> {
                String oldPath = map.get(PlatformUtil.pathKey());
                String newPath = oldPath + PlatformUtil.pathSeparator() + pwd;
                map.put(PlatformUtil.pathKey(), newPath);
            };

            // Exercise
            CommandState state = command("set").environment(consumer).execute();

            // Verify
            verifySucceeded(state);

            try (BufferedReader reader = IOUtil.toBufferedReader(state.getStdout())) {
                Map<String, String> result = new HashMap<>();
                for (String line = null; (line = reader.readLine()) != null;) {
                    String[] split = line.split("=");
                    result.put(split[0], (split.length == 2 ? split[1] : ""));
                }
                assertThat(result.get(PlatformUtil.pathKey()), containsString(pwd));
            }
        });
    }

    @Test
    public void 既存の環境変数を削除() throws Exception {
        msdos(() -> {
            // Setup
            Consumer<Map<String, String>> consumer = map -> {
                assumeTrue(map.containsKey("JAVA_HOME"));
                map.remove("JAVA_HOME");
            };

            // Exercise
            CommandState state = command("set").environment(consumer).execute();

            // Verify
            verifySucceeded(state);

            try (BufferedReader reader = IOUtil.toBufferedReader(state.getStdout())) {
                Map<String, String> result = new HashMap<>();
                for (String line = null; (line = reader.readLine()) != null;) {
                    String[] split = line.split("=");
                    result.put(split[0], (split.length == 2 ? split[1] : ""));
                }
                assertThat(result.containsKey("JAVA_HOME"), is(false));
            }
        });
    }

    @Test
    public void 非同期で実行() throws Exception {
        msdos(() -> {
            // Setup
            EventHandler<CommandState> succeeded = verifyableEmptyHandler();
            EventHandler<CommandState> done = verifyableEmptyHandler();

            // Exercise
            CommandState state = command("ping").options("127.0.0.1", "-n", "2").async(true).onSucceeded(succeeded)
                    .onDone(done).execute();

            // Verify
            assertThat(state.isReady(), is(true));
            Thread.sleep(100L);
            assertThat(state.isRunning(), is(true));
            Thread.sleep(1000L);
            verifySucceeded(state);
            verifyHandled(succeeded, done);
        });
    }

    @Test
    public void 標準出力をファイルにリダイレクト() throws Exception {
        msdos(() -> {
            // Setup
            File tempFile = Files.createTempFile("", "").toFile();

            try {
                // Exercise
                CommandState state = command("echo").options("hoge").redirectStdout(tempFile).execute();

                // Verify
                verifySucceeded(state);
                assertThat(StringUtil.chomp(IOUtil.readAll(new FileInputStream(tempFile))), is("hoge"));

            } finally {
                // Teardown
                tempFile.delete();
            }
        });
    }

    @Test
    public void 標準エラー出力をファイルにリダイレクト() throws Exception {
        msdos(() -> {
            // Setup
            File tempFile = Files.createTempFile("", "").toFile();

            try {
                // Exercise
                CommandState state = command("java").options("-version").redirectStderr(tempFile).execute();

                // Verify
                verifySucceeded(state);
                assertThat(StringUtil.chomp(IOUtil.readAll(new FileInputStream(tempFile))), startsWith("java version"));

            } finally {
                // Teardown
                tempFile.delete();
            }
        });
    }

    @Test
    public void 標準出力をdevnullにリダイレクト() throws Exception {
        msdos(() -> {
            // Exercise
            CommandState state = command("echo").options("hoge").redirectStdoutToDevNull().execute();

            // Verify
            verifySucceeded(state);
            assertThat(IOUtil.readAll(state.getStdout()), is(""));
        });
    }

    @Test
    public void 標準エラー出力をdevnullにリダイレクト() throws Exception {
        msdos(() -> {
            // Exercise
            CommandState state = command("java").options("-version").redirectStdoutToDevNull().execute();

            // Verify
            verifySucceeded(state);
            assertThat(IOUtil.readAll(state.getStdout()), is(""));
        });
    }

    @Test
    public void timeout_単位を省略_タイムアウトする() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = 200L;

            // Exercise
            CommandState state = command("ping").options("127.0.0.1", "-n", "2").timeout(timeout).execute();

            // Verify
            verifyCancelled(state);
            assertThat(state.getExitCode(), is(1));
        });
    }

    @Test
    public void timeout_単位を省略_タイムアウトしない() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = 200L;

            // Exercise
            CommandState state = command(":").timeout(timeout).execute();

            // Verify
            verifySucceeded(state);
            assertThat(state.getExitCode(), is(0));
        });
    }

    @Test
    public void timeout_単位を省略_負数_タイムアウトする() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = -1L;

            // Exercise
            CommandState state = command(":").timeout(timeout).execute();

            // Verify
            verifyCancelled(state);
            assertThat(state.getExitCode(), is(1));
        });
    }

    @Test
    public void timeout_ナノ秒を指定_タイムアウトする() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = 500L;
            TimeUnit unit = TimeUnit.NANOSECONDS;

            // Exercise
            CommandState state = command("ping").options("127.0.0.1", "-n", "2").timeout(timeout, unit).execute();

            // Verify
            verifyCancelled(state);
            assertThat(state.getExitCode(), is(1));
        });
    }

    @Test
    public void timeout_ナノ秒を指定_タイムアウトしない() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = 100000000L;
            TimeUnit unit = TimeUnit.NANOSECONDS;

            // Exercise
            CommandState state = command(":").timeout(timeout, unit).execute();

            // Verify
            verifySucceeded(state);
            assertThat(state.getExitCode(), is(0));
        });
    }

    @Test
    public void timeout_ナノ秒を指定_負数_タイムアウトする() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = -1L;
            TimeUnit unit = TimeUnit.NANOSECONDS;

            // Exercise
            CommandState state = command(":").timeout(timeout, unit).execute();

            // Verify
            verifyCancelled(state);
            assertThat(state.getExitCode(), is(1));
        });
    }

    @Test
    public void timeout_マイクロ秒を指定_タイムアウトする() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = 500L;
            TimeUnit unit = TimeUnit.MICROSECONDS;

            // Exercise
            CommandState state = command("ping").options("127.0.0.1", "-n", "2").timeout(timeout, unit).execute();

            // Verify
            verifyCancelled(state);
            assertThat(state.getExitCode(), is(1));
        });
    }

    @Test
    public void timeout_マイクロ秒を指定_タイムアウトしない() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = 100000L;
            TimeUnit unit = TimeUnit.MICROSECONDS;

            // Exercise
            CommandState state = command(":").timeout(timeout, unit).execute();

            // Verify
            verifySucceeded(state);
            assertThat(state.getExitCode(), is(0));
        });
    }

    @Test
    public void timeout_マイクロ秒を指定_負数_タイムアウトする() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = -1L;
            TimeUnit unit = TimeUnit.MICROSECONDS;

            // Exercise
            CommandState state = command(":").timeout(timeout, unit).execute();

            // Verify
            verifyCancelled(state);
            assertThat(state.getExitCode(), is(1));
        });
    }

    @Test
    public void timeout_ミリ秒を指定_タイムアウトする() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = 50L;
            TimeUnit unit = TimeUnit.MILLISECONDS;

            // Exercise
            CommandState state = command("ping").options("127.0.0.1", "-n", "2").timeout(timeout, unit).execute();

            // Verify
            verifyCancelled(state);
            assertThat(state.getExitCode(), is(1));
        });
    }

    @Test
    public void timeout_ミリ秒を指定_タイムアウトしない() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = 50L;
            TimeUnit unit = TimeUnit.MILLISECONDS;

            // Exercise
            CommandState state = command(":").timeout(timeout, unit).execute();

            // Verify
            verifySucceeded(state);
            assertThat(state.getExitCode(), is(0));
        });
    }

    @Test
    public void timeout_ミリ秒を指定_負数_タイムアウトする() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = -1L;
            TimeUnit unit = TimeUnit.MILLISECONDS;

            // Exercise
            CommandState state = command(":").timeout(timeout, unit).execute();

            // Verify
            verifyCancelled(state);
            assertThat(state.getExitCode(), is(1));
        });
    }

    @Test
    public void timeout_秒を指定_タイムアウトする() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = 1L;
            TimeUnit unit = TimeUnit.SECONDS;

            // Exercise
            CommandState state = command("ping").options("127.0.0.1", "-n", "3").timeout(timeout, unit).execute();

            // Verify
            verifyCancelled(state);
            assertThat(state.getExitCode(), is(1));
        });
    }

    @Test
    public void timeout_秒を指定_タイムアウトしない() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = 1L;
            TimeUnit unit = TimeUnit.SECONDS;

            // Exercise
            CommandState state = command(":").timeout(timeout, unit).execute();

            // Verify
            verifySucceeded(state);
            assertThat(state.getExitCode(), is(0));
        });
    }

    @Test
    public void timeout_秒を指定_負数_タイムアウトする() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = -1L;
            TimeUnit unit = TimeUnit.SECONDS;

            // Exercise
            CommandState state = command(":").timeout(timeout, unit).execute();

            // Verify
            verifyCancelled(state);
            assertThat(state.getExitCode(), is(1));
        });
    }

    @Disabled("時間がかかるので省略")
    @Test
    public void timeout_分を指定_タイムアウトする() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = 1L;
            TimeUnit unit = TimeUnit.MINUTES;

            // Exercise
            CommandState state = command("ping").options("127.0.0.1", "-n", "70").timeout(timeout, unit).execute();

            // Verify
            verifyCancelled(state);
            assertThat(state.getExitCode(), is(1));
        });
    }

    @Test
    public void timeout_分を指定_タイムアウトしない() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = 1L;
            TimeUnit unit = TimeUnit.MINUTES;

            // Exercise
            CommandState state = command(":").timeout(timeout, unit).execute();

            // Verify
            verifySucceeded(state);
            assertThat(state.getExitCode(), is(0));
        });
    }

    @Test
    public void timeout_分を指定_負数_タイムアウトする() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = -1L;
            TimeUnit unit = TimeUnit.MINUTES;

            // Exercise
            CommandState state = command("ping").options("127.0.0.1", "-n", "3").timeout(timeout, unit).execute();

            // Verify
            verifyCancelled(state);
            assertThat(state.getExitCode(), is(1));
        });
    }

    @Disabled("時間がかかるので省略")
    @Test
    public void timeout_時間を指定_タイムアウトする() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = 1L;
            TimeUnit unit = TimeUnit.HOURS;

            // Exercise
            CommandState state = command("ping").options("127.0.0.1", "-n", "3650").timeout(timeout, unit).execute();

            // Verify
            verifyCancelled(state);
            assertThat(state.getExitCode(), is(1));
        });

    }

    @Test
    public void timeout_時間を指定_タイムアウトしない() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = 1L;
            TimeUnit unit = TimeUnit.HOURS;

            // Exercise
            CommandState state = command(":").timeout(timeout, unit).execute();

            // Verify
            verifySucceeded(state);
            assertThat(state.getExitCode(), is(0));
        });
    }

    @Test
    public void timeout_時間を指定_負数_タイムアウトする() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = -1L;
            TimeUnit unit = TimeUnit.HOURS;

            // Exercise
            CommandState state = command(":").timeout(timeout, unit).execute();

            // Verify
            verifyCancelled(state);
            assertThat(state.getExitCode(), is(1));
        });
    }

    @Disabled("時間がかかるので省略")
    @Test
    public void timeout_日を指定_タイムアウトする() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = 1L;
            TimeUnit unit = TimeUnit.DAYS;

            // Exercise
            CommandState state = command("ping").options("127.0.0.1", "-n", "86410").timeout(timeout, unit).execute();

            // Verify
            verifyCancelled(state);
            assertThat(state.getExitCode(), is(1));
        });
    }

    @Test
    public void timeout_日を指定_タイムアウトしない() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = 1L;
            TimeUnit unit = TimeUnit.DAYS;

            // Exercise
            CommandState state = command(":").timeout(timeout, unit).execute();

            // Verify
            verifySucceeded(state);
            assertThat(state.getExitCode(), is(0));
        });
    }

    @Test
    public void timeout_日を指定_負数_タイムアウトする() throws Exception {
        msdos(() -> {
            // SetUp
            long timeout = -1L;
            TimeUnit unit = TimeUnit.DAYS;

            // Exercise
            CommandState state = command(":").timeout(timeout, unit).execute();

            // Verify
            verifyCancelled(state);
            assertThat(state.getExitCode(), is(1));
        });
    }

    @Test
    public void 作業ディレクトリをPathで指定_存在するディレクトリ() throws Exception {
        msdos(() -> {
            // Setup
            Path directory = Paths.get(getClass().getResource(".").toURI());

            // Exercise
            CommandState state = command("cd").directory(directory).execute();

            // Verify
            verifySucceeded(state);
            assertThat(StringUtil.chomp(IOUtil.readAll(state.getStdout())), is(directory.toString()));
        });
    }

    @Test
    public void 作業ディレクトリをPathで指定_存在しないディレクトリ() throws Exception {
        msdos(() -> {
            // Setup
            Path directory = Paths.get("notexist");

            // Exercise
            IllegalArgumentException thrown = expectThrows(IllegalArgumentException.class, () -> {
                command("cd").directory(directory).execute();
            });

            // Verify
            String absolutePath = directory.toAbsolutePath().toString();
            String expectMessage = i18n("指定されたディレクトリは存在しません。[" + absolutePath + "]",
                    "The specified directory does not exist. [" + absolutePath + "]");
            assertThat(thrown.getMessage(), is(expectMessage));
        });
    }

    @Test
    public void 作業ディレクトリをPathで指定_null() throws Exception {
        msdos(() -> {
            // Setup
            Path directory = null;

            // Exercise
            expectThrows(NullPointerException.class, () -> {
                command("cd").directory(directory).execute();
            });
        });
    }

    @Test
    public void 作業ディレクトリをStringで指定_存在するディレクトリ() throws Exception {
        msdos(() -> {
            // Setup
            Path directory = Paths.get(getClass().getResource(".").toURI());

            // Exercise
            CommandState state = command("cd").directory(directory.toString()).execute();

            // Verify
            verifySucceeded(state);
            assertThat(StringUtil.chomp(IOUtil.readAll(state.getStdout())), is(directory.toString()));
        });
    }

    @Test
    public void 作業ディレクトリをStringで指定_存在しないディレクトリ() throws Exception {
        msdos(() -> {
            // Setup
            Path directory = Paths.get("notexist");

            // Exercise
            IllegalArgumentException thrown = expectThrows(IllegalArgumentException.class, () -> {
                command("cd").directory(directory.toString()).execute();
            });

            // Verify
            String absolutePath = directory.toAbsolutePath().toString();
            String expectMessage = i18n("指定されたディレクトリは存在しません。[" + absolutePath + "]",
                    "The specified directory does not exist. [" + absolutePath + "]");
            assertThat(thrown.getMessage(), is(expectMessage));
        });
    }

    @Test
    public void 作業ディレクトリをStringで指定_null() throws Exception {
        msdos(() -> {
            // Setup
            String directory = null;

            // Exercise
            expectThrows(NullPointerException.class, () -> {
                command("cd").directory(directory).execute();
            });
        });
    }

    @Test
    public void 作業ディレクトリをFileで指定_存在するディレクトリ() throws Exception {
        msdos(() -> {
            // Setup
            Path directory = Paths.get(getClass().getResource(".").toURI());

            // Exercise
            CommandState state = command("cd").directory(directory.toFile()).execute();

            // Verify
            verifySucceeded(state);
            assertThat(StringUtil.chomp(IOUtil.readAll(state.getStdout())), is(directory.toString()));
        });
    }

    @Test
    public void 作業ディレクトリをFileで指定_存在しないディレクトリ() throws Exception {
        msdos(() -> {
            // Setup
            Path directory = Paths.get("notexist");

            // Exercise
            IllegalArgumentException thrown = expectThrows(IllegalArgumentException.class, () -> {
                command("cd").directory(directory.toFile()).execute();
            });

            // Verify
            String absolutePath = directory.toAbsolutePath().toString();
            String expectMessage = i18n("指定されたディレクトリは存在しません。[" + absolutePath + "]",
                    "The specified directory does not exist. [" + absolutePath + "]");
            assertThat(thrown.getMessage(), is(expectMessage));
        });
    }

    @DisplayName("hoge")
    @Test
    public void 作業ディレクトリをFileで指定_null() throws Exception {
        msdos(() -> {
            // Setup
            File directory = null;

            // Exercise
            expectThrows(NullPointerException.class, () -> {
                command("cd").directory(directory).execute();
            });
        });
    }

    @Test
    public void 作業ディレクトリをFileで指定_絶対パスに変換時にエラー() throws Exception {
        // SetUp
        File directory = mock(File.class);
        IOException cause = new IOException();
        when(directory.getCanonicalFile()).thenThrow(cause);

        // Exercise
        UncheckedIOException thrown = expectThrows(UncheckedIOException.class, () -> {
            command("cd").directory(directory).execute();
        });

        // Verify
        assertThat(thrown.getCause(), is(cause));
    }

    // TODO でっかい出力
}
