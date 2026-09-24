package me.alphamode.mcbig.client.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.alphamode.mcbig.commands.Commands;

public class ClientCommands {
    public static LiteralArgumentBuilder<ClientCommandSource> literal(String literal) {
        return Commands.literal(literal);
    }

    public static <T> RequiredArgumentBuilder<ClientCommandSource, T> argument(final String name, final ArgumentType<T> type) {
        return Commands.argument(name, type);
    }
}
