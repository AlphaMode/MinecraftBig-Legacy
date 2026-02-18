package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.extensions.BigChunkSourceExtension;
import me.alphamode.mcbig.level.chunk.BigChunkPos;
import me.alphamode.mcbig.level.chunk.BigEmptyLevelChunk;
import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import net.minecraft.client.multiplayer.MultiplayerChunkCache;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mixin(MultiplayerChunkCache.class)
public abstract class MultiplayerChunkCacheMixin implements BigChunkSourceExtension {

    @Shadow
    public abstract void postProcess(ChunkSource generator, int x, int z);

    @Shadow
    private Level level;

    @Shadow
    private Map<BigChunkPos, LevelChunk> loadedChunks;

    @Shadow
    private List<LevelChunk> loadedChunkList;

    @Shadow
    private LevelChunk emptyChunk;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void replaceEmptyChunkWithBigEmptyChunk(Level level, CallbackInfo ci) {
        this.emptyChunk = new BigEmptyLevelChunk(level, new byte[32768], BigInteger.ZERO, BigInteger.ZERO);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void unloadChunk(int x, int z) {
        BigInteger bigX = BigInteger.valueOf(x);
        BigInteger bigZ = BigInteger.valueOf(z);
        LevelChunk chunk = this.getChunk(bigX, bigZ);
        if (!chunk.isEmpty()) {
            chunk.unload();
        }

        this.loadedChunks.remove(new BigChunkPos(bigX, bigZ));
        this.loadedChunkList.remove(chunk);
    }

    @Override
    public boolean hasChunk(BigInteger x, BigInteger z) {
        if (this != null) {
            return true;
        } else {
            BigChunkPos pos = new BigChunkPos(x, z);
            return this.loadedChunks.containsKey(pos);
        }
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
    public LevelChunk getChunk(BigInteger x, BigInteger z) {
        BigChunkPos pos = new BigChunkPos(x, z);
        LevelChunk chunk = this.loadedChunks.get(pos);
        return chunk == null ? this.emptyChunk : chunk;
    }

    /**
     * @author AlphaMode
     * @reason Redirect to big int method
     */
    @Overwrite
    public LevelChunk getChunk(int x, int z) {
        return this.getChunk(BigInteger.valueOf(x), BigInteger.valueOf(z));
    }

    @Override
    public LevelChunk loadChunk(BigInteger x, BigInteger z) {
        BigChunkPos pos = new BigChunkPos(x, z);
        byte[] tiles = new byte[32768];
        BigLevelChunk chunk = new BigLevelChunk(this.level, tiles, x, z);
        Arrays.fill(chunk.skyLight.data, (byte)-1);
        this.loadedChunks.put(pos, chunk);
        chunk.loaded = true;
        return chunk;
    }

    /**
     * @author AlphaMode
     * @reason Redirect to big int method
     */
    @Overwrite
    public LevelChunk loadChunk(int x, int z) {
        return loadChunk(BigInteger.valueOf(x), BigInteger.valueOf(z));
    }

    @Override
    public void postProcess(ChunkSource generator, BigInteger x, BigInteger z) {
        postProcess(generator, x.intValue(), z.intValue());
    }
}
