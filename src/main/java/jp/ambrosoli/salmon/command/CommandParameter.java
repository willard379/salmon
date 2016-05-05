package jp.ambrosoli.salmon.command;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

import jp.ambrosoli.salmon.command.CommandState.Status;
import jp.ambrosoli.salmon.event.EventHandler;

/**
 * <p>
 * {@link CommandBuilder}で組み立てたコマンドを表すインタフェースです。
 * </p>
 *
 * @author willard379
 * @since 0.1.0
 *
 */
public interface CommandParameter {

    /**
     * <p>
     * コマンド名を返します。
     * </p>
     *
     * @return コマンド名
     */
    String getCommand();

    /**
     * <p>
     * コマンドのオプションを返します。
     * </p>
     *
     * @return コマンドのオプション
     */
    Collection<String> getOptions();

    /**
     * <p>
     * コマンドの状態が遷移した時に呼び出されるイベントハンドラを返します。
     * </p>
     *
     * @return
     */
    Map<Status, List<EventHandler<CommandState>>> getHandlers();

    /**
     * <p>
     * コマンドの実行が終了した時に呼び出されるイベントハンドラを返します。
     * </p>
     *
     * @return イベントハンドラ
     */
    List<EventHandler<CommandState>> getDoneHandlers();

    /**
     * <p>
     * コマンドの成否を判定するロジックを返します。
     * </p>
     *
     * @return コマンドの成否を判定するロジック
     */
    IntPredicate getSuccessCondition();

    /**
     * <p>
     * コマンドのタイムアウト時間を返します。
     * </p>
     *
     * @return タイムアウト時間
     */
    long getTimeout();

    /**
     * <p>
     * コマンドのタイムアウトの時間単位を返します。
     * </p>
     *
     * @return 時間単位
     */
    TimeUnit getTimeoutUnit();

    /**
     * <p>
     * コマンドの作業ディレクトリを返します。
     * </p>
     *
     * @return 作業ディレクトリ
     */
    File getDirectory();

    /**
     * <p>
     * 環境変数を設定するロジックを返します。
     * </p>
     *
     * @return 環境変数を設定するロジック
     */
    Consumer<Map<String, String>> getEnvironment();

    /**
     * <p>
     * コマンドを非同期で実行するかどうかを示すフラグを返します。
     * </p>
     *
     * @return コマンドを非同期で実行する場合{@code true}、その他の場合{@code false}
     */
    boolean isAsync();

    /**
     * <p>
     * 標準出力のリダイレクト先に設定されているファイルを返します。
     * </p>
     *
     * @return 標準出力のリダイレクト先に設定されているファイル
     */
    File getRedirectOutput();

    /**
     * <p>
     * 標準エラー出力のリダイレクト先に設定されているファイルを返します。
     * </p>
     *
     * @return 標準エラー出力のリダイレクト先に設定されているファイル
     */
    File getRedirectError();
}
