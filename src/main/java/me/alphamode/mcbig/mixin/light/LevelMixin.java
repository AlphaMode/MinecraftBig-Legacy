package me.alphamode.mcbig.mixin.light;

import me.alphamode.mcbig.constants.LevelConstants;
import me.alphamode.mcbig.extensions.BigLevelExtension;
import me.alphamode.mcbig.extensions.BigLevelSourceExtension;
//? <1.0.0-beta.8.0.r
import me.alphamode.mcbig.level.BigLightUpdate;
import me.alphamode.mcbig.math.BigConstants;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelListener;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Mixin(Level.class)
public abstract class LevelMixin implements BigLevelExtension, BigLevelSourceExtension {
    @Shadow
    public int skyDarken;

    @Shadow
    @Final
    public Dimension dimension;
    @Shadow
    protected List<LevelListener> listeners;

    //? >=1.0.0-beta.8.0.r
    //@Shadow private int[] toCheck;

    //? >1.0.0-beta.8.0.r {
    /*private List<BigLightUpdate> lightUpdatesBig = new ArrayList<>();
    *///? }

    @Override
    public int getLightLevel(BigInteger x, int y, BigInteger z) {
        return getRawBrightness(x, y, z, true);
    }

    @Override
    public int getRawBrightness(BigInteger x, int y, BigInteger z, boolean combineNeighbours) {
        if (combineNeighbours) {
            int id = getTile(x, y, z);
            if (id == Tile.stoneSlabHalf.id || id == Tile.farmland.id || id == Tile.stairs_stone.id || id == Tile.stairs_wood.id) {
                int br  = getRawBrightness(x, y + 1, z, false);
                int br1 = getRawBrightness(x.add(BigInteger.ONE), y, z, false);
                int br2 = getRawBrightness(x.subtract(BigInteger.ONE), y, z, false);
                int br3 = getRawBrightness(x, y, z.add(BigInteger.ONE), false);
                int br4 = getRawBrightness(x, y, z.subtract(BigInteger.ONE), false);
                if (br1 > br) br = br1;
                if (br2 > br) br = br2;
                if (br3 > br) br = br3;
                if (br4 > br) br = br4;
                return br;
            }
        }

        if (y < 0) return 0;
        if (y >= 128) {
            y = 127;
        }

        LevelChunk c = this.getChunk(x.shiftRight(4), z.shiftRight(4));
        return c.getRawBrightness(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue(), this.skyDarken);
    }

    //? >=1.0.0-beta.8.0.r {
    /*public void checkLight(BigInteger x, int y, BigInteger z) {
        checkLight(LightLayer.SKY, x, y, z);
        checkLight(LightLayer.BLOCK, x, y, z);
    }

    private int getSkyLight(int oc, BigInteger x, int y, BigInteger z, int ct, int block) {
        int emmit = 0;
        if (this.canSeeSky(x, y, z)) {
            emmit = 15;
        } else {
            if (block == 0) {
                block = 1;
            }

            for (int face = 0; face < 6; face++) {
                int step = face % 2 * 2 - 1;
                BigInteger xt = x.add(BigInteger.valueOf(face / 2 % 3 / 2 * step));
                int yt = y + (face / 2 + 1) % 3 / 2 * step;
                BigInteger zt = z.add(BigInteger.valueOf((face / 2 + 2) % 3 / 2 * step));
                int br = this.getBrightness(LightLayer.SKY, xt, yt, zt) - block;
                if (br > emmit) {
                    emmit = br;
                }
            }
        }

        return emmit;
    }

    private int getBlockLight(int oc, BigInteger x, int y, BigInteger z, int ct, int block) {
        int br = Tile.lightEmission[ct];
        int br1 = getBrightness(LightLayer.BLOCK, x.subtract(BigInteger.ONE), y, z) - block;
        int br2 = getBrightness(LightLayer.BLOCK, x.add(BigInteger.ONE), y, z) - block;
        int br3 = getBrightness(LightLayer.BLOCK, x, y - 1, z) - block;
        int br4 = getBrightness(LightLayer.BLOCK, x, y + 1, z) - block;
        int br5 = getBrightness(LightLayer.BLOCK, x, y, z.subtract(BigInteger.ONE)) - block;
        int br6 = getBrightness(LightLayer.BLOCK, x, y, z.add(BigInteger.ONE)) - block;

        if (br1 > br) br = br1;
        if (br2 > br) br = br2;
        if (br3 > br) br = br3;
        if (br4 > br) br = br4;
        if (br5 > br) br = br5;
        if (br6 > br) br = br6;

        return br;
    }

    public void checkLight(LightLayer layer, BigInteger xc, int yc, BigInteger zc) {
        if (this.hasChunksAt(xc, yc, zc, 17)) {
            int checkedPosition = 0;
            int toCheckCount = 0;
            int centerCurrent = this.getBrightness(layer, xc, yc, zc);
            int t = this.getTile(xc, yc, zc);
            int b = Tile.lightBlock[t];
            if (b == 0) {
                b = 1;
            }

            int centerExpected = 0;
            if (layer == LightLayer.SKY) {
                centerExpected = this.getSkyLight(centerCurrent, xc, yc, zc, t, b);
            } else {
                centerExpected = this.getBlockLight(centerCurrent, xc, yc, zc, t, b);
            }

            if (centerExpected > centerCurrent) {
                this.toCheck[toCheckCount++] = 32 | (32 << 6) | (32 << 12);
            } else if (centerExpected < centerCurrent) {
                if (layer != LightLayer.BLOCK) {
                }

                this.toCheck[toCheckCount++] = 32 | (32 << 6) | (32 << 12) + (centerCurrent << 18);

                while (checkedPosition < toCheckCount) {
                    int p = this.toCheck[checkedPosition++];
                    BigInteger x = BigInteger.valueOf((p & 63) - 32).add(xc);
                    int y = (p >> 6 & 63) - 32 + yc;
                    BigInteger z = BigInteger.valueOf((p >> 12 & 63) - 32).add(zc);
                    int expected = p >> 18 & 15;
                    int current = this.getBrightness(layer, x, y, z);
                    if (current == expected) {
                        this.setBrightness(layer, x, y, z, 0);
                        expected--;
                        if (expected > 0) {
                            int xd = x.subtract(xc).intValue();
                            int yd = y - yc;
                            int zd = z.subtract(zc).intValue();

                            if (xd < 0) xd = -xd;
                            if (yd < 0) yd = -yd;
                            if (zd < 0) zd = -zd;

                            if (xd + yd + zd < 17) {
                                for (int face = 0; face < 6; face++) {
                                    int step = face % 2 * 2 - 1;
                                    BigInteger xx = x.add(BigInteger.valueOf(face / 2 % 3 / 2 * step));
                                    int yy = y + (face / 2 + 1) % 3 / 2 * step;
                                    BigInteger zz = z.add(BigInteger.valueOf((face / 2 + 2) % 3 / 2 * step));

                                    current = this.getBrightness(layer, xx, yy, zz);
                                    if (current == expected) {
                                        this.toCheck[toCheckCount++] = (xx.subtract(xc).intValue() + 32) + ((yy - yc + 32) << 6) + ((zz.subtract(zc).intValue() + 32) << 12) + (expected << 18);
                                    }
                                }
                            }
                        }
                    }
                }

                checkedPosition = 0;
            }

            while (checkedPosition < toCheckCount) {
                final int p = this.toCheck[checkedPosition++];
                final BigInteger x = BigInteger.valueOf((p & 63) - 32).add(xc);
                final int y = (p >> 6 & 63) - 32 + yc;
                BigInteger z = BigInteger.valueOf((p >> 12 & 63) - 32).add(zc);
                final BigInteger xMinusOne = x.subtract(BigInteger.ONE);
                final BigInteger xPlusOne = x.add(BigInteger.ONE);
                final BigInteger zMinusOne = z.subtract(BigInteger.ONE);
                final BigInteger zPlusOne = z.add(BigInteger.ONE);
                int current = this.getBrightness(layer, x, y, z);
                int id = this.getTile(x, y, z);
                int block = Tile.lightBlock[id];
                if (block == 0) block = 1;

                int expected = 0;
                if (layer == LightLayer.SKY) {
                    expected = this.getSkyLight(current, x, y, z, id, block);
                } else {
                    expected = this.getBlockLight(current, x, y, z, id, block);
                }

                if (expected != current) {
                    this.setBrightness(layer, x, y, z, expected);
                    if (expected > current) {
                        int xd = x.subtract(xc).intValue();
                        int yd = y - yc;
                        int zd = z.subtract(zc).intValue();
                        if (xd < 0) xd = -xd;
                        if (yd < 0) yd = -yd;
                        if (zd < 0) zd = -zd;

                        if (xd + yd + zd < 17 && toCheckCount < this.toCheck.length - 6) {
                            if (this.getBrightness(layer, xMinusOne, y, z) < expected) {
                                this.toCheck[toCheckCount++] = ((xMinusOne.subtract(xc).intValue()) + 32) + (((y - yc) + 32) << 6) + (((z.subtract(zc).intValue()) + 32) << 12);
                            }

                            if (this.getBrightness(layer, xPlusOne, y, z) < expected) {
                                this.toCheck[toCheckCount++] = ((xPlusOne.subtract(xc).intValue()) + 32) + (((y - yc) + 32) << 6) + (((z.subtract(zc).intValue()) + 32) << 12);
                            }

                            if (this.getBrightness(layer, x, y - 1, z) < expected) {
                                this.toCheck[toCheckCount++] = (((x.subtract(xc).intValue()) + 32)) + (((y - 1 - yc) + 32) << 6) + (((z.subtract(zc).intValue()) + 32) << 12);
                            }

                            if (this.getBrightness(layer, x, y + 1, z) < expected) {
                                this.toCheck[toCheckCount++] = (((x.subtract(xc).intValue()) + 32)) + (((y + 1 - yc) + 32) << 6) + (((z.subtract(zc).intValue()) + 32) << 12);
                            }

                            if (this.getBrightness(layer, x, y, zMinusOne) < expected) {
                                this.toCheck[toCheckCount++] = (((x.subtract(xc).intValue()) + 32)) + (((y - yc) + 32) << 6) + (((zMinusOne.subtract(zc).intValue()) + 32) << 12);
                            }

                            if (this.getBrightness(layer, x, y, zPlusOne) < expected) {
                                this.toCheck[toCheckCount++] = (((x.subtract(xc).intValue()) + 32)) + (((y - yc) + 32) << 6) + (((zPlusOne.subtract(zc).intValue()) + 32) << 12);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getLightColor(BigInteger x, int y, BigInteger z, int emitt) {
        int s = this.getBrightnessPropagate(LightLayer.SKY, x, y, z);
        int b = this.getBrightnessPropagate(LightLayer.BLOCK, x, y, z);
        if (b < emitt) {
            b = emitt;
        }

        return s << 20 | b << 4;
    }

    @Override
    public int getBrightnessPropagate(LightLayer layer, BigInteger x, int y, BigInteger z) {
        if (y < 0) {
            y = 0;
        }

        if (y >= LevelConstants.MAX_BUILD_HEIGHT && layer == LightLayer.SKY) {
            return 15;
        } else if (y >= 0 && y < LevelConstants.MAX_BUILD_HEIGHT/^ && x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000^/) {
            BigInteger xc = x.shiftRight(4);
            BigInteger zc = z.shiftRight(4);
            if (!this.hasChunk(xc, zc)) return 0;
            int id = this.getTile(x, y, z);
            if (id != Tile.stoneSlabHalf.id && id != Tile.farmland.id && id != Tile.stairs_stone.id && id != Tile.stairs_wood.id) {
                LevelChunk lc = this.getChunk(xc, zc);
                return lc.getBrightness(layer, x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue());
            } else {
                int br = this.getBrightness(layer, x, y + 1, z);
                int br1 = this.getBrightness(layer, x.add(BigInteger.ONE), y, z);
                int br2 = this.getBrightness(layer, x.subtract(BigInteger.ONE), y, z);
                int br3 = this.getBrightness(layer, x, y, z.add(BigInteger.ONE));
                int br4 = this.getBrightness(layer, x, y, z.subtract(BigInteger.ONE));
                if (br1 > br) br = br1;
                if (br2 > br) br = br2;
                if (br3 > br) br = br3;
                if (br4 > br) br = br4;
                return br;
            }
        } else {
            return layer.surrounding;
        }
    }
    *///? } else {

    @Shadow
    private static int maxLoop;

    private List<BigLightUpdate> lightUpdatesBig = new ArrayList<>();

    @Override
    public void updateLight(LightLayer type, BigInteger x0, int y0, BigInteger z0, BigInteger x1, int y1, BigInteger z1, boolean expand) {
        if (!this.dimension.hasCeiling || type != LightLayer.SKY) {
            ++maxLoop;

            try {
                if (maxLoop != 50) {
                    BigInteger x = (x1.add(x0)).divide(BigInteger.TWO);
                    BigInteger z = (z1.add(z0)).divide(BigInteger.TWO);
                    if (hasChunkAt(x, 64, z)) {
                        if (!getChunkAt(x, z).isEmpty()) {
                            int size = this.lightUpdatesBig.size();
                            if (expand) {
                                int maxSize = 5;
                                if (maxSize > size) {
                                    maxSize = size;
                                }

                                for (int i = 0; i < maxSize; ++i) {
                                    BigLightUpdate update = this.lightUpdatesBig.get(this.lightUpdatesBig.size() - i - 1);
                                    if (update.type == type && update.expandToContain(x0, y0, z0, x1, y1, z1)) {
                                        return;
                                    }
                                }
                            }

                            this.lightUpdatesBig.add(new BigLightUpdate(type, x0, y0, z0, x1, y1, z1));
                            int updates = 1000000;
                            if (this.lightUpdatesBig.size() > 1000000) {
                                System.out.println("More than " + updates + " updates, aborting lighting updates");
                                this.lightUpdatesBig.clear();
                            }
                        }
                    }
                }
            } finally {
                --maxLoop;
            }
        }
    }

    @Override
    public void updateLightIfOtherThan(LightLayer layer, BigInteger x, int y, BigInteger z, int level) {
        if (!this.dimension.hasCeiling || layer != LightLayer.SKY) {
            if (this.hasChunkAt(x, y, z)) {
                if (layer == LightLayer.SKY) {
                    if (this.isSkyLit(x, y, z)) {
                        level = 15;
                    }
                } else if (layer == LightLayer.BLOCK) {
                    int tt = this.getTile(x, y, z);
                    if (Tile.lightEmission[tt] > level) {
                        level = Tile.lightEmission[tt];
                    }
                }

                if (this.getBrightness(layer, x, y, z) != level) {
                    this.updateLight(layer, x, y, z, x, y, z);
                }
            }
        }
    }
    //? }

    @Override
    public float getBrightness(BigInteger x, int y, BigInteger z, int emitt) {
        int n = getLightLevel(x, y, z);
        if (n < emitt) n = emitt;
        return this.dimension.brightnessRamp[n];
    }

    @Override
    public float getBrightness(BigInteger x, int y, BigInteger z) {
        return this.dimension.brightnessRamp[this.getLightLevel(x, y, z)];
    }

    @Override
    public int getBrightness(LightLayer type, BigInteger x, int y, BigInteger z) {
        if (y < 0) {
            y = 0;
        }

        if (y >= 128) {
            y = 127;
        }

        if (y >= 0 && y < 128) {
            BigInteger chunkX = x.shiftRight(4);
            BigInteger chunkZ = z.shiftRight(4);
            if (!hasChunk(chunkX, chunkZ)) {
                return 0;
            } else {
                LevelChunk chunk = this.getChunk(chunkX, chunkZ);
                return chunk.getBrightness(type, x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue());
            }
        } else {
            return type.surrounding;
        }
    }

    @Override
    public void setBrightness(LightLayer layer, BigInteger x, int y, BigInteger z, int level) {
        if (y >= 0) {
            if (y < 128) {
                if (hasChunk(x.shiftRight(4), z.shiftRight(4))) {
                    LevelChunk chunk = this.getChunk(x.shiftRight(4), z.shiftRight(4));
                    chunk.setBrightness(layer, x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue(), level);

                    for (LevelListener listener : this.listeners) {
                        listener.tileChanged(x, y, z);
                    }
                }
            }
        }
    }

    @Override
    public int getRawBrightness(BigInteger x, int y, BigInteger z) {
        if (y < 0) {
            return 0;
        } else {
            if (y >= 128) {
                y = 127;
            }

            return this.getChunk(x.shiftRight(4), z.shiftRight(4)).getRawBrightness(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue(), 0);
        }
    }

    @Override
    public void updateLight(LightLayer type, BigInteger x0, int y0, BigInteger z0, BigInteger x1, int y1, BigInteger z1) {
        //? <1.0.0-beta.8.0.r
        this.updateLight(type, x0, y0, z0, x1, y1, z1, true);
    }
}
