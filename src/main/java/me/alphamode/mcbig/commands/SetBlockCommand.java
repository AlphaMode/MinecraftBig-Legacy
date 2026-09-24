package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.alphamode.mcbig.commands.arguments.BlockPosArgument;
import me.alphamode.mcbig.commands.arguments.ItemArgument;
import me.alphamode.mcbig.util.BigCoordinates;
import me.alphamode.mcbig.world.phys.BigVec3i;
import net.minecraft.world.item.ItemInstance;

public class SetBlockCommand {
    public static <S extends CommandSource> void register(CommandDispatcher<S> dispatcher) {
        dispatcher.register(
                Commands.<S>literal("setblock")
                        .then(
                                Commands.<S, ItemInstance>argument("block", new ItemArgument())
                                        .then(
                                                Commands.<S, BigCoordinates.BigIntegerCoordinates>argument("location", new BlockPosArgument())
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
