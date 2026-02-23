package me.alphamode.mcbig.mixin.server;

import me.alphamode.mcbig.extensions.BigChunkSourceExtension;
import me.alphamode.mcbig.extensions.server.BigServerChunkCacheExtension;
import me.alphamode.mcbig.level.chunk.BigChunkPos;
import me.alphamode.mcbig.level.chunk.BigEmptyLevelChunk;
import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.world.phys.BigVec3i;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Vec3i;
import net.minecraft.world.level.chunk.ChunkPos;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin implements BigChunkSourceExtension, BigServerChunkCacheExtension {
    @Shadow
    private ServerLevel level;
    @Shadow
    private Set<BigChunkPos> toDrop;
    @Shadow
    public boolean f_95421973;
    @Shadow
    private LevelChunk emptyChunks;
    @Shadow
    private ChunkStorage chunkIo;
    @Shadow
    private ChunkSource wrapped;
    @Shadow
    private List<LevelChunk> chunks;

    @Shadow
    protected abstract void saveChunk(LevelChunk chunk);

    @Shadow
    protected abstract void saveExtra(LevelChunk chunk);

    private Map<BigChunkPos, LevelChunk> cacheBig = new HashMap<>();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void replaceEmptyChunkWithBigEmptyChunk(ServerLevel level, ChunkStorage chunkIo, ChunkSource wrapped, CallbackInfo ci) {
        this.emptyChunks = new BigEmptyLevelChunk(this.level, new byte[32768], BigInteger.ZERO, BigInteger.ZERO);
    }

    @Override
    public boolean hasChunk(BigInteger x, BigInteger z) {
        return this.cacheBig.containsKey(new BigChunkPos(x, z));
    }

    /**
     * @author AlphaMode
     * @reason Redirect to big int method
     */
    @Overwrite
    public boolean hasChunk(int x, int z) {
        return this.hasChunk(BigInteger.valueOf(x), BigInteger.valueOf(z));
    }

    @Override
    public void dropNoneSpawnChunk(BigInteger x, BigInteger z) {
        BigVec3i spawnPos = this.level.getBigSpawnPos();
        int xRange = x.multiply(BigConstants.SIXTEEN).add(BigConstants.EIGHT).subtract(spawnPos.x()).intValue();
        int zRange = z.multiply(BigConstants.SIXTEEN).add(BigConstants.EIGHT).subtract(spawnPos.z()).intValue();
        short range = 128;
        if (xRange < -range || xRange > range || zRange < -range || zRange > range) {
            this.toDrop.add(new BigChunkPos(x, z));
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void dropNoneSpawnChunk(int x, int z) {
        this.dropNoneSpawnChunk(BigInteger.valueOf(x), BigInteger.valueOf(z));
    }

    @Override
    public LevelChunk loadChunk(BigInteger x, BigInteger z) {
        BigChunkPos pos = new BigChunkPos(x, z);
        this.toDrop.remove(pos);
        LevelChunk chunk = this.cacheBig.get(pos);
        if (chunk == null) {
            chunk = this.readChunk(x, z);
            if (chunk == null) {
                if (this.wrapped == null) {
                    chunk = this.emptyChunks;
                } else {
                    chunk = this.wrapped.getChunk(x, z);
                }
            }

            this.cacheBig.put(pos, chunk);
            this.chunks.add(chunk);
            if (chunk != null) {
                chunk.lightLava();
                chunk.load();
            }

            if (!chunk.terrainPopulated && this.hasChunk(x.add(BigInteger.ONE), z.add(BigInteger.ONE)) && this.hasChunk(x, z.add(BigInteger.ONE)) && this.hasChunk(x.add(BigInteger.ONE), z)) {
                this.postProcess((ChunkSource) this, x, z);
            }

            if (this.hasChunk(x.subtract(BigInteger.ONE), z)
                    && !this.getChunk(x.subtract(BigInteger.ONE), z).terrainPopulated
                    && this.hasChunk(x.subtract(BigInteger.ONE), z.add(BigInteger.ONE))
                    && this.hasChunk(x, z.add(BigInteger.ONE))
                    && this.hasChunk(x.subtract(BigInteger.ONE), z)) {
                this.postProcess((ChunkSource) this, x.subtract(BigInteger.ONE), z);
            }

            if (this.hasChunk(x, z.subtract(BigInteger.ONE))
                    && !this.getChunk(x, z.subtract(BigInteger.ONE)).terrainPopulated
                    && this.hasChunk(x.add(BigInteger.ONE), z.subtract(BigInteger.ONE))
                    && this.hasChunk(x, z.subtract(BigInteger.ONE))
                    && this.hasChunk(x.add(BigInteger.ONE), z)) {
                this.postProcess((ChunkSource) this, x, z.subtract(BigInteger.ONE));
            }

            if (this.hasChunk(x.subtract(BigInteger.ONE), z.subtract(BigInteger.ONE))
                    && !this.getChunk(x.subtract(BigInteger.ONE), z.subtract(BigInteger.ONE)).terrainPopulated
                    && this.hasChunk(x.subtract(BigInteger.ONE), z.subtract(BigInteger.ONE))
                    && this.hasChunk(x, z.subtract(BigInteger.ONE))
                    && this.hasChunk(x.subtract(BigInteger.ONE), z)) {
                this.postProcess((ChunkSource) this, x.subtract(BigInteger.ONE), z.subtract(BigInteger.ONE));
            }
        }

        return chunk;
    }

    /**
     * @author AlphaMode
     * @reason Redirect to big int method
     */
    @Overwrite
    public LevelChunk loadChunk(int x, int z) {
        return this.loadChunk(BigInteger.valueOf(x), BigInteger.valueOf(z));
    }

    @Override
    public LevelChunk getChunk(BigInteger x, BigInteger z) {
        LevelChunk chunk = this.cacheBig.get(new BigChunkPos(x, z));
        if (chunk == null) {
            return !this.level.isFindingSpawn && !this.f_95421973 ? this.emptyChunks : this.loadChunk(x, z);
        } else {
            return chunk;
        }
    }

    /**
     * @author AlphaMode
     * @reason Redirect to big int method
     */
    @Overwrite
    public LevelChunk getChunk(int x, int z) {
        return this.getChunk(BigInteger.valueOf(x), BigInteger.valueOf(z));
    }

    private LevelChunk readChunk(BigInteger x, BigInteger z) {
        if (this.chunkIo == null) {
            return null;
        } else {
            try {
                BigLevelChunk chunk = this.chunkIo.load(this.level, x, z);
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
        LevelChunk chunk = this.getChunk(x, z);
        if (!chunk.terrainPopulated) {
            chunk.terrainPopulated = true;
            if (this.wrapped != null) {
                this.wrapped.postProcess(generator, x, z);
                chunk.markUnsaved();
            }
        }
    }

    /**
     * @author AlphaMode
     * @reason Redirect to big int method
     */
    @Overwrite
    public void postProcess(ChunkSource generator, int x, int z) {
        this.postProcess(generator, BigInteger.valueOf(x), BigInteger.valueOf(z));
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean tick() {
        if (!this.level.noSave) {
            for (int i = 0; i < 100; i++) {
                if (!this.toDrop.isEmpty()) {
                    BigChunkPos pos = this.toDrop.iterator().next();
                    LevelChunk chunk = this.cacheBig.get(pos);
                    chunk.unload();
                    this.saveChunk(chunk);
                    this.saveExtra(chunk);
                    this.toDrop.remove(pos);
                    this.cacheBig.remove(pos);
                    this.chunks.remove(chunk);
                }
            }

            if (this.chunkIo != null) {
                this.chunkIo.tick();
            }
        }

        return this.wrapped.tick();
    }
}
