package me.alphamode.mcbig.level.chunk.storage;

import me.alphamode.mcbig.level.chunk.BigChunkPos;
import net.minecraft.world.level.Level;

import java.util.concurrent.CompletableFuture;

public interface EntityStorage {
    CompletableFuture<ChunkEntities> load(Level level, BigChunkPos pos);

    void save(Level level, ChunkEntities entities);
}
