package me.alphamode.mcbig.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

public class OpCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                ServerCommands.literal("op")
                        .then(
                                ServerCommands.argument("player", StringArgumentType.word())
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            String player = context.getArgument("player", String.class);
                                            source.getPlayers().op(player);
                                            source.success(source.getName(), "Opping " + player);
                                            source.getPlayers().sendMessage(player, "§eYou are now op!");
                                            return Command.SINGLE_SUCCESS;
                                        })
                        )
        );

    }
}
