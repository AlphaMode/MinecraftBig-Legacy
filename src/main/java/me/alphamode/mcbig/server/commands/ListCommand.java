package me.alphamode.mcbig.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

public class ListCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                ServerCommands.literal("list").executes(context -> {
                    ServerCommandSource source = context.getSource();
                    source.info("Connected players: " + source.getServer().players.getPlayerNames());
                    return Command.SINGLE_SUCCESS;
                })
        );
    }
}
