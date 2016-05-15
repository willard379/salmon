package jp.ambrosoli.salmon.command;

/**
 * <p>
 * {@link CommandExecutor}のインスタンスを生成するファクトリです。
 * </p>
 *
 * <p>
 * このインタフェースの実装クラスが生成する{@link CommandExecutor}を{@link CommandBuilder#execute()}
 * で使用するには、 このインタフェースの実装クラスのインスタンスを
 * {@link CommandExecutorRegistry#registerFactory(CommandExecutorFactory)}
 * で登録してください。
 * </p>
 *
 * @author willard379
 * @since 0.1.0
 *
 */
public interface CommandExecutorFactory {

    /**
     * <p>
     * このインタフェースの実装クラスが生成する{@link CommandExecutor}に適用されるデフォルトのプライオリティです。
     * </p>
     */
    int DEFAULT_PRIORITY = 1;

    /**
     * <p>
     * このインタフェースの実装クラスが生成する{@link CommandExecutor}が使用されるプライオリティを返します。
     * </p>
     *
     * <p>
     * カスタムのファクトリはデフォルトでプライオリティ値:1で登録されます。 プリセットのファクトリはプライオリティ値:10で登録されているため、
     * デフォルトではカスタムのファクトリはプリセットのファクトリよりも優先して使用されます。
     * </p>
     *
     * @return プライオリティ
     */
    default int getPriority() {
        return DEFAULT_PRIORITY;
    }

    /**
     * <p>
     * {@link CommandBuilder#execute()}が呼び出される環境で、このインタフェースの実装クラスが生成する
     * {@link CommandExecutor}の使用条件を満たすかどうかを返します。
     * </p>
     *
     * @return 使用条件を満たす場合{@code true}、満たさない場合{@code false}
     */
    boolean isSupported();

    /**
     * <p>
     * {@link CommandExecutor}を生成します。
     * </p>
     *
     * @return {@link CommandExecutor}
     */
    CommandExecutor create();
}
