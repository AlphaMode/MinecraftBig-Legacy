package me.alphamode.mcbig.extensions.features.commands;

import me.alphamode.mcbig.client.commands.CommandHistory;

public interface ChatMinecraftExtension {
    default CommandHistory commandHistory() {
        throw new UnsupportedOperationException();
    }
}
