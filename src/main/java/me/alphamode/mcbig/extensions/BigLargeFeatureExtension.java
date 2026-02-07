package me.alphamode.mcbig.extensions;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;

import java.math.BigInteger;

public interface BigLargeFeatureExtension {
    default void apply(ChunkSource source, Level level, BigInteger x, BigInteger z, byte[] tiles) {
        throw new UnsupportedOperationException();
    }

    default void addFeature(Level level, BigInteger minX, BigInteger minZ, BigInteger maxX, BigInteger maxZ, byte[] tiles) {
        throw new UnsupportedOperationException();
    }
}
