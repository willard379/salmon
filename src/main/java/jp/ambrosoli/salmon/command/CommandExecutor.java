package jp.ambrosoli.salmon.command;

import java.io.IOException;

/**
 * <p>
 * コマンドの実行を行うインタフェースです
 * </p>
 *
 * @author willard379
 * @since 0.1.0
 *
 */
public interface CommandExecutor {

    /**
     * <p>
     * コマンドの実行を行います。
     * </p>
     *
     * @param parameter
     *            パラメータ
     * @return {@link CommandState}
     * @throws IOException
     * @throws InterruptedException
     */
    CommandState execute(CommandParameter parameter) throws IOException, InterruptedException;

}