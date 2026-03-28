package me.alphamode.mcbig.client.gui;

import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import me.alphamode.mcbig.math.BigConstants;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.TileEntity;

import java.math.BigInteger;

public class PreviewRegion implements LevelSource {
    private final int size;

    private final LevelChunk[] chunks;
    private final BiomeSource biomeSource;

    public PreviewRegion(ChunkSource source, int size) {
        this.size = size;
        this.chunks = new BigLevelChunk[size * size];

        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                chunks[x + z * size] = source.getChunk(x, z);
            }
        }

        this.biomeSource = new FixedBiomeSource(Biome.PLAINS, 1, 1);
    }

    @Override
    public int getTile(BigInteger x, int y, BigInteger z) {
        if (y < 0) return 0;
        if (y >= 128) return 0;

        int xc = x.shiftRight(4).intValue();
        int zc = z.shiftRight(4).intValue();

        if (xc < 0 || zc < 0 || xc >= size || zc >= size) return 0;

        LevelChunk lc = this.chunks[xc + zc * size];
        return lc == null ? 0 : lc.getTile(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue());
    }

    @Override
    public int getData(BigInteger x, int y, BigInteger z) {
        if (y < 0) return 0;
        if (y >= 128) return 0;

        int xc = x.shiftRight(4).intValue();
        int zc = z.shiftRight(4).intValue();

        if (xc < 0 || zc < 0 || xc >= size || zc >= size) return 0;

        LevelChunk lc = this.chunks[xc + zc * size];
        return lc == null ? 0 : lc.getData(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue());
    }

    @Override
    public Material getMaterial(BigInteger x, int y, BigInteger z) {
        int t = getTile(x, y, z);
        return t == 0 ? Material.AIR : Tile.tiles[t].material;
    }

    @Override
    public boolean isSolidRenderTile(BigInteger x, int y, BigInteger z) {
        Tile tile = Tile.tiles[this.getTile(x, y, z)];
        return tile == null ? false : tile.isSolidRender();
    }

    @Override
    public boolean isSolidBlockingTile(BigInteger x, int y, BigInteger z) {
        Tile tile = Tile.tiles[this.getTile(x, y, z)];
        if (tile == null) {
            return false;
        } else {
            return tile.material.blocksMotion() && tile.isCubeShaped();
        }
    }

    @Override
    public float getBrightness(BigInteger x, int y, BigInteger z, int max) {
        return 1;
    }

    @Override
    public float getBrightness(BigInteger x, int y, BigInteger z) {
        return 1;
    }

    @Override
    public BiomeSource getBiomeSource() {
        return this.biomeSource;
    }

    @Override
    public int getTile(int x, int y, int z) {
        return getTile(BigInteger.valueOf(x), y, BigInteger.valueOf(z));
    }

    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        return getTileEntity(BigInteger.valueOf(x), y, BigInteger.valueOf(z));
    }

    @Override
    public float getBrightness(int x, int y, int z, int max) {
        return getBrightness(BigInteger.valueOf(x), y, BigInteger.valueOf(z), max);
    }

    @Override
    public float getBrightness(int x, int y, int z) {
        return getBrightness(BigInteger.valueOf(x), y, BigInteger.valueOf(z));
    }

    @Override
    public int getData(int x, int y, int z) {
        return getData(BigInteger.valueOf(x), y, BigInteger.valueOf(z));
    }

    @Override
    public Material getMaterial(int x, int y, int z) {
        return getMaterial(BigInteger.valueOf(x), y, BigInteger.valueOf(z));
    }

    @Override
    public boolean isSolidRenderTile(int x, int y, int z) {
        return isSolidRenderTile(BigInteger.valueOf(x), y, BigInteger.valueOf(z));
    }

    @Override
    public boolean isSolidBlockingTile(int x, int y, int z) {
        return isSolidBlockingTile(BigInteger.valueOf(x), y, BigInteger.valueOf(z));
    }
}
