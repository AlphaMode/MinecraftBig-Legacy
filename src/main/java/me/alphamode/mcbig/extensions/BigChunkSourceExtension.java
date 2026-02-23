package me.alphamode.mcbig.extensions;

import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;

import java.math.BigInteger;

public interface BigChunkSourceExtension {
    default boolean hasChunk(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default LevelChunk getChunk(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default LevelChunk loadChunk(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void postProcess(ChunkSource generator, BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }
}
