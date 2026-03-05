package me.alphamode.mcbig.world.level.levelgen;

import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import net.minecraft.util.Mth;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.tile.Tile;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class DebugLevelSource implements McBigChunkSource {
    private static final List<BlockState> ALL_BLOCKS;
    private static final BigInteger GRID_WIDTH;
    private static final BigInteger GRID_HEIGHT;
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
        // Spawn block needs to be sand and between y 63 and 64
        if (x.equals(BigInteger.ZERO) && z.equals(BigInteger.ZERO)) {
            for (int xt = 0; xt < 16; xt++) {
                for (int zt = 0; zt < 16; zt++) {
                    int pos = xt << 11 | zt << 7;
                    tiles[pos | 62] = (byte) Tile.DIRT.id;
                    tiles[pos | 63] = (byte) Tile.SAND.id;
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

    @Nullable
    public static BlockState getBlockStateFor(BigInteger worldX, BigInteger worldZ) {
        BlockState state = null;
        if (worldX.compareTo(BigInteger.ZERO) > 0 && worldZ.compareTo(BigInteger.ZERO) > 0 && !worldX.remainder(BigInteger.TWO).equals(BigInteger.ZERO) && !worldZ.remainder(BigInteger.TWO).equals(BigInteger.ZERO)) {
            worldX = worldX.divide(BigInteger.TWO);
            worldZ = worldZ.divide(BigInteger.TWO);
            if (worldX.compareTo(GRID_WIDTH) <= 0 && worldZ.compareTo(GRID_HEIGHT) <= 0) {
                int index = worldX.multiply(GRID_WIDTH).add(worldZ).abs().intValue();
                if (index < ALL_BLOCKS.size()) {
                    state = ALL_BLOCKS.get(index);
                }
            }
        }

        return state;
    }

    @Override
    public void postProcess(ChunkSource source, BigInteger chunkX, BigInteger chunkZ) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                BigInteger worldX = chunkX.shiftLeft(4).add(BigInteger.valueOf(x));
                BigInteger worldZ = chunkZ.shiftLeft(4).add(BigInteger.valueOf(z));
//                level.setTile(worldX, 60, worldZ, Tile.SAND.id);
                BlockState state = getBlockStateFor(worldX, worldZ);
                if (state != null) {
                    level.setTileAndData(worldX, 70, worldZ, state.id(), state.data());
                }
            }
        }
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

    static {
        var blocks = new ArrayList<BlockState>();
        for (int id = 0; id < Tile.tiles.length; id++) {
            if (Tile.tiles[id] == null)
                continue;
            for (int data = 0; data < 16; data++) {
                blocks.add(new BlockState(id, data));
            }
        }
        ALL_BLOCKS = List.copyOf(blocks);
        GRID_WIDTH = BigInteger.valueOf(ceil(Mth.sqrt(ALL_BLOCKS.size())));
        GRID_HEIGHT = BigInteger.valueOf(ceil((float)ALL_BLOCKS.size() / GRID_WIDTH.intValue()));
    }

    public static int ceil(final float v) {
        int i = (int)v;
        return v > i ? i + 1 : i;
    }

    public record BlockState(int id, int data) {

    }
}
