package jp.ambrosoli.salmon.command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

import jp.ambrosoli.salmon.command.CommandState.Status;
import jp.ambrosoli.salmon.event.EventHandler;

/**
 * <p>
 * コマンドの実行条件を組み立てるビルダーインタフェースです。
 * </p>
 *
 * @author willard379
 * @since 0.1.0
 *
 */
public interface CommandBuilder {

    /**
     * <p>
     * コマンドの実行を行います。
     * </p>
     *
     * @return {@link CommandState}
     * @throws IOException
     * @throws InterruptedException
     */
    CommandState execute() throws IOException, InterruptedException;

    /**
     * <p>
     * コマンドのオプションを指定します。
     * </p>
     *
     * @param options
     *            オプション
     * @return {@link CommandBuilder}
     */
    CommandBuilder options(Collection<String> options);

    /**
     * <p>
     * コマンドのオプションを指定します。
     * </p>
     *
     * @param options
     *            オプション
     * @return {@link CommandBuilder}
     */
    CommandBuilder options(String... options);

    /**
     * <p>
     * コマンドの状態が{@link Status#READY}に遷移した時に呼び出されるイベントハンドラを設定します。
     * </p>
     *
     * @param handler
     *            イベントハンドラ
     * @return {@link CommandBuilder}
     */
    CommandBuilder onReady(EventHandler<CommandState> handler);

    /**
     * <p>
     * コマンドの状態が{@link Status#RUNNING}に遷移した時に呼び出されるイベントハンドラを設定します。
     * </p>
     *
     * @param handler
     *            イベントハンドラ
     * @return {@link CommandBuilder}
     */
    CommandBuilder onRunning(EventHandler<CommandState> handler);

    /**
     * <p>
     * コマンドの状態が{@link Status#SUCCEEDED}に遷移した時に呼び出されるイベントハンドラを設定します。
     * </p>
     *
     * @param handler
     *            イベントハンドラ
     * @return {@link CommandBuilder}
     */
    CommandBuilder onSucceeded(EventHandler<CommandState> handler);

    /**
     * <p>
     * コマンドの状態が{@link Status#FAILED}に遷移した時に呼び出されるイベントハンドラを設定します。
     * </p>
     *
     * @param handler
     *            イベントハンドラ
     * @return {@link CommandBuilder}
     */
    CommandBuilder onFailed(EventHandler<CommandState> handler);

    /**
     * <p>
     * コマンドの状態が{@link Status#ERROR}に遷移した時に呼び出されるイベントハンドラを設定します。
     * </p>
     *
     * @param handler
     *            イベントハンドラ
     * @return {@link CommandBuilder}
     */
    CommandBuilder onError(EventHandler<CommandState> handler);

    /**
     * <p>
     * コマンドの状態が{@link Status#CANCELLED}に遷移した時に呼び出されるイベントハンドラを設定します。
     * </p>
     *
     * @param handler
     *            イベントハンドラ
     * @return {@link CommandBuilder}
     */
    CommandBuilder onCancelled(EventHandler<CommandState> handler);

    /**
     * <p>
     * コマンドの実行が完了した時に呼び出されるイベントハンドラを設定します。
     * </p>
     *
     * @param handler
     *            イベントハンドラ
     * @return {@link CommandBuilder}
     */
    CommandBuilder onDone(EventHandler<CommandState> handler);

    /**
     * <p>
     * コマンドの成否を判定するロジックを設定します。
     * </p>
     *
     * @param condition
     *            コマンドの成否を判定するロジック
     * @return {@link CommandBuilder}
     */
    CommandBuilder successCondition(IntPredicate condition);

    /**
     * <p>
     * コマンドのタイムアウト時間を設定します。
     * </p>
     *
     * @param milliseconds
     *            待機する最長時間(ミリ秒)
     * @return {@link CommandBuilder}
     */
    CommandBuilder timeout(final long milliseconds);

    /**
     * <p>
     * コマンドのタイムアウト時間を設定します。
     * </p>
     *
     * @param time
     *            待機する最長時間
     * @param unit
     *            引数の時間単位
     * @return {@link CommandBuilder}
     */
    CommandBuilder timeout(final long time, final TimeUnit unit);

    /**
     * <p>
     * コマンドの作業ディレクトリを設定します。
     * </p>
     *
     * @param directory
     *            作業ディレクトリ
     * @return {@link CommandBuilder}
     */
    CommandBuilder directory(final String directory);

    /**
     * <p>
     * コマンドの作業ディレクトリを設定します。
     * </p>
     *
     * @param directory
     *            作業ディレクトリ
     * @return {@link CommandBuilder}
     */
    CommandBuilder directory(final Path directory);

    /**
     * <p>
     * コマンドの作業ディレクトリを設定します。
     * </p>
     *
     * @param directory
     *            作業ディレクトリ
     * @return {@link CommandBuilder}
     */
    CommandBuilder directory(final File directory);

    /**
     * <p>
     * 環境変数を設定するロジックを設定します。
     * </p>
     *
     * @param consumer
     *            環境変数を設定するロジック
     * @return {@link CommandBuilder}
     */
    CommandBuilder environment(Consumer<Map<String, String>> consumer);

    /**
     * <p>
     * コマンドを非同期で実行するかどうかを示すフラグを設定します。
     * </p>
     *
     * @param async
     *            コマンドを非同期で実行する場合{@code true}、その他の場合{@code false}
     * @return {@link CommandBuilder}
     */
    CommandBuilder async(boolean async);

    /**
     * <p>
     * 標準出力のリダイレクト先を設定します。
     * </p>
     *
     * @param redirect
     *            標準出力のリダイレクト先
     * @return {@link CommandBuilder}
     */
    CommandBuilder redirectStdout(File redirect);

    /**
     * <p>
     * 標準エラー出力のリダイレクト先を設定します。
     * </p>
     *
     * @param redirect
     *            標準エラー出力のリダイレクト先
     * @return {@link CommandBuilder}
     */
    CommandBuilder redirectStderr(File redirect);

    /**
     * <p>
     * 標準出力を/dev/nullにリダイレクトします。
     * </p>
     *
     * @return {@link CommandBuilder}
     */
    CommandBuilder redirectStdoutToDevNull();

    /**
     * <p>
     * 標準エラー出力を/dev/nullにリダイレクトします。
     * </p>
     *
     * @return {@link CommandBuilder}
     */
    CommandBuilder redirectStderrToDevNull();

}