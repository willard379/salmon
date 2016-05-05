package jp.ambrosoli.salmon;

import jp.ambrosoli.salmon.command.CommandBuilder;
import jp.ambrosoli.salmon.command.internal.CommandBuilderFactory;
import jp.ambrosoli.salmon.config.SalmonInitializer;

/**
 * <p>
 * コマンドの実行を行います。
 * </p>
 *
 * @author willard379
 * @since 0.1.0
 *
 */
public class Salmon {

    static {
        SalmonInitializer.initialize();
    }

    /**
     * <p>
     * コマンドの組み立てを開始します。
     * </p>
     *
     * @param command
     *            コマンド名
     * @return コマンドの組み立てを行うビルダー
     */
    public static CommandBuilder command(final String command) {
        return CommandBuilderFactory.create(command);
    }

}
