package me.alphamode.mcbig.commands;

import com.mojang.brigadier.CommandDispatcher;

public class HelpCommand {
    public static <S extends CommandSource> void register(CommandDispatcher<S> dispatcher) {
        dispatcher.register(Commands.<S>literal("help").executes(context -> {
            S source = context.getSource();
            var usage = dispatcher.getSmartUsage(dispatcher.getRoot(), source);

            for (String line : usage.values()) {
                source.sendMessage(line);
            }

            return usage.size();
        }));
    }
}
