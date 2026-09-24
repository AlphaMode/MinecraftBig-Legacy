package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.alphamode.mcbig.extensions.CommandPlayerExtension;
import net.minecraft.world.entity.Entity;

public class NoclipCommand {
    public static <S extends CommandSource> void register(CommandDispatcher<S> dispatcher) {
        dispatcher.register(
                Commands.<S>literal("noclip")
                        .executes(context -> {
                            Entity entity = context.getSource().getEntity();
                            if (entity instanceof CommandPlayerExtension player) {
                                player.setNoclip(!player.canNoclip());
                                context.getSource().sendMessage("Toggled noclip: " + player.canNoclip());
                            }

                            return Command.SINGLE_SUCCESS;
                        })
        );
    }
}
