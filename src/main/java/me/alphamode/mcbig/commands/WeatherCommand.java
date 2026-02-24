package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

public class WeatherCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("weather")
                        .then(
                                Commands.literal("clear")
                                        .executes(context -> {
                                            context.getSource().getEntity().level.stopWeather();
                                            return Command.SINGLE_SUCCESS;
                                        })
                        )
                        .then(
                                Commands.literal("rain")
                                        .executes(context -> {
                                            context.getSource().getEntity().level.getLevelData().setRaining(true);
                                            return Command.SINGLE_SUCCESS;
                                        })
                        )
        );
    }
}
