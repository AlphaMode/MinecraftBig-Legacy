package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

public class WeatherCommand {
    public static <S extends CommandSource> void register(CommandDispatcher<S> dispatcher) {
        dispatcher.register(
                Commands.<S>literal("weather")
                        .then(
                                Commands.<S>literal("clear")
                                        .executes(context -> {
                                            context.getSource().getEntity().level.stopWeather();
                                            return Command.SINGLE_SUCCESS;
                                        })
                        )
                        .then(
                                Commands.<S>literal("rain")
                                        .executes(context -> {
                                            context.getSource().getEntity().level.getLevelData().setRaining(true);
                                            return Command.SINGLE_SUCCESS;
                                        })
                        )
        );
    }
}
