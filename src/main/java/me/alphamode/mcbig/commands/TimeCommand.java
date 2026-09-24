package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class TimeCommand {
    public static <S extends CommandSource> void register(CommandDispatcher<S> dispatcher) {
        dispatcher.register(
                Commands.<S>literal("time")
                        .then(
                                Commands.<S>literal("set")
                                        .then(Commands.<S>literal("day").executes(context -> setTime(context, 1000)))
                                        .then(Commands.<S>literal("noon").executes(context -> setTime(context, 6000)))
                                        .then(Commands.<S>literal("night").executes(context -> setTime(context, 13000)))
                                        .then(Commands.<S>literal("midnight").executes(context -> setTime(context, 18000)))
                        )
        );
    }

    private static <S extends CommandSource> int setTime(CommandContext<S> context, int time) throws CommandSyntaxException {
        context.getSource().getEntity().level.setTime(time);
        return Command.SINGLE_SUCCESS;
    }
}
