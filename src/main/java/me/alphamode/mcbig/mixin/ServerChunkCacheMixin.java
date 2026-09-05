package me.alphamode.mcbig.mixin;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.alphamode.mcbig.extensions.BigChunkSourceExtension;
import me.alphamode.mcbig.level.chunk.BigChunkPos;
import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.levelgen.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin implements ChunkSource, BigChunkSourceExtension {
    private static final ExecutorService CHUNK_LOADING_EXECUTOR = Executors.newSingleThreadExecutor();

    private Object2ObjectMap<BigChunkPos, LevelChunk> cacheBig = new Object2ObjectOpenHashMap<>();

    private Set<BigChunkPos> toDropB = new HashSet<>();

    @Shadow private ChunkSource source;

    @Shadow private LevelChunk emptyChunk;

    @Shadow private List<LevelChunk> chunks;

    @Shadow private ChunkStorage storage;

    @Shadow private Level level;

    @Override
    public boolean hasChunk(BigInteger x, BigInteger z) {
        return this.cacheBig.containsKey(new BigChunkPos(x, z));
    }

    /**
     * @author AlphaMode
     * @reason Fallback to big int version
     */
    @Overwrite
    public boolean hasChunk(int x, int z) {
        return hasChunk(BigInteger.valueOf(x), BigInteger.valueOf(z));
    }

    private BigChunkPos lastPos = null;

    @Override
    public LevelChunk create(BigInteger x, BigInteger z) {
        BigChunkPos pos = new BigChunkPos(x, z);
        this.toDropB.remove(pos);
        LevelChunk chunk = this.cacheBig.get(pos);
        if (chunk == null) {
            chunk = readChunk(x, z);
            if (chunk == null) {
                if (this.source == null) {
                    chunk = this.emptyChunk;
                } else {
                    chunk = this.source.getChunk(x, z);
                }
            }

            this.cacheBig.put(pos, chunk);
            this.chunks.add(chunk);
            if (chunk != null) {
                chunk.lightLava();
                chunk.load();
            }

            //? >=1.0.0-beta.8.0.r {
            /*chunk.checkPostProcess(this, this, x, z);
            *///? } else {
            BigInteger xMinusOne = x.subtract(BigInteger.ONE);
            BigInteger zMinusOne = z.subtract(BigInteger.ONE);

            BigInteger xPlusOne = x.add(BigInteger.ONE);
            BigInteger zPlusOne = z.add(BigInteger.ONE);

            if (!chunk.terrainPopulated && this.hasChunk(xPlusOne, zPlusOne) && this.hasChunk(x, zPlusOne) && this.hasChunk(xPlusOne, z)) {
                this.postProcess(this, x, z);
            }

            if (this.hasChunk(xMinusOne, z)
                    && !this.getChunk(xMinusOne, z).terrainPopulated
                    && this.hasChunk(xMinusOne, zPlusOne)
                    && this.hasChunk(x, zPlusOne)
                    && this.hasChunk(xMinusOne, z)) {
                this.postProcess(this, xMinusOne, z);
            }

            if (this.hasChunk(x, zMinusOne)
                    && !this.getChunk(x, zMinusOne).terrainPopulated
                    && this.hasChunk(xPlusOne, zMinusOne)
                    && this.hasChunk(x, zMinusOne)
                    && this.hasChunk(xPlusOne, z)) {
                this.postProcess(this, x, zMinusOne);
            }

            if (this.hasChunk(xMinusOne, zMinusOne)
                    && !this.getChunk(xMinusOne, zMinusOne).terrainPopulated
                    && this.hasChunk(xMinusOne, zMinusOne)
                    && this.hasChunk(x, zMinusOne)
                    && this.hasChunk(xMinusOne, z)) {
                this.postProcess(this, xMinusOne, zMinusOne);
            }
            //? }
        }

        return chunk;
    }

    /**
     * @author AlphaMode
     * @reason Fallback to big int version
     */
    @Overwrite
    public LevelChunk create(int x, int z) {
        return create(BigInteger.valueOf(x), BigInteger.valueOf(z));
    }

    @Override
    public LevelChunk getChunk(BigInteger x, BigInteger z) {
        BigChunkPos pos = new BigChunkPos(x, z);
        LevelChunk chunk = this.cacheBig.get(pos);
        if (lastPos != null && lastPos.equals(pos) && chunk == null) {
            throw new RuntimeException("Chunk cache is in an invalid state");
        }
        this.lastPos = pos;
        return chunk == null ? this.create(x, z) : chunk;
    }

    @Override
    public CompletableFuture<LevelChunk> getChunkFuture(BigInteger x, BigInteger z) {
        LevelChunk chunk = this.cacheBig.get(new BigChunkPos(x, z));
        return chunk == null ? CompletableFuture.supplyAsync(() -> this.create(x, z), CHUNK_LOADING_EXECUTOR) : CompletableFuture.completedFuture(chunk);
    }

    /**
     * @author AlphaMode
     * @reason Fallback to big int version
     */
    @Overwrite
    public LevelChunk getChunk(int x, int z) {
        return getChunk(BigInteger.valueOf(x), BigInteger.valueOf(z));
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
        LevelChunk chunk = getChunk(x, z);
        if (!chunk.terrainPopulated) {
            chunk.terrainPopulated = true;
            if (this.source != null) {
                this.source.postProcess(generator, x, z);
                chunk.markUnsaved();
            }
        }
    }

    //? >=1.0.0-beta.8.0.r {

    /*@Shadow
    private int lastChunkIndex;

    @Shadow
    public abstract void drop(int par1, int par2);

    @Shadow
    protected abstract void saveChunk(LevelChunk chunk);

    @Shadow
    protected abstract void saveEntities(LevelChunk chunk);

    /^*
     * @author
     * @reason
     ^/
    @Overwrite
    public boolean tick() {
        for(int var1 = 0; var1 < 100; ++var1) {
            if (!this.toDropB.isEmpty()) {
                BigChunkPos var2 = this.toDropB.iterator().next();
                LevelChunk var3 = this.cacheBig.get(var2);
                var3.unload();
                this.saveChunk(var3);
                this.saveEntities(var3);
                this.toDropB.remove(var2);
                this.cacheBig.remove(var2);
                this.chunks.remove(var3);
            }
        }

        for(int var4 = 0; var4 < 10; ++var4) {
            if (this.lastChunkIndex >= this.chunks.size()) {
                this.lastChunkIndex = 0;
                break;
            }

            LevelChunk var5 = this.chunks.get(this.lastChunkIndex++);
            Player var6 = this.level.getNearestPlayer((double)(var5.x << 4) + (double)8.0F, 64.0F, (double)(var5.z << 4) + (double)8.0F, (double)288.0F);
            if (var6 == null) {
                this.drop(var5.x, var5.z);
            }
        }

        if (this.storage != null) {
            this.storage.tick();
        }

        return this.source.tick();
    }
    *///? }
}
