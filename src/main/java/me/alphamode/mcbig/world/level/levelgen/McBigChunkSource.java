package me.alphamode.mcbig.world.level.levelgen;

import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;

import java.math.BigInteger;

public interface McBigChunkSource extends ChunkSource {
    @Override
    default boolean hasChunk(int x, int z) {
        return hasChunk(BigInteger.valueOf(x), BigInteger.valueOf(z));
    }

    @Override
    default LevelChunk getChunk(int x, int z) {
        return getChunk(BigInteger.valueOf(x), BigInteger.valueOf(z));
    }

    @Override
    default LevelChunk loadChunk(int x, int z) {
        return loadChunk(BigInteger.valueOf(x), BigInteger.valueOf(z));
    }

    @Override
    default void postProcess(ChunkSource generator, int x, int z) {
        postProcess(generator, BigInteger.valueOf(x), BigInteger.valueOf(z));
    }

    @Override
    boolean hasChunk(BigInteger x, BigInteger z);

    @Override
    LevelChunk getChunk(BigInteger x, BigInteger z);

    @Override
    LevelChunk loadChunk(BigInteger x, BigInteger z);

    @Override
    void postProcess(ChunkSource generator, BigInteger x, BigInteger z);
}
