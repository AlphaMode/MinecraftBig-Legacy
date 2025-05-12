package me.alphamode.mcbig.level.tile;

import me.alphamode.mcbig.extensions.BigLiquidTileExtension;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.LiquidTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.Vec3;

import java.math.BigInteger;

public class LiquidUtil {
    public static double getSlopeAngle(LevelSource level, BigInteger x, int y, BigInteger z, Material material) {
        Vec3 var5 = null;
        if (material == Material.WATER) {
            var5 = ((BigLiquidTileExtension) Tile.FLOWING_WATER).getFlow(level, x, y, z);
        }

        if (material == Material.LAVA) {
            var5 = ((BigLiquidTileExtension)Tile.FLOWING_LAVA).getFlow(level, x, y, z);
        }

        return var5.x == 0.0 && var5.z == 0.0 ? -1000.0 : Math.atan2(var5.z, var5.x) - (Math.PI / 2);
    }
}
