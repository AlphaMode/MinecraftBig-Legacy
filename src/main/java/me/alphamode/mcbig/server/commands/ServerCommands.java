package me.alphamode.mcbig.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.alphamode.mcbig.commands.Commands;

public class ServerCommands {
    public static LiteralArgumentBuilder<ServerCommandSource> literal(String literal) {
        return Commands.literal(literal);
    }

    public static <T> RequiredArgumentBuilder<ServerCommandSource, T> argument(final String name, final ArgumentType<T> type) {
        return Commands.argument(name, type);
    }

    public static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
        HelpCommand.register(dispatcher);
        StopCommand.register(dispatcher);
        ListCommand.register(dispatcher);
        SaveCommand.register(dispatcher);
        OpCommand.register(dispatcher);
        DeOpCommand.register(dispatcher);
        BanIPCommand.register(dispatcher);
        BanCommand.register(dispatcher);
        PardonCommand.register(dispatcher);
        KickCommand.register(dispatcher);
    }
}
