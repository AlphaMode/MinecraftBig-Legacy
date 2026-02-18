package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.alphamode.mcbig.extensions.PlayerExtension;

public class FlyCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("fly").executes(context -> {
                    if (context.getSource().getEntity() instanceof PlayerExtension player) {
                        player.setCanFly(!player.canFly());
                        context.getSource().sendMessage("Toggled flight: " + player.canFly());
                    }

                    return Command.SINGLE_SUCCESS;
                })
        );
    }
}
