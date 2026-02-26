package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import me.alphamode.mcbig.extensions.PlayerExtension;
import net.minecraft.world.entity.player.Player;

public class FlySpeedCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("flyspeed")
                        .then(
                                Commands.literal("set")
                                        .then(
                                                Commands.argument("speed", FloatArgumentType.floatArg())
                                                        .executes(context -> {
                                                            if (context.getSource().getEntity() instanceof PlayerExtension player) {
                                                                player.setFlySpeed(FloatArgumentType.getFloat(context, "speed"));
                                                            }
                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                        )
                        )
                        .then(
                                Commands.literal("get")
                                        .executes(context -> {
                                            if (context.getSource().getEntity() instanceof PlayerExtension player) {
                                                context.getSource().sendMessage("Fly speed: " + player.getFlySpeed());
                                            }
                                            return Command.SINGLE_SUCCESS;
                                        })
                        )
        );
    }
}
