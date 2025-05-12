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

            if (!chunk.terrainPopulated && hasChunk(x.add(BigInteger.ONE), z.add(BigInteger.ONE)) && hasChunk(x, z.add(BigInteger.ONE)) && hasChunk(x.add(BigInteger.ONE), z)) {
                postProcess(this, x, z);
            }

            if (hasChunk(x.subtract(BigInteger.ONE), z)
                    && !getChunk(x.subtract(BigInteger.ONE), z).terrainPopulated
                    && hasChunk(x.subtract(BigInteger.ONE), z.add(BigInteger.ONE))
                    && hasChunk(x, z.add(BigInteger.ONE))
                    && hasChunk(x.subtract(BigInteger.ONE), z)) {
                this.postProcess(this, x.subtract(BigInteger.ONE), z);
            }

            if (hasChunk(x, z.subtract(BigInteger.ONE))
                    && !getChunk(x, z.subtract(BigInteger.ONE)).terrainPopulated
                    && hasChunk(x.add(BigInteger.ONE), z.subtract(BigInteger.ONE))
                    && hasChunk(x, z.subtract(BigInteger.ONE))
                    && hasChunk(x.add(BigInteger.ONE), z)) {
                postProcess(this, x, z.subtract(BigInteger.ONE));
            }

            if (hasChunk(x.subtract(BigInteger.ONE), z.subtract(BigInteger.ONE))
                    && !getChunk(x.subtract(BigInteger.ONE), z.subtract(BigInteger.ONE)).terrainPopulated
                    && hasChunk(x.subtract(BigInteger.ONE), z.subtract(BigInteger.ONE))
                    && hasChunk(x, z.subtract(BigInteger.ONE))
                    && hasChunk(x.subtract(BigInteger.ONE), z)) {
                postProcess(this, x.subtract(BigInteger.ONE), z.subtract(BigInteger.ONE));
            }
        }

        return chunk;
    }

    @Override
    public LevelChunk getChunk(BigInteger x, BigInteger z) {
        LevelChunk chunk = this.cache.get(new BigChunkPos(x, z));
        return chunk == null ? this.loadChunk(x, z) : chunk;
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
