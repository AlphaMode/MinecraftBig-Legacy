package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.alphamode.mcbig.commands.arguments.DimensionArgument;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.Dimension;

import java.math.BigDecimal;

public class DimensionCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("dimensiontp")
                        .then(
                                Commands.argument("dimension", new DimensionArgument())
                                        .executes(context -> moveToDimension(context.getSource(), context.getArgument("dimension", Integer.class)))
                        )
        );
    }

    private static int moveToDimension(CommandSource source, int dimension) {
        if (!(source.getEntity() instanceof Player player))
            return 0;

//        if (player.dimension == dimension) {
//            return Command.SINGLE_SUCCESS;
//        }

        player.level.removeEntity(player);
        player.removed = false;
        if (player.isBigMovementEnabled()) {
            BigDecimal x = ((BigEntityExtension) player).getX();
            BigDecimal z = ((BigEntityExtension) player).getZ();

            Level level = new Level(player.level, Dimension.getNew(dimension));
            ((Minecraft) FabricLoader.getInstance().getGameInstance()).setLevel(level, "Traveling to dimension: " + dimension, player);

            player.level = level;
            if (player.isAlive()) {
                ((BigEntityExtension) player).moveTo(x, player.y, z, player.yRot, player.xRot);
                level.tick(player, false);
            }
            return Command.SINGLE_SUCCESS;
        } else {
            double x = player.x;
            double z = player.z;

            Level level = new Level(player.level, Dimension.getNew(dimension));
            ((Minecraft) FabricLoader.getInstance().getGameInstance()).setLevel(level, "Traveling to dimension: " + dimension, player);

            player.level = level;
            if (player.isAlive()) {
                player.moveTo(x, player.y, z, player.yRot, player.xRot);
                level.tick(player, false);
            }
            return Command.SINGLE_SUCCESS;
        }
    }
}
