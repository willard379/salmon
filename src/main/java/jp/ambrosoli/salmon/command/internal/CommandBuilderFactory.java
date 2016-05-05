package jp.ambrosoli.salmon.command.internal;

import jp.ambrosoli.salmon.command.CommandBuilder;

public class CommandBuilderFactory {

    public static CommandBuilder create(final String command) {
        return new CommandBuilderImpl(command);
    }
}
