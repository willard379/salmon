package jp.ambrosoli.salmon.command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jp.ambrosoli.salmon.messages.Messages;

/**
 * <p>
 * {@link CommandBuilder#execute()}が使用する{@link CommandExecutor}の種類を制御するためのクラスです。
 * </p>
 *
 * @author willard379
 * @since 0.1.0
 *
 */
public class CommandExecutorRegistry {

    /**
     * <p>
     * 登録された{@ CommandExecutorFactory}を保持するレジストリです。
     * </p>
     */
    private static final List<CommandExecutorFactory> registry = new ArrayList<>();

    /**
     * <p>
     * {@link CommandExecutorFactory}を登録します。
     * </p>
     *
     * @param factory
     *            {@link CommandExecutorFactory}のインスタンス
     */
    public static void registerFactory(final CommandExecutorFactory... factories) {
        for (CommandExecutorFactory factory : factories) {
            registry.add(factory);
        }
        registry.sort(Comparator.comparing(CommandExecutorFactory::getPriority));
    }

    /**
     * <p>
     * 使用条件を満たす、もっともプライオリティ値の高い{@link CommandExecutor}インスタンスを生成して返します。
     * </p>
     *
     * @return {@link CommandExecutor}のインスタンス
     */
    public static CommandExecutor get() {
        return registry.stream().filter(CommandExecutorFactory::isSupported).map(CommandExecutorFactory::create)
                .findFirst().orElseThrow(() -> new IllegalStateException(
                        Messages.getString("CommandExecutorRegistry.executor.not.found"))); //$NON-NLS-1$
    }
}
