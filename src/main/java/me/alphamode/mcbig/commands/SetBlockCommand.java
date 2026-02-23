package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.alphamode.mcbig.commands.arguments.BlockPosArgument;
import me.alphamode.mcbig.commands.arguments.ItemArgument;
import me.alphamode.mcbig.world.phys.BigVec3i;
import net.minecraft.world.ItemInstance;

public class SetBlockCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("setblock")
                        .then(
                                Commands.argument("block", new ItemArgument())
                                        .then(
                                                Commands.argument("location", new BlockPosArgument())
                                                        .executes(context -> {
                                                            ItemInstance item = context.getArgument("block", ItemInstance.class);

                                                            BigVec3i pos = BlockPosArgument.getBlockPos(context, "location");

                                                            context.getSource().getEntity().level.setTileAndData(pos.x(), pos.y(), pos.z(), item.id, item.getAuxValue());

                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                        )
                        )
        );
    }
}
