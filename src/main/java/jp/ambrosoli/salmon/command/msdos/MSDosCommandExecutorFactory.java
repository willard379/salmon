package jp.ambrosoli.salmon.command.msdos;

import jp.ambrosoli.salmon.command.CommandExecutor;
import jp.ambrosoli.salmon.command.CommandExecutorFactory;
import jp.ambrosoli.salmon.utils.PlatformUtil;

public class MSDosCommandExecutorFactory implements CommandExecutorFactory {

    @Override
    public boolean isSupported() {
        return PlatformUtil.isWindows();
    }

    @Override
    public CommandExecutor create() {
        return new MSDosCommandExecutor();
    }

}
