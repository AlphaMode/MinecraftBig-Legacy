package me.alphamode.mcbig.extensions.features.commands;

import me.alphamode.mcbig.commands.CommandSource;

public interface LocalPlayerExtension {
    default CommandSource getCommandSource() {
        throw new UnsupportedOperationException();
    }
}
