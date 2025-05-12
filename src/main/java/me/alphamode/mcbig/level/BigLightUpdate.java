package me.alphamode.mcbig.level;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.tile.Tile;

import java.math.BigInteger;

public class BigLightUpdate {
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
        int var2 = this.x1.subtract(this.x0).add(BigInteger.ONE).intValue();
        int var3 = this.y1 - this.y0 + 1;
        int var4 = this.z1.subtract(this.z0).add(BigInteger.ONE).intValue();
        int var5 = var2 * var3 * var4;
        if (var5 > 32768) {
            System.out.println("Light too large, skipping!");
        } else {
            BigInteger var6 = BigInteger.ZERO;
            BigInteger var7 = BigInteger.ZERO;
            boolean var8 = false;
            boolean var9 = false;

            for(BigInteger var10 = this.x0; var10.compareTo(this.x1) <= 0; var10 = var10.add(BigInteger.ONE)) {
                for(BigInteger var11 = this.z0; var11.compareTo(this.z1) <= 0; var11 = var11.add(BigInteger.ONE)) {
                    BigInteger var12 = var10.shiftRight(4);
                    BigInteger var13 = var11.shiftRight(4);
                    boolean var14 = false;
                    if (var8 && var12.equals(var6) && var13.equals(var7)) {
                        var14 = var9;
                    } else {
                        var14 = level.hasChunksAt(var10, 0, var11, 1);
                        if (var14) {
                            LevelChunk var15 = level.getChunk(var10.shiftRight(4), var11.shiftRight(4));
                            if (var15.isEmpty()) {
                                var14 = false;
                            }
                        }

                        var9 = var14;
                        var6 = var12;
                        var7 = var13;
                    }

                    if (var14) {
                        if (this.y0 < 0) {
                            this.y0 = 0;
                        }

                        if (this.y1 >= 128) {
                            this.y1 = 127;
                        }

                        for(int var28 = this.y0; var28 <= this.y1; ++var28) {
                            int var16 = level.getBrightness(this.type, var10, var28, var11);
                            int var17 = 0;
                            int var18 = level.getTile(var10, var28, var11);
                            int var19 = Tile.lightBlock[var18];
                            if (var19 == 0) {
                                var19 = 1;
                            }

                            int var20 = 0;
                            if (this.type == LightLayer.SKY) {
                                if (level.isSkyLit(var10, var28, var11)) {
                                    var20 = 15;
                                }
                            } else if (this.type == LightLayer.BLOCK) {
                                var20 = Tile.lightEmission[var18];
                            }

                            if (var19 >= 15 && var20 == 0) {
                                var17 = 0;
                            } else {
                                int var21 = level.getBrightness(this.type, var10.subtract(BigInteger.ONE), var28, var11);
                                int var22 = level.getBrightness(this.type, var10.add(BigInteger.ONE), var28, var11);
                                int var23 = level.getBrightness(this.type, var10, var28 - 1, var11);
                                int var24 = level.getBrightness(this.type, var10, var28 + 1, var11);
                                int var25 = level.getBrightness(this.type, var10, var28, var11.subtract(BigInteger.ONE));
                                int var26 = level.getBrightness(this.type, var10, var28, var11.add(BigInteger.ONE));
                                var17 = var21;
                                if (var22 > var21) {
                                    var17 = var22;
                                }

                                if (var23 > var17) {
                                    var17 = var23;
                                }

                                if (var24 > var17) {
                                    var17 = var24;
                                }

                                if (var25 > var17) {
                                    var17 = var25;
                                }

                                if (var26 > var17) {
                                    var17 = var26;
                                }

                                var17 -= var19;
                                if (var17 < 0) {
                                    var17 = 0;
                                }

                                if (var20 > var17) {
                                    var17 = var20;
                                }
                            }

                            if (var16 != var17) {
                                level.setBrightness(this.type, var10, var28, var11, var17);
                                int var31 = var17 - 1;
                                if (var31 < 0) {
                                    var31 = 0;
                                }

                                level.updateLightIfOtherThan(this.type, var10.subtract(BigInteger.ONE), var28, var11, var31);
                                level.updateLightIfOtherThan(this.type, var10, var28 - 1, var11, var31);
                                level.updateLightIfOtherThan(this.type, var10, var28, var11.subtract(BigInteger.ONE), var31);
                                if (var10.add(BigInteger.ONE).compareTo(this.x1) >= 0) {
                                    level.updateLightIfOtherThan(this.type, var10.add(BigInteger.ONE), var28, var11, var31);
                                }

                                if (var28 + 1 >= this.y1) {
                                    level.updateLightIfOtherThan(this.type, var10, var28 + 1, var11, var31);
                                }

                                if (var11.add(BigInteger.ONE).compareTo(this.z1) >= 0) {
                                    level.updateLightIfOtherThan(this.type, var10, var28, var11.add(BigInteger.ONE), var31);
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

