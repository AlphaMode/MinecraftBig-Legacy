package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.alphamode.mcbig.util.ScreenUtil;

public class SeedCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("seed")
                        .then(
                                Commands.literal("copy")
                                        .executes(context -> {
                                            long seed = context.getSource().getEntity().level.getSeed();
                                            ScreenUtil.setClipboard(Long.toString(seed));
                                            context.getSource().sendMessage("Copied seed to clipboard");
                                            return Command.SINGLE_SUCCESS;
                                        })
                        ).executes(context -> {
                            context.getSource().sendMessage("Seed: §a" + context.getSource().getEntity().level.getSeed());
                            return Command.SINGLE_SUCCESS;
                        })
        );
    }
}
