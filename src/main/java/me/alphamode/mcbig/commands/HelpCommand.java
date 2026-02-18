package me.alphamode.mcbig.commands;

import com.mojang.brigadier.CommandDispatcher;

public class HelpCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("help").executes(context -> {
            CommandSource source = context.getSource();
            var usage = dispatcher.getSmartUsage(dispatcher.getRoot(), source);

            for (String line : usage.values()) {
                source.sendMessage(line);
            }

            return usage.size();
        }));
    }
}
