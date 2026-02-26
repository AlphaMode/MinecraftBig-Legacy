package me.alphamode.mcbig.extensions.tiles;

import net.minecraft.world.level.Level;

import java.math.BigInteger;

public interface BigCropTileExtension {
    default void growCropsToMax(Level level, BigInteger x, int y, BigInteger z) {
        level.setData(x, y, z, 7);
    }
}
