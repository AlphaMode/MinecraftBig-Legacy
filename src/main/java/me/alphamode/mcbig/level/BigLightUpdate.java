package me.alphamode.mcbig.level;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.tile.Tile;

import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BigLightUpdate {
    public static final ExecutorService LIGHT_EXECUTOR = Executors.newCachedThreadPool();
    public final LightLayer type;
    public BigInteger x0;
    public int y0;
    public BigInteger z0;
    public BigInteger x1;
    public int y1;
    public BigInteger z1;

    public BigLightUpdate(LightLayer type, BigInteger x0, int y0, BigInteger z0, BigInteger x1, int y1, BigInteger z1) {
        this.type = type;
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
    }

    public void update(Level level) {
        int mx = this.x1.subtract(this.x0).add(BigInteger.ONE).intValue();
        int my = this.y1 - this.y0 + 1;
        int mz = this.z1.subtract(this.z0).add(BigInteger.ONE).intValue();
        int area = mx * my * mz;
        if (area > 32768) {
            System.out.println("Light too large, skipping!");
        } else {
            BigInteger lxc = BigInteger.ZERO;
            BigInteger lzc = BigInteger.ZERO;
            boolean var8 = false;
            boolean var9 = false;

            for(BigInteger x = this.x0; x.compareTo(this.x1) <= 0; x = x.add(BigInteger.ONE)) {
                for(BigInteger z = this.z0; z.compareTo(this.z1) <= 0; z = z.add(BigInteger.ONE)) {
                    BigInteger xc = x.shiftRight(4);
                    BigInteger zc = z.shiftRight(4);
                    boolean hasChunk = false;
                    if (var8 && xc.equals(lxc) && zc.equals(lzc)) {
                        hasChunk = var9;
                    } else {
                        hasChunk = level.hasChunksAt(x, 0, z, 1);
                        if (hasChunk) {
                            LevelChunk var15 = level.getChunk(x.shiftRight(4), z.shiftRight(4));
                            if (var15.isEmpty()) {
                                hasChunk = false;
                            }
                        }

                        var9 = hasChunk;
                        lxc = xc;
                        lzc = zc;
                    }

                    if (hasChunk) {
                        if (this.y0 < 0) {
                            this.y0 = 0;
                        }

                        if (this.y1 >= 128) {
                            this.y1 = 127;
                        }

                        for(int y = this.y0; y <= this.y1; ++y) {
                            int oldBr = level.getBrightness(this.type, x, y, z);
                            int newBr = 0;
                            int t = level.getTile(x, y, z);
                            int blockLight = Tile.lightBlock[t];
                            if (blockLight == 0) {
                                blockLight = 1;
                            }

                            int emissionLevel = 0;
                            if (this.type == LightLayer.SKY) {
                                if (level.isSkyLit(x, y, z)) {
                                    emissionLevel = 15;
                                }
                            } else if (this.type == LightLayer.BLOCK) {
                                emissionLevel = Tile.lightEmission[t];
                            }

                            if (blockLight >= 15 && emissionLevel == 0) {
                                newBr = 0;
                            } else {
                                int westBr = level.getBrightness(this.type, x.subtract(BigInteger.ONE), y, z);
                                int eastBr = level.getBrightness(this.type, x.add(BigInteger.ONE), y, z);
                                int downBr = level.getBrightness(this.type, x, y - 1, z);
                                int upBr = level.getBrightness(this.type, x, y + 1, z);
                                int northBr = level.getBrightness(this.type, x, y, z.subtract(BigInteger.ONE));
                                int southBr = level.getBrightness(this.type, x, y, z.add(BigInteger.ONE));
                                newBr = westBr;
                                if (eastBr > westBr) {
                                    newBr = eastBr;
                                }

                                if (downBr > newBr) {
                                    newBr = downBr;
                                }

                                if (upBr > newBr) {
                                    newBr = upBr;
                                }

                                if (northBr > newBr) {
                                    newBr = northBr;
                                }

                                if (southBr > newBr) {
                                    newBr = southBr;
                                }

                                newBr -= blockLight;
                                if (newBr < 0) {
                                    newBr = 0;
                                }

                                if (emissionLevel > newBr) {
                                    newBr = emissionLevel;
                                }
                            }

                            if (oldBr != newBr) {
                                level.setBrightness(this.type, x, y, z, newBr);
                                int l = newBr - 1;
                                if (l < 0) {
                                    l = 0;
                                }

                                level.updateLightIfOtherThan(this.type, x.subtract(BigInteger.ONE), y, z, l);
                                level.updateLightIfOtherThan(this.type, x, y - 1, z, l);
                                level.updateLightIfOtherThan(this.type, x, y, z.subtract(BigInteger.ONE), l);
                                if (x.add(BigInteger.ONE).compareTo(this.x1) >= 0) {
                                    level.updateLightIfOtherThan(this.type, x.add(BigInteger.ONE), y, z, l);
                                }

                                if (y + 1 >= this.y1) {
                                    level.updateLightIfOtherThan(this.type, x, y + 1, z, l);
                                }

                                if (z.add(BigInteger.ONE).compareTo(this.z1) >= 0) {
                                    level.updateLightIfOtherThan(this.type, x, y, z.add(BigInteger.ONE), l);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean expandToContain(BigInteger i, int j, BigInteger k, BigInteger l, int m, BigInteger n) {
        if (i.compareTo(this.x0) >= 0 && j >= this.y0 && k.compareTo(this.z0) >= 0 && l.compareTo(this.x1) <= 0 && m <= this.y1 && n.compareTo(this.z1) <= 0) {
            return true;
        } else {
            byte var7 = 1;
            if (i.compareTo(this.x0.subtract(BigInteger.ONE)) >= 0 && j >= this.y0 - var7 && k.compareTo(this.z0.subtract(BigInteger.ONE)) >= 0 && l.compareTo(this.x1.add(BigInteger.ONE)) <= 0 && m <= this.y1 + var7 && n.compareTo(this.z1.add(BigInteger.ONE)) <= 0) {
                int var8 = this.x1.subtract(this.x0).intValue();
                int var9 = this.y1 - this.y0;
                int var10 = this.z1.subtract(this.z0).intValue();
                if (i.compareTo(this.x0) > 0) {
                    i = this.x0;
                }

                if (j > this.y0) {
                    j = this.y0;
                }

                if (k.compareTo(this.z0) > 0) {
                    k = this.z0;
                }

                if (l.compareTo(this.x1) < 0) {
                    l = this.x1;
                }

                if (m < this.y1) {
                    m = this.y1;
                }

                if (n.compareTo(this.z1) < 0) {
                    n = this.z1;
                }

                int var11 = l.subtract(i).intValue();
                int var12 = m - j;
                int var13 = n.subtract(k).intValue();
                int var14 = var8 * var9 * var10;
                int var15 = var11 * var12 * var13;
                if (var15 - var14 <= 2) {
                    this.x0 = i;
                    this.y0 = j;
                    this.z0 = k;
                    this.x1 = l;
                    this.y1 = m;
                    this.z1 = n;
                    return true;
                }
            }

            return false;
        }
    }
}

