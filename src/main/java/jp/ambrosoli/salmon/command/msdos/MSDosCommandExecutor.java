package jp.ambrosoli.salmon.command.msdos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.ambrosoli.salmon.command.CommandParameter;
import jp.ambrosoli.salmon.command.internal.AbstractCommandExecutor;
import jp.ambrosoli.salmon.config.SalmonConfig;

class MSDosCommandExecutor extends AbstractCommandExecutor {

    @Override
    protected void setCommand(final ProcessBuilder processBuilder, final CommandParameter parameter) {
        List<String> command = new ArrayList<>();
        if (SalmonConfig.isAutoMSDos()) {
            command.add("cmd"); //$NON-NLS-1$
            command.add("/c"); //$NON-NLS-1$
        }

        command.add(parameter.getCommand());
        Collection<String> options = parameter.getOptions();
        if (options != null) {
            command.addAll(options);
        }

        processBuilder.command(command);
    }

}
