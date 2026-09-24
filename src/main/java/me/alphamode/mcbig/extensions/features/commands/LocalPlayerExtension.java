package me.alphamode.mcbig.extensions.features.commands;

import me.alphamode.mcbig.client.commands.ClientCommandSource;

public interface LocalPlayerExtension {
    default ClientCommandSource getCommandSource() {
        throw new UnsupportedOperationException();
    }

    default void command(String command) {
        throw new UnsupportedOperationException();
    }
}
