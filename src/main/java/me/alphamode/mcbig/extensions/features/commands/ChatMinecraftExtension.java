package me.alphamode.mcbig.extensions.features.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.alphamode.mcbig.client.commands.ClientCommandSource;
import me.alphamode.mcbig.client.commands.CommandHistory;
import me.alphamode.mcbig.commands.Commands;

public interface ChatMinecraftExtension {
    default CommandHistory commandHistory() {
        throw new UnsupportedOperationException();
    }

    default Commands<ClientCommandSource> getCommands() {
        throw new UnsupportedOperationException();
    }

    default void setDispatcher(CommandDispatcher<ClientCommandSource> commands) {
        throw new UnsupportedOperationException();
    }
}
