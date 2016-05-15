package jp.ambrosoli.salmon.config;

import jp.ambrosoli.salmon.command.CommandExecutorRegistry;
import jp.ambrosoli.salmon.command.msdos.MSDosCommandExecutorFactory;

public class SalmonInitializer {

    private static SalmonInitializer initializer = new SalmonInitializer();

    private boolean initialized;

    private SalmonInitializer() {
    }

    public static void initialize() {
        initializer.runOnlyOnce();
    }

    private void runOnlyOnce() {
        if (initialized) {
            return;
        }
        SalmonConfig.initialize();
        registCommandExecutorFactory();
        initialized = true;
    }

    private void registCommandExecutorFactory() {
        CommandExecutorRegistry.registerFactory(new MSDosCommandExecutorFactory());
    }
}
