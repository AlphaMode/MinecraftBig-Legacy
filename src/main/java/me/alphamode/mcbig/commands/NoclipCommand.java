package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.alphamode.mcbig.extensions.PlayerExtension;
import net.minecraft.world.entity.Entity;

public class NoclipCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("noclip")
                        .executes(context -> {
                            Entity entity = context.getSource().getEntity();
                            if (entity instanceof PlayerExtension player) {
                                entity.noPhysics = !entity.noPhysics;
                                player.setNoclip(!player.canNoclip());
                                context.getSource().sendMessage("Toggled noclip: " + player.canFly());
                            }

                            return Command.SINGLE_SUCCESS;
                        })
        );
    }
}
