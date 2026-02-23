package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.world.entity.Mob;

public class HealthCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("health")
                        .then(
                                Commands.literal("heal")
                                        .executes(context -> {
                                            if (context.getSource().getEntity() instanceof Mob mob) {
                                                mob.health = 20;
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            context.getSource().sendMessage("Not a mob");
                                            return 0;
                                        })
                        )
                        .then(
                                Commands.argument("health", IntegerArgumentType.integer(0, 20))
                                        .executes(context -> {
                                            if (context.getSource().getEntity() instanceof Mob mob) {
                                                mob.health = IntegerArgumentType.getInteger(context, "health");
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            context.getSource().sendMessage("Not a mob");
                                            return 0;
                                        })
                        )
        );
    }
}
