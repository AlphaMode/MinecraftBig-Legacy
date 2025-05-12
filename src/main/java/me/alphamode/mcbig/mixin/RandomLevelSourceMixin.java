package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.BigChunkSourceExtension;
import me.alphamode.mcbig.extensions.BigPerlinNoiseExtension;
import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import me.alphamode.mcbig.math.BigConstants;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.LargeFeature;
import net.minecraft.world.level.levelgen.RandomLevelSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(RandomLevelSource.class)
public abstract class RandomLevelSourceMixin implements ChunkSource, BigChunkSourceExtension {
    @Shadow private Random random;

    @Shadow private Level level;

    @Shadow private Biome[] biomes;

    @Shadow private LargeFeature caveFeature;

    @Shadow private double[] buffer;

    @Shadow private double[] sr;

    @Shadow private double[] dr;

    @Shadow private double[] pnr;

    @Shadow private double[] ar;

    @Shadow private double[] br;

    @Shadow public PerlinNoise scaleNoise;

    @Shadow public PerlinNoise depthNoise;

    @Shadow private PerlinNoise perlinNoise1;

    @Shadow private PerlinNoise lperlinNoise1;

    @Shadow private PerlinNoise lperlinNoise2;

    @Shadow private double[] sandBuffer;

    @Shadow private double[] gravelBuffer;

    @Shadow private double[] depthBuffer;

    @Shadow private PerlinNoise perlinNoise2;

    @Shadow private PerlinNoise perlinNoise3;

    private double[] getHeights(double[] noise, BigInteger x, int y, BigInteger z, int l, int m, int n) {
        if (noise == null) {
            noise = new double[l * m * n];
        }

        double var8 = 684.412;
        double var10 = 684.412;
        double[] var12 = this.level.getBiomeSource().temperatures;
        double[] var13 = this.level.getBiomeSource().downfalls;
        this.sr = ((BigPerlinNoiseExtension)this.scaleNoise).getRegion(this.sr, x, z, l, n, 1.121, 1.121, 0.5);
        this.dr = ((BigPerlinNoiseExtension)this.depthNoise).getRegion(this.dr, x, z, l, n, 200.0, 200.0, 0.5);
        this.pnr = this.perlinNoise1.getRegion(this.pnr, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), l, m, n, var8 / 80.0, var10 / 160.0, var8 / 80.0);
        this.ar = this.lperlinNoise1.getRegion(this.ar, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), l, m, n, var8, var10, var8);
        this.br = this.lperlinNoise2.getRegion(this.br, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), l, m, n, var8, var10, var8);
        int var14 = 0;
        int var15 = 0;
        int var16 = 16 / l;

        for(int var17 = 0; var17 < l; ++var17) {
            int var18 = var17 * var16 + var16 / 2;

            for(int var19 = 0; var19 < n; ++var19) {
                int var20 = var19 * var16 + var16 / 2;
                double var21 = var12[var18 * 16 + var20];
                double var23 = var13[var18 * 16 + var20] * var21;
                double var25 = 1.0 - var23;
                var25 *= var25;
                var25 *= var25;
                var25 = 1.0 - var25;
                double var27 = (this.sr[var15] + 256.0) / 512.0;
                var27 *= var25;
                if (var27 > 1.0) {
                    var27 = 1.0;
                }

                double var29 = this.dr[var15] / 8000.0;
                if (var29 < 0.0) {
                    var29 = -var29 * 0.3;
                }

                var29 = var29 * 3.0 - 2.0;
                if (var29 < 0.0) {
                    var29 /= 2.0;
                    if (var29 < -1.0) {
                        var29 = -1.0;
                    }

                    var29 /= 1.4;
                    var29 /= 2.0;
                    var27 = 0.0;
                } else {
                    if (var29 > 1.0) {
                        var29 = 1.0;
                    }

                    var29 /= 8.0;
                }

                if (var27 < 0.0) {
                    var27 = 0.0;
                }

                var27 += 0.5;
                var29 = var29 * (double)m / 16.0;
                double var31 = (double)m / 2.0 + var29 * 4.0;
                ++var15;

                for(int var33 = 0; var33 < m; ++var33) {
                    double var34 = 0.0;
                    double var36 = ((double)var33 - var31) * 12.0 / var27;
                    if (var36 < 0.0) {
                        var36 *= 4.0;
                    }

                    double var38 = this.ar[var14] / 512.0;
                    double var40 = this.br[var14] / 512.0;
                    double var42 = (this.pnr[var14] / 10.0 + 1.0) / 2.0;
                    if (var42 < 0.0) {
                        var34 = var38;
                    } else if (var42 > 1.0) {
                        var34 = var40;
                    } else {
                        var34 = var38 + (var40 - var38) * var42;
                    }

                    var34 -= var36;
                    if (var33 > m - 4) {
                        double var44 = (double)((float)(var33 - (m - 4)) / 3.0F);
                        var34 = var34 * (1.0 - var44) + -10.0 * var44;
                    }

                    noise[var14] = var34;
                    ++var14;
                }
            }
        }

        return noise;
    }

    public void prepareHeights(BigInteger x, BigInteger z, byte[] tiles, Biome[] biomes, double[] ds) {
        int var6 = 4;
        int var7 = 64;
        int var8 = var6 + 1;
        int var9 = 17;
        int var10 = var6 + 1;
        this.buffer = this.getHeights(this.buffer, x.multiply(BigInteger.valueOf(var6)), 0, z.multiply(BigInteger.valueOf(var6)), var8, var9, var10);

        for(int var11 = 0; var11 < var6; ++var11) {
            for(int var12 = 0; var12 < var6; ++var12) {
                for(int var13 = 0; var13 < 16; ++var13) {
                    double var14 = 0.125;
                    double var16 = this.buffer[((var11 + 0) * var10 + var12 + 0) * var9 + var13 + 0];
                    double var18 = this.buffer[((var11 + 0) * var10 + var12 + 1) * var9 + var13 + 0];
                    double var20 = this.buffer[((var11 + 1) * var10 + var12 + 0) * var9 + var13 + 0];
                    double var22 = this.buffer[((var11 + 1) * var10 + var12 + 1) * var9 + var13 + 0];
                    double var24 = (this.buffer[((var11 + 0) * var10 + var12 + 0) * var9 + var13 + 1] - var16) * var14;
                    double var26 = (this.buffer[((var11 + 0) * var10 + var12 + 1) * var9 + var13 + 1] - var18) * var14;
                    double var28 = (this.buffer[((var11 + 1) * var10 + var12 + 0) * var9 + var13 + 1] - var20) * var14;
                    double var30 = (this.buffer[((var11 + 1) * var10 + var12 + 1) * var9 + var13 + 1] - var22) * var14;

                    for(int var32 = 0; var32 < 8; ++var32) {
                        double var33 = 0.25;
                        double var35 = var16;
                        double var37 = var18;
                        double var39 = (var20 - var16) * var33;
                        double var41 = (var22 - var18) * var33;

                        for(int var43 = 0; var43 < 4; ++var43) {
                            int var44 = var43 + var11 * 4 << 11 | 0 + var12 * 4 << 7 | var13 * 8 + var32;
                            short var45 = 128;
                            double var46 = 0.25;
                            double var48 = var35;
                            double var50 = (var37 - var35) * var46;

                            for(int var52 = 0; var52 < 4; ++var52) {
                                double var53 = ds[(var11 * 4 + var43) * 16 + var12 * 4 + var52];
                                int var55 = 0;
                                if (var13 * 8 + var32 < var7) {
                                    if (var53 < 0.5 && var13 * 8 + var32 >= var7 - 1) {
                                        var55 = Tile.ICE.id;
                                    } else {
                                        var55 = Tile.WATER.id;
                                    }
                                }

                                if (var48 > 0.0) {
                                    var55 = Tile.STONE.id;
                                }

                                tiles[var44] = (byte)var55;
                                var44 += var45;
                                var48 += var50;
                            }

                            var35 += var39;
                            var37 += var41;
                        }

                        var16 += var24;
                        var18 += var26;
                        var20 += var28;
                        var22 += var30;
                    }
                }
            }
        }
    }

    public void buildSurfaces(BigInteger x, BigInteger z, byte[] tiles, Biome[] biomes) {
        byte var5 = 64;
        double var6 = 0.03125;
        this.sandBuffer = this.perlinNoise2.getRegion(this.sandBuffer, x.multiply(BigConstants.SIXTEEN).doubleValue(), z.multiply(BigConstants.SIXTEEN).doubleValue(), 0.0, 16, 16, 1, var6, var6, 1.0);
        this.gravelBuffer = this.perlinNoise2.getRegion(this.gravelBuffer, x.multiply(BigConstants.SIXTEEN).doubleValue(), 109.0134, z.multiply(BigConstants.SIXTEEN).doubleValue(), 16, 1, 16, var6, 1.0, var6);
        this.depthBuffer = this.perlinNoise3.getRegion(this.depthBuffer, x.multiply(BigConstants.SIXTEEN).doubleValue(), z.multiply(BigConstants.SIXTEEN).doubleValue(), 0.0, 16, 16, 1, var6 * 2.0, var6 * 2.0, var6 * 2.0);

        for(int var8 = 0; var8 < 16; ++var8) {
            for(int var9 = 0; var9 < 16; ++var9) {
                Biome var10 = biomes[var8 + var9 * 16];
                boolean var11 = this.sandBuffer[var8 + var9 * 16] + this.random.nextDouble() * 0.2 > 0.0;
                boolean var12 = this.gravelBuffer[var8 + var9 * 16] + this.random.nextDouble() * 0.2 > 3.0;
                int var13 = (int)(this.depthBuffer[var8 + var9 * 16] / 3.0 + 3.0 + this.random.nextDouble() * 0.25);
                int var14 = -1;
                byte var15 = var10.topMaterial;
                byte var16 = var10.material;

                for(int var17 = 127; var17 >= 0; --var17) {
                    int pos = (var9 * 16 + var8) * 128 + var17;
                    if (var17 <= 0 + this.random.nextInt(5)) {
                        tiles[pos] = (byte)Tile.BEDROCK.id;
                    } else {
                        byte tile = tiles[pos];
                        if (tile == 0) {
                            var14 = -1;
                        } else if (tile == Tile.STONE.id) {
                            if (var14 == -1) {
                                if (var13 <= 0) {
                                    var15 = 0;
                                    var16 = (byte)Tile.STONE.id;
                                } else if (var17 >= var5 - 4 && var17 <= var5 + 1) {
                                    var15 = var10.topMaterial;
                                    var16 = var10.material;
                                    if (var12) {
                                        var15 = 0;
                                    }

                                    if (var12) {
                                        var16 = (byte)Tile.GRAVEL.id;
                                    }

                                    if (var11) {
                                        var15 = (byte)Tile.SAND.id;
                                    }

                                    if (var11) {
                                        var16 = (byte)Tile.SAND.id;
                                    }
                                }

                                if (var17 < var5 && var15 == 0) {
                                    var15 = (byte)Tile.WATER.id;
                                }

                                var14 = var13;
                                if (var17 >= var5 - 1) {
                                    tiles[pos] = var15;
                                } else {
                                    tiles[pos] = var16;
                                }
                            } else if (var14 > 0) {
                                --var14;
                                tiles[pos] = var16;
                                if (var14 == 0 && var16 == Tile.SAND.id) {
                                    var14 = this.random.nextInt(4);
                                    var16 = (byte)Tile.SANDSTONE.id;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean hasChunk(BigInteger x, BigInteger z) {
        return true;
    }

    @Override
    public LevelChunk getChunk(BigInteger x, BigInteger z) {
        this.random.setSeed(x.longValue() * 341873128712L + z.longValue() * 132897987541L);
        byte[] tiles = new byte[32768];
        BigLevelChunk chunk = new BigLevelChunk(this.level, tiles, x, z);
        this.biomes = this.level.getBiomeSource().getBiomeBlock(this.biomes, x.intValue() * 16, z.intValue() * 16, 16, 16);
        double[] temps = this.level.getBiomeSource().temperatures;
        prepareHeights(x, z, tiles, this.biomes, temps);
        buildSurfaces(x, z, tiles, this.biomes);
        this.caveFeature.apply(this, this.level, x.intValue(), z.intValue(), tiles);
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
}
