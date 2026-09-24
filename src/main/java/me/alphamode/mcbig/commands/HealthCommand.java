package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.world.entity.Mob;

public class HealthCommand {
    public static <S extends CommandSource> void register(CommandDispatcher<S> dispatcher) {
        dispatcher.register(
                Commands.<S>literal("health")
                        .then(
                                Commands.<S>literal("heal")
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
                                Commands.<S, Integer>argument("health", IntegerArgumentType.integer(0, 20))
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
