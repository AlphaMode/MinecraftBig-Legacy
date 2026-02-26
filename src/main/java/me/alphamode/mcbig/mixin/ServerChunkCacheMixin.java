package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.BigChunkSourceExtension;
import me.alphamode.mcbig.level.chunk.BigChunkPos;
import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.levelgen.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin implements ChunkSource, BigChunkSourceExtension {
    @Shadow
    private Map<BigChunkPos, LevelChunk> cache;

    @Shadow
    private Set toDrop;

    @Shadow private ChunkSource source;

    @Shadow private LevelChunk emptyChunk;

    @Shadow private List<LevelChunk> chunks;

    @Shadow private ChunkStorage storage;

    @Shadow private Level level;

    @Override
    public boolean hasChunk(BigInteger x, BigInteger z) {
        return this.cache.containsKey(new BigChunkPos(x, z));
    }

    /**
     * @author AlphaMode
     * @reason Fallback to big int version
     */
    @Overwrite
    public boolean hasChunk(int x, int z) {
        return hasChunk(BigInteger.valueOf(x), BigInteger.valueOf(z));
    }

    @Override
    public LevelChunk loadChunk(BigInteger x, BigInteger z) {
        BigChunkPos pos = new BigChunkPos(x, z);
        this.toDrop.remove(pos);
        LevelChunk chunk = this.cache.get(pos);
        if (chunk == null) {
            chunk = readChunk(x, z);
            if (chunk == null) {
                if (this.source == null) {
                    chunk = this.emptyChunk;
                } else {
                    chunk = this.source.getChunk(x, z);
                }
            }

            this.cache.put(pos, chunk);
            this.chunks.add(chunk);
            if (chunk != null) {
                chunk.lightLava();
                chunk.load();
            }

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
        }

        return chunk;
    }

    /**
     * @author AlphaMode
     * @reason Fallback to big int version
     */
    @Overwrite
    public LevelChunk loadChunk(int x, int z) {
        return loadChunk(BigInteger.valueOf(x), BigInteger.valueOf(z));
    }

    @Override
    public LevelChunk getChunk(BigInteger x, BigInteger z) {
        LevelChunk chunk = this.cache.get(new BigChunkPos(x, z));
        return chunk == null ? this.loadChunk(x, z) : chunk;
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
}
