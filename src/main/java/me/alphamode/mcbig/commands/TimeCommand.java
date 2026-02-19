package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class TimeCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("time")
                        .then(
                                Commands.literal("set")
                                        .then(Commands.literal("day").executes(context -> setTime(context, 1000)))
                                        .then(Commands.literal("noon").executes(context -> setTime(context, 6000)))
                                        .then(Commands.literal("night").executes(context -> setTime(context, 13000)))
                                        .then(Commands.literal("midnight").executes(context -> setTime(context, 18000)))
                        )
        );
    }

    private static int setTime(CommandContext<CommandSource> context, int time) throws CommandSyntaxException {
        context.getSource().getEntity().level.setTime(time);
        return Command.SINGLE_SUCCESS;
    }
}
