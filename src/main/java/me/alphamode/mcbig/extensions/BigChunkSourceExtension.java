package me.alphamode.mcbig.extensions;

import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;

import java.math.BigInteger;

public interface BigChunkSourceExtension {
    boolean hasChunk(BigInteger x, BigInteger z);

    LevelChunk getChunk(BigInteger x, BigInteger z);

    LevelChunk loadChunk(BigInteger x, BigInteger z);

    void postProcess(ChunkSource generator, BigInteger x, BigInteger z);
}
