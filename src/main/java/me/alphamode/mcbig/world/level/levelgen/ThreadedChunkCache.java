package me.alphamode.mcbig.world.level.levelgen;

import it.unimi.dsi.fastutil.objects.*;
import me.alphamode.mcbig.level.chunk.BigChunkPos;
import me.alphamode.mcbig.level.chunk.BigEmptyLevelChunk;
import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkStorage;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadedChunkCache implements McBigChunkSource {
    private static final Executor THREAD_POOL = Executors.newSingleThreadExecutor();
    private BigEmptyLevelChunk emptyChunk;
    private final ChunkSource source;
    private final ChunkStorage storage;
    private final ObjectSet<BigChunkPos> toDrop = new ObjectOpenHashSet<>();
    private final Object2ObjectMap<BigChunkPos, LevelChunk> cache = new Object2ObjectOpenHashMap<>();
    private final List<LevelChunk> chunks = new ArrayList<>();
    private final Level level;

    public ThreadedChunkCache(Level level, ChunkStorage storage, ChunkSource source) {
        this.emptyChunk = new BigEmptyLevelChunk(level, new byte[32768], BigInteger.ZERO, BigInteger.ZERO);
        this.storage = storage;
        this.source = source;
        this.level = level;
    }

    @Override
    public boolean hasChunk(BigInteger x, BigInteger z) {
        return this.cache.containsKey(new BigChunkPos(x, z));
    }

    @Override
    public LevelChunk getChunk(BigInteger x, BigInteger z) {
        BigChunkPos pos = new BigChunkPos(x, z);
        LevelChunk chunk = this.cache.get(pos);
        return chunk == null ? this.loadChunk(x, z) : chunk;
    }

    @Override
    public LevelChunk loadChunk(BigInteger x, BigInteger z) {
        BigChunkPos pos = new BigChunkPos(x, z);
        this.toDrop.remove(pos);
        LevelChunk chunk = this.cache.get(pos);
        CompletableFuture<LevelChunk> chunkFuture = new CompletableFuture<>();
        if (chunk == null) {
            chunk = readChunk(x, z);
            if (chunk == null) {
                if (this.source == null) {
                    chunk = this.emptyChunk;
                    chunkFuture = CompletableFuture.completedFuture(chunk);
                } else {
                    chunkFuture = this.loadChunkFuture(x, z);
                }
            }

            this.cache.put(pos, chunk);
            this.chunks.add(chunk);

            try {
                return chunkFuture.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }

//            chunkFuture.whenComplete((levelChunk, throwable) -> {
//                if (levelChunk != null) {
//                    levelChunk.lightLava();
//                    levelChunk.load();
//                }
//
//                BigInteger xMinusOne = x.subtract(BigInteger.ONE);
//                BigInteger zMinusOne = z.subtract(BigInteger.ONE);
//
//                BigInteger xPlusOne = x.add(BigInteger.ONE);
//                BigInteger zPlusOne = z.add(BigInteger.ONE);
//
//                if (!levelChunk.terrainPopulated && this.hasChunk(xPlusOne, zPlusOne) && this.hasChunk(x, zPlusOne) && this.hasChunk(xPlusOne, z)) {
//                    this.postProcess(this, x, z);
//                }
//
//                if (this.hasChunk(xMinusOne, z)
//                        && !this.getChunk(xMinusOne, z).terrainPopulated
//                        && this.hasChunk(xMinusOne, zPlusOne)
//                        && this.hasChunk(x, zPlusOne)
//                        && this.hasChunk(xMinusOne, z)) {
//                    this.postProcess(this, xMinusOne, z);
//                }
//
//                if (this.hasChunk(x, zMinusOne)
//                        && !this.getChunk(x, zMinusOne).terrainPopulated
//                        && this.hasChunk(xPlusOne, zMinusOne)
//                        && this.hasChunk(x, zMinusOne)
//                        && this.hasChunk(xPlusOne, z)) {
//                    this.postProcess(this, x, zMinusOne);
//                }
//
//                if (this.hasChunk(xMinusOne, zMinusOne)
//                        && !this.getChunk(xMinusOne, zMinusOne).terrainPopulated
//                        && this.hasChunk(xMinusOne, zMinusOne)
//                        && this.hasChunk(x, zMinusOne)
//                        && this.hasChunk(xMinusOne, z)) {
//                    this.postProcess(this, xMinusOne, zMinusOne);
//                }
//            });
        }

        return chunk;
    }

    @Override
    public CompletableFuture<LevelChunk> loadChunkFuture(BigInteger x, BigInteger z) {
        BigChunkPos pos = new BigChunkPos(x, z);
        byte[] blocks = new byte[32768];
        LevelChunk lc = new BigLevelChunk(this.level, blocks, x, z);
        Arrays.fill(lc.skyLight.data, (byte)-1);
        this.cache.put(pos, lc);
        lc.loaded = true;
        return CompletableFuture.supplyAsync(() -> {
            LevelChunk newLc = this.source.loadChunk(x, z);
            System.arraycopy(newLc.blocks, 0, lc.blocks, 0, newLc.blocks.length);
            System.arraycopy(newLc.data.data, 0, lc.data.data, 0, newLc.data.data.length);
            System.arraycopy(newLc.blockLight.data, 0, lc.blockLight.data, 0, newLc.blockLight.data.length);
            System.arraycopy(newLc.skyLight.data, 0, lc.skyLight.data, 0, newLc.skyLight.data.length);
            return newLc;
        }, THREAD_POOL);
    }

    private BigLevelChunk readChunk(BigInteger x, BigInteger z) {
        if (this.storage == null) {
            return null;
        } else {
            try {
                BigLevelChunk chunk = this.storage.load(this.level, x, z);
                if (chunk != null) {
                    chunk.lastSaveTime = this.level.getTime();
                }

                return chunk;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public void postProcess(ChunkSource generator, BigInteger x, BigInteger z) {
        LevelChunk lc = this.getChunk(x, z);
        if (!lc.terrainPopulated) {
            lc.terrainPopulated = true;
            if (this.source != null) {
                this.source.postProcess(generator, x, z);
                lc.markUnsaved();
            }
        }
    }

    private void saveExtra(LevelChunk chunk) {
        if (this.storage != null) {
            try {
                this.storage.saveEntities(this.level, chunk);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveChunk(LevelChunk chunk) {
        if (this.storage != null) {
            try {
                chunk.lastSaveTime = this.level.getTime();
                this.storage.save(this.level, chunk);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean save(boolean force, ProgressListener listener) {
        int var3 = 0;

        for (int var4 = 0; var4 < this.chunks.size(); var4++) {
            LevelChunk var5 = this.chunks.get(var4);
            if (force && !var5.dontSave) {
                this.saveExtra(var5);
            }

            if (var5.shouldSave(force)) {
                this.saveChunk(var5);
                var5.unsaved = false;
                if (++var3 == 24 && !force) {
                    return false;
                }
            }
        }

        if (force) {
            if (this.storage == null) {
                return true;
            }

            this.storage.flush();
        }

        return true;
    }

    @Override
    public boolean tick() {
        for (int i = 0; i < 100; i++) {
            if (!this.toDrop.isEmpty()) {
                BigChunkPos pos = this.toDrop.iterator().next();
                LevelChunk lc = this.cache.get(pos);
                lc.unload();
                this.saveChunk(lc);
                this.saveExtra(lc);
                this.toDrop.remove(pos);
                this.cache.remove(pos);
                this.chunks.remove(lc);
            }
        }

        if (this.storage != null) {
            this.storage.tick();
        }

        return this.source.tick();
    }

    @Override
    public boolean shouldSave() {
        return true;
    }

    @Override
    public String gatherStats() {
        return "ThreadedChunkCache: " + this.cache.size() + " Drop: " + this.toDrop.size();
    }
}
