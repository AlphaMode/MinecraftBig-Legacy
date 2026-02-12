package me.alphamode.mcbig.extensions;

import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.io.IOException;
import java.math.BigInteger;

public interface BigChunkStorageExtension {
    default BigLevelChunk load(Level level, BigInteger x, BigInteger z) throws IOException {
        throw new UnsupportedOperationException();
    }
}
