package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.alphamode.mcbig.commands.arguments.ItemArgument;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;

public class GiveCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("give")
                        .then(
                                Commands.argument("item", new ItemArgument())
                                        .executes(context -> {
                                            if (context.getSource().getEntity() instanceof Player player) {
                                                player.inventory.add(context.getArgument("item", ItemInstance.class));
                                            }
                                            return Command.SINGLE_SUCCESS;
                                        })
                        )
        );
    }
}
