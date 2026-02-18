package me.alphamode.mcbig.extensions.tiles;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;

import java.math.BigInteger;

public interface BigFireTileExtension {
    default boolean canBurn(LevelSource level, BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default int getFlammability(Level level, BigInteger x, int y, BigInteger z, int lastOdds) {
        throw new UnsupportedOperationException();
    }
}
