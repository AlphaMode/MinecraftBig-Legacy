package me.alphamode.mcbig.world.level.levelgen;

import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;

import java.math.BigInteger;

public class DebugLevelSource implements McBigChunkSource {
    private final Level level;

    public DebugLevelSource(Level level, long seed) {
        this.level = level;
    }

    @Override
    public boolean hasChunk(BigInteger x, BigInteger z) {
        return true;
    }

    @Override
    public LevelChunk getChunk(BigInteger x, BigInteger z) {
        byte[] tiles = new byte[32768];
        BigLevelChunk chunk = new BigLevelChunk(this.level, tiles, x, z);
        chunk.recalcHeightmap();
        return chunk;
    }

    @Override
    public LevelChunk loadChunk(BigInteger x, BigInteger z) {
        return getChunk(x, z);
    }

    @Override
    public void postProcess(ChunkSource generator, BigInteger x, BigInteger z) {

    }

    @Override
    public boolean save(boolean bl, ProgressListener listener) {
        return false;
    }

    @Override
    public boolean tick() {
        return false;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public String gatherStats() {
        return "DebugLevelSource";
    }

    public record BlockState(int id) {

    }
}
