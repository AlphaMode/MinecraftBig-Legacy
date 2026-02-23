package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.alphamode.mcbig.commands.arguments.BlockPosArgument;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.phys.BigVec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.LevelData;

public class SetSpawnCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("setspawn")
                        .executes(context -> {
                            Entity entity = context.getSource().getEntity();
                            return setSpawn(context, new BigVec3i(BigMath.floor(((BigEntityExtension) entity).getX()), Mth.floor(entity.y), BigMath.floor(((BigEntityExtension) entity).getZ())));
                        })
                        .then(
                                Commands.argument("location", new BlockPosArgument())
                                        .executes(context -> {
                                            return setSpawn(context, BlockPosArgument.getBlockPos(context, "location"));
                                        })
                        )
        );
    }

    private static int setSpawn(CommandContext<CommandSource> context, BigVec3i pos) {
        Entity entity = context.getSource().getEntity();
        LevelData data = entity.level.getLevelData();
        data.setBigSpawnX(pos.x());
        data.setSpawnY(pos.y());
        data.setBigSpawnZ(pos.z());
        context.getSource().sendMessage("Set spawn to x: " + pos.x() + " y: " + pos.y() + " z: " + pos.z());
        return Command.SINGLE_SUCCESS;
    }
}
