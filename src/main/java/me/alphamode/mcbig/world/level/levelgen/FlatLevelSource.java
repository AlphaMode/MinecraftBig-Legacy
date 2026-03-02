package me.alphamode.mcbig.world.level.levelgen;

import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.tile.Tile;

import java.math.BigInteger;

public class FlatLevelSource implements McBigChunkSource {

    private final Level level;

    public FlatLevelSource(Level level, long seed) {
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

        for (int xt = 0; xt < 16; xt++) {
            for (int zt = 0; zt < 16; zt++) {
                int pos = xt << 11 | zt << 7;
                tiles[pos | 0] = (byte) Tile.BEDROCK.id;
                tiles[pos | 60] = (byte) Tile.STONE.id;
                tiles[pos | 61] = (byte) Tile.STONE.id;
                tiles[pos | 62] = (byte) Tile.DIRT.id;
                tiles[pos | 63] = x.equals(BigInteger.ZERO) && z.equals(BigInteger.ZERO) ? (byte) Tile.SAND.id : (byte) Tile.GRASS.id; // Spawn block needs to be sand and between y 63 and 64
                if (z.equals(BigInteger.ZERO)) {

                }
            }
        }
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
        return true;
    }

    @Override
    public boolean tick() {
        return false;
    }

    @Override
    public boolean shouldSave() {
        return true;
    }

    @Override
    public String gatherStats() {
        return "FlatLevelSource";
    }
}
