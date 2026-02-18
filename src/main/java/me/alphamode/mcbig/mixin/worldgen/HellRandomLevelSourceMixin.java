package me.alphamode.mcbig.mixin.worldgen;

import me.alphamode.mcbig.extensions.BigChunkSourceExtension;
import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import me.alphamode.mcbig.math.BigConstants;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.HellRandomLevelSource;
import net.minecraft.world.level.levelgen.LargeFeature;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.tile.SandTile;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(HellRandomLevelSource.class)
public class HellRandomLevelSourceMixin implements BigChunkSourceExtension {

    @Shadow
    private double[] buffer;

    @Shadow
    private double[] sandBuffer;

    @Shadow
    private double[] gravelBuffer;

    @Shadow
    private double[] depthBuffer;

    @Shadow
    public PerlinNoise depthNoise;

    @Shadow
    private PerlinNoise perlinNoise1;

    @Shadow
    private PerlinNoise perlinNoise2;

    @Shadow
    private PerlinNoise perlinNoise3;

    @Shadow
    private PerlinNoise lperlinNoise1;

    @Shadow
    private PerlinNoise lperlinNoise2;

    @Shadow
    public PerlinNoise scaleNoise;

    @Shadow
    private Random random;

    @Shadow
    private LargeFeature caveFeature;

    @Shadow
    private Level level;

    @Shadow
    private double[] sr;

    @Shadow
    private double[] dr;

    @Shadow
    private double[] pnr;

    @Shadow
    private double[] ar;

    @Shadow
    private double[] br;

    public void prepareHeights(BigInteger x, BigInteger z, byte[] tiles) {
        int var4 = 4;
        BigInteger bigvar4 = BigConstants.FOUR;
        int var5 = 32;
        int var6 = var4 + 1;
        int var7 = 17;
        int var8 = var4 + 1;
        this.buffer = this.getHeights(this.buffer, x.multiply(bigvar4), 0, z.multiply(bigvar4), var6, var7, var8);

        for (int var9 = 0; var9 < var4; var9++) {
            for (int var10 = 0; var10 < var4; var10++) {
                for (int var11 = 0; var11 < 16; var11++) {
                    double var12 = 0.125;
                    double var14 = this.buffer[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 0];
                    double var16 = this.buffer[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 0];
                    double var18 = this.buffer[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 0];
                    double var20 = this.buffer[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 0];
                    double var22 = (this.buffer[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 1] - var14) * var12;
                    double var24 = (this.buffer[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 1] - var16) * var12;
                    double var26 = (this.buffer[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 1] - var18) * var12;
                    double var28 = (this.buffer[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 1] - var20) * var12;

                    for (int var30 = 0; var30 < 8; var30++) {
                        double var31 = 0.25;
                        double var33 = var14;
                        double var35 = var16;
                        double var37 = (var18 - var14) * var31;
                        double var39 = (var20 - var16) * var31;

                        for (int var41 = 0; var41 < 4; var41++) {
                            int var42 = var41 + var9 * 4 << 11 | 0 + var10 * 4 << 7 | var11 * 8 + var30;
                            short var43 = 128;
                            double var44 = 0.25;
                            double var46 = var33;
                            double var48 = (var35 - var33) * var44;

                            for (int var50 = 0; var50 < 4; var50++) {
                                int var51 = 0;
                                if (var11 * 8 + var30 < var5) {
                                    var51 = Tile.LAVA.id;
                                }

                                if (var46 > 0.0) {
                                    var51 = Tile.NETHERRACK.id;
                                }

                                tiles[var42] = (byte)var51;
                                var42 += var43;
                                var46 += var48;
                            }

                            var33 += var37;
                            var35 += var39;
                        }

                        var14 += var22;
                        var16 += var24;
                        var18 += var26;
                        var20 += var28;
                    }
                }
            }
        }
    }

    public void buildSurfaces(BigInteger x, BigInteger z, byte[] tiles) {
        byte var4 = 64;
        double var5 = 0.03125;
        this.sandBuffer = this.perlinNoise2.getRegion(this.sandBuffer, x.doubleValue() * 16, z.doubleValue() * 16, 0.0, 16, 16, 1, var5, var5, 1.0);
        this.gravelBuffer = this.perlinNoise2.getRegion(this.gravelBuffer, x.doubleValue() * 16, 109.0134, z.doubleValue() * 16, 16, 1, 16, var5, 1.0, var5);
        this.depthBuffer = this.perlinNoise3.getRegion(this.depthBuffer, x.doubleValue() * 16, z.doubleValue() * 16, 0.0, 16, 16, 1, var5 * 2.0, var5 * 2.0, var5 * 2.0);

        for (int var7 = 0; var7 < 16; var7++) {
            for (int var8 = 0; var8 < 16; var8++) {
                boolean var9 = this.sandBuffer[var7 + var8 * 16] + this.random.nextDouble() * 0.2 > 0.0;
                boolean var10 = this.gravelBuffer[var7 + var8 * 16] + this.random.nextDouble() * 0.2 > 0.0;
                int var11 = (int)(this.depthBuffer[var7 + var8 * 16] / 3.0 + 3.0 + this.random.nextDouble() * 0.25);
                int var12 = -1;
                byte var13 = (byte)Tile.NETHERRACK.id;
                byte var14 = (byte)Tile.NETHERRACK.id;

                for (int var15 = 127; var15 >= 0; var15--) {
                    int var16 = (var8 * 16 + var7) * 128 + var15;
                    if (var15 >= 127 - this.random.nextInt(5)) {
                        tiles[var16] = (byte)Tile.BEDROCK.id;
                    } else if (var15 <= 0 + this.random.nextInt(5)) {
                        tiles[var16] = (byte)Tile.BEDROCK.id;
                    } else {
                        byte var17 = tiles[var16];
                        if (var17 == 0) {
                            var12 = -1;
                        } else if (var17 == Tile.NETHERRACK.id) {
                            if (var12 == -1) {
                                if (var11 <= 0) {
                                    var13 = 0;
                                    var14 = (byte)Tile.NETHERRACK.id;
                                } else if (var15 >= var4 - 4 && var15 <= var4 + 1) {
                                    var13 = (byte)Tile.NETHERRACK.id;
                                    var14 = (byte)Tile.NETHERRACK.id;
                                    if (var10) {
                                        var13 = (byte)Tile.GRAVEL.id;
                                    }

                                    if (var10) {
                                        var14 = (byte)Tile.NETHERRACK.id;
                                    }

                                    if (var9) {
                                        var13 = (byte)Tile.SOUL_SAND.id;
                                    }

                                    if (var9) {
                                        var14 = (byte)Tile.SOUL_SAND.id;
                                    }
                                }

                                if (var15 < var4 && var13 == 0) {
                                    var13 = (byte)Tile.LAVA.id;
                                }

                                var12 = var11;
                                if (var15 >= var4 - 1) {
                                    tiles[var16] = var13;
                                } else {
                                    tiles[var16] = var14;
                                }
                            } else if (var12 > 0) {
                                var12--;
                                tiles[var16] = var14;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public LevelChunk loadChunk(BigInteger x, BigInteger z) {
        return this.getChunk(x, z);
    }

    @Override
    public LevelChunk getChunk(BigInteger x, BigInteger z) {
        this.random.setSeed(x.longValue() * 341873128712L + z.longValue() * 132897987541L);
        byte[] tiles = new byte[32768];
        this.prepareHeights(x, z, tiles);
        this.buildSurfaces(x, z, tiles);
        this.caveFeature.apply((ChunkSource) this, this.level, x, z, tiles);
        return new BigLevelChunk(this.level, tiles, x, z);
    }

    private double[] getHeights(double[] ds, BigInteger i, int j, BigInteger k, int l, int m, int n) {
        if (ds == null) {
            ds = new double[l * m * n];
        }

        double var8 = 684.412;
        double var10 = 2053.236;
        this.sr = this.scaleNoise.getRegion(this.sr, i.doubleValue(), j, k.doubleValue(), l, 1, n, 1.0, 0.0, 1.0);
        this.dr = this.depthNoise.getRegion(this.dr, i.doubleValue(), j, k.doubleValue(), l, 1, n, 100.0, 0.0, 100.0);
        this.pnr = this.perlinNoise1.getRegion(this.pnr, i.doubleValue(), j, k.doubleValue(), l, m, n, var8 / 80.0, var10 / 60.0, var8 / 80.0);
        this.ar = this.lperlinNoise1.getRegion(this.ar, i.doubleValue(), j, k.doubleValue(), l, m, n, var8, var10, var8);
        this.br = this.lperlinNoise2.getRegion(this.br, i.doubleValue(), j, k.doubleValue(), l, m, n, var8, var10, var8);
        int var12 = 0;
        int var13 = 0;
        double[] var14 = new double[m];

        for (int var15 = 0; var15 < m; var15++) {
            var14[var15] = Math.cos(var15 * Math.PI * 6.0 / m) * 2.0;
            double var16 = var15;
            if (var15 > m / 2) {
                var16 = m - 1 - var15;
            }

            if (var16 < 4.0) {
                var16 = 4.0 - var16;
                var14[var15] -= var16 * var16 * var16 * 10.0;
            }
        }

        for (int var36 = 0; var36 < l; var36++) {
            for (int var38 = 0; var38 < n; var38++) {
                double var17 = (this.sr[var13] + 256.0) / 512.0;
                if (var17 > 1.0) {
                    var17 = 1.0;
                }

                double var19 = 0.0;
                double var21 = this.dr[var13] / 8000.0;
                if (var21 < 0.0) {
                    var21 = -var21;
                }

                var21 = var21 * 3.0 - 3.0;
                if (var21 < 0.0) {
                    var21 /= 2.0;
                    if (var21 < -1.0) {
                        var21 = -1.0;
                    }

                    var21 /= 1.4;
                    var21 /= 2.0;
                    var17 = 0.0;
                } else {
                    if (var21 > 1.0) {
                        var21 = 1.0;
                    }

                    var21 /= 6.0;
                }

                var17 += 0.5;
                var21 = var21 * m / 16.0;
                var13++;

                for (int var23 = 0; var23 < m; var23++) {
                    double var24 = 0.0;
                    double var26 = var14[var23];
                    double var28 = this.ar[var12] / 512.0;
                    double var30 = this.br[var12] / 512.0;
                    double var32 = (this.pnr[var12] / 10.0 + 1.0) / 2.0;
                    if (var32 < 0.0) {
                        var24 = var28;
                    } else if (var32 > 1.0) {
                        var24 = var30;
                    } else {
                        var24 = var28 + (var30 - var28) * var32;
                    }

                    var24 -= var26;
                    if (var23 > m - 4) {
                        double var34 = (var23 - (m - 4)) / 3.0F;
                        var24 = var24 * (1.0 - var34) + -10.0 * var34;
                    }

                    if (var23 < var19) {
                        double var47 = (var19 - var23) / 4.0;
                        if (var47 < 0.0) {
                            var47 = 0.0;
                        }

                        if (var47 > 1.0) {
                            var47 = 1.0;
                        }

                        var24 = var24 * (1.0 - var47) + -10.0 * var47;
                    }

                    ds[var12] = var24;
                    var12++;
                }
            }
        }

        return ds;
    }

    @Override
    public boolean hasChunk(BigInteger x, BigInteger z) {
        return true;
    }

    @Override
    public void postProcess(ChunkSource generator, BigInteger xc, BigInteger zc) {
        SandTile.instaFall = true;
        BigInteger xt = xc.multiply(BigConstants.SIXTEEN);
        BigInteger zt = zc.multiply(BigConstants.SIXTEEN);

        for (int i = 0; i < 8; i++) {
            int x = xt.intValue() + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(120) + 4;
            int z = zt.intValue() + this.random.nextInt(16) + 8;
            new HellSpringFeature(Tile.FLOWING_LAVA.id).place(this.level, this.random, x, y, z);
        }

        int rand = this.random.nextInt(this.random.nextInt(10) + 1) + 1;

        for (int var13 = 0; var13 < rand; var13++) {
            int var18 = xt.intValue() + this.random.nextInt(16) + 8;
            int var23 = this.random.nextInt(120) + 4;
            int var10 = zt.intValue() + this.random.nextInt(16) + 8;
            new HellFireFeature().place(this.level, this.random, var18, var23, var10);
        }

        rand = this.random.nextInt(this.random.nextInt(10) + 1);

        for (int i = 0; i < rand; i++) {
            int x = xt.intValue() + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(120) + 4;
            int z = zt.intValue() + this.random.nextInt(16) + 8;
            new LightGemFeature().place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 10; i++) {
            int x = xt.intValue() + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(128);
            int z = zt.intValue() + this.random.nextInt(16) + 8;
            new HellPortalFeature().place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(1) == 0) {
            int x = xt.intValue() + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(128);
            int z = zt.intValue() + this.random.nextInt(16) + 8;
            new FlowerFeature(Tile.BROWN_MUSHROOM.id).place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(1) == 0) {
            int x = xt.intValue() + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(128);
            int z = zt.intValue() + this.random.nextInt(16) + 8;
            new FlowerFeature(Tile.RED_MUSHROOM.id).place(this.level, this.random, x, y, z);
        }

        SandTile.instaFall = false;
    }
}
