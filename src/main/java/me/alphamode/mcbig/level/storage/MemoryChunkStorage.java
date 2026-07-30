package me.alphamode.mcbig.level.storage;

import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkStorage;

import java.io.IOException;
import java.math.BigInteger;

public class MemoryChunkStorage implements ChunkStorage {
    @Override
    public LevelChunk load(Level level, int x, int z) throws IOException {
        return null;
    }

    @Override
    public void save(Level level, LevelChunk chunk) throws IOException {
    }

    @Override
    public void saveEntities(Level level, LevelChunk chunk) {
    }

    @Override
    public void tick() {
    }

    @Override
    public void flush() {
    }

    @Override
    public BigLevelChunk load(Level level, BigInteger x, BigInteger z) throws IOException {
        return null;
    }
}
