package me.alphamode.mcbig.level;

import me.alphamode.mcbig.extensions.BigLevelSourceExtension;
import me.alphamode.mcbig.math.BigConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Region;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.TileEntity;

import java.math.BigInteger;

public class BigRegion extends Region implements BigLevelSourceExtension {
    private BigInteger xc1;
    private BigInteger zc1;
    private LevelChunk[][] chunks;
    private Level level;

    public BigRegion(Level level, BigInteger minX, int minY, BigInteger minZ, BigInteger maxX, int maxY, BigInteger maxZ) {
        super(level, 0, 0, 0, 0, 0, 0);
        this.level = level;
        this.xc1 = minX.shiftRight(4);
        this.zc1 = minZ.shiftRight(4);
        BigInteger xc2 = maxX.shiftRight(4);
        BigInteger zc2 = maxZ.shiftRight(4);
        this.chunks = new LevelChunk[xc2.subtract(this.xc1).add(BigInteger.ONE).intValue()][zc2.subtract(this.zc1).add(BigInteger.ONE).intValue()];

        for(BigInteger x = this.xc1; x.compareTo(xc2) <= 0; x = x.add(BigInteger.ONE)) {
            for(BigInteger z = this.zc1; z.compareTo(zc2) <= 0; z = z.add(BigInteger.ONE)) {
                this.chunks[x.subtract(this.xc1).intValue()][z.subtract(this.zc1).intValue()] = level.getChunk(x, z);
            }
        }
    }

    @Override
    public int getTile(BigInteger x, int y, BigInteger z) {
        if (y < 0) {
            return 0;
        } else if (y >= 128) {
            return 0;
        } else {
            int var4 = (x.shiftRight(4)).subtract(this.xc1).intValue();
            int var5 = (z.shiftRight(4)).subtract(this.zc1).intValue();
            if (var4 >= 0 && var4 < this.chunks.length && var5 >= 0 && var5 < this.chunks[var4].length) {
                LevelChunk var6 = this.chunks[var4][var5];
                return var6 == null ? 0 : var6.getTile(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue());
            } else {
                return 0;
            }
        }
    }

    @Override
    public TileEntity getTileEntity(BigInteger x, int y, BigInteger z) {
        int var4 = (x.shiftRight(4)).subtract(this.xc1).intValue();
        int var5 = (z.shiftRight(4)).subtract(this.zc1).intValue();
        return this.chunks[var4][var5].getTileEntity(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue());
    }

    @Override
    public float getBrightness(BigInteger x, int y, BigInteger z, int max) {
        int br = getRawBrightness(x, y, z);
        if (br < max) {
            br = max;
        }

        return this.level.dimension.brightnessRamp[br];
    }

    @Override
    public float getBrightness(BigInteger x, int y, BigInteger z) {
        return this.level.dimension.brightnessRamp[this.getRawBrightness(x, y, z)];
    }

    @Environment(EnvType.CLIENT)
    public int getRawBrightness(BigInteger x, int y, BigInteger z) {
        return this.getRawBrightness(x, y, z, true);
    }

    @Environment(EnvType.CLIENT)
    public int getRawBrightness(BigInteger x, int y, BigInteger z, boolean checkNeighbors) {
        if (checkNeighbors) {
            int var5 = this.getTile(x, y, z);
            if (var5 == Tile.SLAB.id || var5 == Tile.FARMLAND.id || var5 == Tile.WOOD_STAIRS.id || var5 == Tile.COBBLESTONE_STAIRS.id) {
                int var13 = this.getRawBrightness(x, y + 1, z, false);
                int var7 = this.getRawBrightness(x.add(BigInteger.ONE), y, z, false);
                int var8 = this.getRawBrightness(x.subtract(BigInteger.ONE), y, z, false);
                int var9 = this.getRawBrightness(x, y, z.add(BigInteger.ONE), false);
                int var10 = this.getRawBrightness(x, y, z.subtract(BigInteger.ONE), false);
                if (var7 > var13) {
                    var13 = var7;
                }

                if (var8 > var13) {
                    var13 = var8;
                }

                if (var9 > var13) {
                    var13 = var9;
                }

                if (var10 > var13) {
                    var13 = var10;
                }

                return var13;
            }
        }

        if (y < 0) {
            return 0;
        } else if (y >= 128) {
            int var12 = 15 - this.level.skyDarken;
            if (var12 < 0) {
                var12 = 0;
            }

            return var12;
        } else {
            int var11 = (x.shiftRight(4)).subtract(this.xc1).intValue();
            int var6 = (z.shiftRight(4)).subtract(this.zc1).intValue();
            return this.chunks[var11][var6].getRawBrightness(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue(), this.level.skyDarken);
        }
    }

    @Override
    public int getData(BigInteger x, int y, BigInteger z) {
        if (y < 0) {
            return 0;
        } else if (y >= 128) {
            return 0;
        } else {
            int var4 = (x.shiftRight(4)).subtract(this.xc1).intValue();
            int var5 = (z.shiftRight(4)).subtract(this.zc1).intValue();
            return this.chunks[var4][var5].getData(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue());
        }
    }

    @Override
    public Material getMaterial(BigInteger x, int y, BigInteger z) {
        int tile = getTile(x, y, z);
        return tile == 0 ? Material.AIR : Tile.tiles[tile].material;
    }

    @Override
    public boolean isSolidTile(BigInteger x, int y, BigInteger z) {
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
    public int getRawBrightness(int x, int y, int z) {
        return getRawBrightness(BigInteger.valueOf(x), y, BigInteger.valueOf(z));
    }

    @Override
    public int getRawBrightness(int x, int y, int z, boolean checkNeighbors) {
        return getRawBrightness(BigInteger.valueOf(x), y, BigInteger.valueOf(z), checkNeighbors);
    }

    @Override
    public int getData(int x, int y, int z) {
        return getData(BigInteger.valueOf(x), y, BigInteger.valueOf(z));
    }

    @Override
    public int getTile(int x, int y, int z) {
        return getTile(BigInteger.valueOf(x), y, BigInteger.valueOf(z));
    }
}
