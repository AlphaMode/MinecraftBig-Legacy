package me.alphamode.mcbig.world.level.levelgen.vanilla;

import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.level.levelgen.McBigChunkSource;
import me.alphamode.mcbig.world.level.levelgen.synth.BigPerlinNoise;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.LargeCaveFeature;
import net.minecraft.world.level.levelgen.LargeFeature;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.SandTile;
import net.minecraft.world.level.tile.Tile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

public class BigRandomLevelSource implements McBigChunkSource {
    public static final int CHUNK_HEIGHT = 8;
    public static final int CHUNK_WIDTH = 4;
    private static final BigDecimal GRAVEL_Y = new BigDecimal("109.0134");
    private Random random;
    private BigPerlinNoise lperlinNoise1;
    private BigPerlinNoise lperlinNoise2;
    private BigPerlinNoise perlinNoise1;
    private BigPerlinNoise perlinNoise2;
    private BigPerlinNoise perlinNoise3;
    public BigPerlinNoise scaleNoise;
    public BigPerlinNoise depthNoise;
    public BigPerlinNoise forestNoise;
    private Level level;
    private double[] buffer;
    private double[] sandBuffer = new double[256];
    private double[] gravelBuffer = new double[256];
    private double[] depthBuffer = new double[256];
    private LargeFeature caveFeature = new LargeCaveFeature();
    private Biome[] biomes;
    double[] pnr;
    double[] ar;
    double[] br;
    double[] sr;
    double[] dr;
    int[][] waterDepths = new int[32][32];
    private double[] temperatures;

    public BigRandomLevelSource(Level level, long seed) {
        this.level = level;
        this.random = new Random(seed);
        this.lperlinNoise1 = new BigPerlinNoise(this.random, 16);
        this.lperlinNoise2 = new BigPerlinNoise(this.random, 16);
        this.perlinNoise1 = new BigPerlinNoise(this.random, 8);
        this.perlinNoise2 = new BigPerlinNoise(this.random, 4);
        this.perlinNoise3 = new BigPerlinNoise(this.random, 4);
        this.scaleNoise = new BigPerlinNoise(this.random, 10);
        this.depthNoise = new BigPerlinNoise(this.random, 16);
        this.forestNoise = new BigPerlinNoise(this.random, 8);
    }

    private double[] getHeights(double[] buffer, BigInteger x, int y, BigInteger z, int xSize, int ySize, int zSize) {
        if (buffer == null) {
            buffer = new double[xSize * ySize * zSize];
        }

        // Scale
        double s = 684.412;
        double hs = 684.412;

        double[] temperatures = this.level.getBiomeSource().temperatures;
        double[] downfalls = this.level.getBiomeSource().downfalls;
        BigDecimal xD = new BigDecimal(x);
        BigDecimal yD = new BigDecimal(y);
        BigDecimal zD = new BigDecimal(z);

        this.sr = this.scaleNoise.getRegion(this.sr, x, z, xSize, zSize, 1.121, 1.121, 0.5);
        this.dr = this.depthNoise.getRegion(this.dr, x, z, xSize, zSize, 200.0, 200.0, 0.5);

        this.pnr = this.perlinNoise1.getRegion(this.pnr, xD, yD, zD, xSize, ySize, zSize, s / 80.0, hs / 160.0, s / 80.0);
        this.ar = this.lperlinNoise1.getRegion(this.ar, xD, yD, zD, xSize, ySize, zSize, s, hs, s);
        this.br = this.lperlinNoise2.getRegion(this.br, xD, yD, zD, xSize, ySize, zSize, s, hs, s);

        int p = 0;
        int pp = 0;

        int wScale = 16 / xSize;

        for(int xx = 0; xx < xSize; ++xx) {
            int xp = xx * wScale + wScale / 2;

            for(int zz = 0; zz < zSize; ++zz) {
                int zp = zz * wScale + wScale / 2;
                double temperature = temperatures[xp * 16 + zp];
                double downfall = downfalls[xp * 16 + zp] * temperature;
                double dd = 1.0 - downfall;
                dd *= dd;
                dd *= dd;
                dd = 1.0 - dd;
                double scale = (this.sr[pp] + 256.0) / 512.0;
                scale *= dd;
                if (scale > 1.0) {
                    scale = 1.0;
                }

                double depth = this.dr[pp] / 8000.0;
                if (depth < 0.0) {
                    depth = -depth * 0.3;
                }

                depth = depth * 3.0 - 2.0;
                if (depth < 0.0) {
                    depth /= 2.0;
                    if (depth < -1.0) {
                        depth = -1.0;
                    }

                    depth /= 1.4;
                    depth /= 2.0;
                    scale = 0.0;
                } else {
                    if (depth > 1.0) {
                        depth = 1.0;
                    }

                    depth /= 8.0;
                }

                if (scale < 0.0) {
                    scale = 0.0;
                }

                scale += 0.5;
                depth = depth * (double)ySize / 16.0;
                double yCenter = (double)ySize / 2.0 + depth * 4.0;
                ++pp;

                for(int yy = 0; yy < ySize; ++yy) {
                    double val = 0.0;
                    double yOffs = ((double)yy - yCenter) * 12.0 / scale;
                    if (yOffs < 0.0) {
                        yOffs *= 4.0;
                    }

                    double bb = this.ar[p] / 512.0;
                    double cc = this.br[p] / 512.0;
                    double v = (this.pnr[p] / 10.0 + 1.0) / 2.0;

                    if (v < 0.0) {
                        val = bb;
                    } else if (v > 1.0) {
                        val = cc;
                    } else {
                        val = bb + (cc - bb) * v;
                    }

                    val -= yOffs;
                    if (yy > ySize - 4) {
                        double slide = (float)(yy - (ySize - 4)) / 3.0F;
                        val = val * (1.0 - slide) + -10.0 * slide;
                    }

                    buffer[p] = val;
                    ++p;
                }
            }
        }

        return buffer;
    }

    public void prepareHeights(BigInteger xOffs, BigInteger zOffs, byte[] blocks, Biome[] biomes, double[] temperatures) {
        int xChunks = 16 / CHUNK_WIDTH;
        int waterHeight = 64;

        int xSize = xChunks + 1;
        int ySize = 17;
        int zSize = xChunks + 1;
        this.buffer = this.getHeights(this.buffer, xOffs.multiply(BigInteger.valueOf(xChunks)), 0, zOffs.multiply(BigInteger.valueOf(xChunks)), xSize, ySize, zSize);

        for(int xc = 0; xc < xChunks; ++xc) {
            for(int zc = 0; zc < xChunks; ++zc) {
                for(int yc = 0; yc < 16; ++yc) {
                    double yStep = 1 / (double) CHUNK_HEIGHT;
                    double s0 = this.buffer[((xc + 0) * zSize + zc + 0) * ySize + yc + 0];
                    double s1 = this.buffer[((xc + 0) * zSize + zc + 1) * ySize + yc + 0];
                    double s2 = this.buffer[((xc + 1) * zSize + zc + 0) * ySize + yc + 0];
                    double s3 = this.buffer[((xc + 1) * zSize + zc + 1) * ySize + yc + 0];

                    double s0a = (this.buffer[((xc + 0) * zSize + zc + 0) * ySize + yc + 1] - s0) * yStep;
                    double s1a = (this.buffer[((xc + 0) * zSize + zc + 1) * ySize + yc + 1] - s1) * yStep;
                    double s2a = (this.buffer[((xc + 1) * zSize + zc + 0) * ySize + yc + 1] - s2) * yStep;
                    double s3a = (this.buffer[((xc + 1) * zSize + zc + 1) * ySize + yc + 1] - s3) * yStep;

                    for(int y = 0; y < CHUNK_HEIGHT; ++y) {
                        double xStep = 1 / (double) CHUNK_WIDTH;

                        double _s0 = s0;
                        double _s1 = s1;
                        double _s0a = (s2 - s0) * xStep;
                        double _s1a = (s3 - s1) * xStep;

                        for(int x = 0; x < CHUNK_WIDTH; ++x) {
                            int offs = x + xc * CHUNK_WIDTH << 11 | 0 + zc * CHUNK_WIDTH << 7 | yc * CHUNK_HEIGHT + y;
                            int step = 1 << 7;
                            double zStep = 1 / (double) CHUNK_WIDTH;
                            double val = _s0;
                            double vala = (_s1 - _s0) * zStep;

                            for(int z = 0; z < CHUNK_WIDTH; ++z) {
                                double temp = temperatures[(xc * 4 + x) * 16 + zc * 4 + z];
                                int tileId = 0;
                                if (yc * 8 + y < waterHeight) {
                                    if (temp < 0.5 && yc * 8 + y >= waterHeight - 1) {
                                        tileId = Tile.ice.id;
                                    } else {
                                        tileId = Tile.calmWater.id;
                                    }
                                }

                                if (val > 0.0) {
                                    tileId = Tile.stone.id;
                                }

                                blocks[offs] = (byte)tileId;
                                offs += step;
                                val += vala;
                            }

                            _s0 += _s0a;
                            _s1 += _s1a;
                        }

                        s0 += s0a;
                        s1 += s1a;
                        s2 += s2a;
                        s3 += s3a;
                    }
                }
            }
        }
    }

    public void buildSurfaces(BigInteger xOffs, BigInteger zOffs, byte[] blocks, Biome[] biomes) {
        int waterHeight = 64;
        double s = 1 / 32.0;

        this.sandBuffer = this.perlinNoise2.getRegion(this.sandBuffer, BigMath.decimalW(xOffs.multiply(BigConstants.SIXTEEN)), BigMath.decimalW(zOffs.multiply(BigConstants.SIXTEEN)), BigDecimal.ZERO, 16, 16, 1, s, s, 1.0);
        this.gravelBuffer = this.perlinNoise2.getRegion(this.gravelBuffer, BigMath.decimalW(xOffs.multiply(BigConstants.SIXTEEN)), GRAVEL_Y, BigMath.decimalW(zOffs.multiply(BigConstants.SIXTEEN)), 16, 1, 16, s, 1.0, s);
        this.depthBuffer = this.perlinNoise3.getRegion(this.depthBuffer, BigMath.decimalW(xOffs.multiply(BigConstants.SIXTEEN)), BigMath.decimalW(zOffs.multiply(BigConstants.SIXTEEN)), BigDecimal.ZERO, 16, 16, 1, s * 2.0, s * 2.0, s * 2.0);

        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                Biome b = biomes[x + z * 16];
                boolean sand = this.sandBuffer[x + z * 16] + this.random.nextDouble() * 0.2 > 0.0;
                boolean gravel = this.gravelBuffer[x + z * 16] + this.random.nextDouble() * 0.2 > 3.0;
                int runDepth = (int)(this.depthBuffer[x + z * 16] / 3.0 + 3.0 + this.random.nextDouble() * 0.25);
                int run = -1;
                byte top = b.topMaterial;
                byte material = b.material;

                for(int y = 127; y >= 0; --y) {
                    int offs = (z * 16 + x) * 128 + y;
                    if (y <= 0 + this.random.nextInt(5)) {
                        blocks[offs] = (byte)Tile.unbreakable.id;
                    } else {
                        byte old = blocks[offs];
                        if (old == 0) {
                            run = -1;
                        } else if (old == Tile.stone.id) {
                            if (run == -1) {
                                if (runDepth <= 0) {
                                    top = 0;
                                    material = (byte)Tile.stone.id;
                                } else if (y >= waterHeight - 4 && y <= waterHeight + 1) {
                                    top = b.topMaterial;
                                    material = b.material;
                                    if (gravel) {
                                        top = 0;
                                    }

                                    if (gravel) {
                                        material = (byte)Tile.gravel.id;
                                    }

                                    if (sand) {
                                        top = (byte)Tile.sand.id;
                                    }

                                    if (sand) {
                                        material = (byte)Tile.sand.id;
                                    }
                                }

                                if (y < waterHeight && top == 0) {
                                    top = (byte)Tile.calmWater.id;
                                }

                                run = runDepth;
                                if (y >= waterHeight - 1) {
                                    blocks[offs] = top;
                                } else {
                                    blocks[offs] = material;
                                }
                            } else if (run > 0) {
                                --run;
                                blocks[offs] = material;

                                // place a few sandstone blocks beneath sand
                                // runs
                                if (run == 0 && material == Tile.sand.id) {
                                    run = this.random.nextInt(4);
                                    material = (byte)Tile.sandStone.id;
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
        this.biomes = this.level.getBiomeSource().getBiomeBlock(this.biomes, x.multiply(BigConstants.SIXTEEN), z.multiply(BigConstants.SIXTEEN), 16, 16);
        double[] temps = this.level.getBiomeSource().temperatures;
        prepareHeights(x, z, tiles, this.biomes, temps);
        buildSurfaces(x, z, tiles, this.biomes);
        this.caveFeature.apply(this, this.level, x, z, tiles);
        chunk.recalcHeightmap();
        return chunk;
    }

    @Override
    public LevelChunk loadChunk(BigInteger x, BigInteger z) {
        return getChunk(x, z);
    }

    @Override
    public void postProcess(ChunkSource generator, BigInteger xc, BigInteger zc) {
        SandTile.instaFall = true;
        BigInteger xt = xc.multiply(BigConstants.SIXTEEN);
        BigInteger zt = zc.multiply(BigConstants.SIXTEEN);
        Biome biome = this.level.getBiomeSource().getBiome(xt.add(BigConstants.SIXTEEN), zt.add(BigConstants.SIXTEEN));
        this.random.setSeed(this.level.getSeed());
        long var7 = this.random.nextLong() / 2L * 2L + 1L;
        long var9 = this.random.nextLong() / 2L * 2L + 1L;
        this.random.setSeed((long)xc.longValue() * var7 + (long)zc.longValue() * var9 ^ this.level.getSeed());
        double var11 = 0.25;
        if (this.random.nextInt(4) == 0) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new LakeFeature(Tile.calmWater.id).place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(8) == 0) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(this.random.nextInt(120) + 8);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            if (y < 64 || this.random.nextInt(10) == 0) {
                new LakeFeature(Tile.calmLava.id).place(this.level, this.random, x, y, z);
            }
        }

        for(int i = 0; i < 8; ++i) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new MonsterRoomFeature().place(this.level, this.random, x, y, z);
        }
        // TODO: Big Int features after this

        for(int i = 0; i < 10; ++i) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(128);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16)));
            new ClayFeature(32).place(this.level, this.random, x, y, z);
        }

        for(int i = 0; i < 20; ++i) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(128);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16)));
            new OreFeature(Tile.dirt.id, 32).place(this.level, this.random, x, y, z);
        }

        for(int i = 0; i < 10; ++i) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(128);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16)));
            new OreFeature(Tile.gravel.id, 32).place(this.level, this.random, x, y, z);
        }

        for(int i = 0; i < 20; ++i) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(128);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16)));
            new OreFeature(Tile.coalOre.id, 16).place(this.level, this.random, x, y, z);
        }

        for(int i = 0; i < 20; ++i) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(64);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16)));
            new OreFeature(Tile.ironOre.id, 8).place(this.level, this.random, x, y, z);
        }

        for(int i = 0; i < 2; ++i) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(32);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16)));
            new OreFeature(Tile.goldOre.id, 8).place(this.level, this.random, x, y, z);
        }

        for(int i = 0; i < 8; ++i) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(16);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16)));
            new OreFeature(Tile.redStoneOre.id, 7).place(this.level, this.random, x, y, z);
        }

        for(int i = 0; i < 1; ++i) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(16);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16)));
            new OreFeature(Tile.diamondOre.id, 7).place(this.level, this.random, x, y, z);
        }

        for(int i = 0; i < 1; ++i) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(16) + this.random.nextInt(16);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16)));
            new OreFeature(Tile.lapisOre.id, 6).place(this.level, this.random, x, y, z);
        }

        BigDecimal ss = BigConstants.POINT_FIVE;
        int oFor = (int)((this.forestNoise.getValue(BigMath.decimalW(xt).multiply(ss), BigMath.decimalW(zt).multiply(ss)) / 8.0 + this.random.nextDouble() * 4.0 + 4.0) / 3.0);
        int forests = 0;
        if (this.random.nextInt(10) == 0) {
            ++forests;
        }

        if (biome == Biome.forest) {
            forests += oFor + 5;
        }

        if (biome == Biome.rainForest) {
            forests += oFor + 5;
        }

        if (biome == Biome.seasonalForest) {
            forests += oFor + 2;
        }

        if (biome == Biome.taiga) {
            forests += oFor + 5;
        }

        if (biome == Biome.desert) {
            forests -= 20;
        }

        if (biome == Biome.tundra) {
            forests -= 20;
        }

        if (biome == Biome.plains) {
            forests -= 20;
        }

        for(int i = 0; i < forests; ++i) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            Feature treeFeature = biome.getTreeFeature(this.random);
            treeFeature.init(1.0, 1.0, 1.0);
            treeFeature.place(this.level, this.random, x, this.level.getHeightmap(x, z), z);
        }

        int flowerCount = 0;
        if (biome == Biome.forest) {
            flowerCount = 2;
        }

        if (biome == Biome.seasonalForest) {
            flowerCount = 4;
        }

        if (biome == Biome.taiga) {
            flowerCount = 2;
        }

        if (biome == Biome.plains) {
            flowerCount = 3;
        }

        for(int i = 0; i < flowerCount; ++i) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new FlowerFeature(Tile.flower.id).place(this.level, this.random, x, y, z);
        }

        byte foilageAmount = 0;
        if (biome == Biome.forest) {
            foilageAmount = 2;
        }

        if (biome == Biome.rainForest) {
            foilageAmount = 10;
        }

        if (biome == Biome.seasonalForest) {
            foilageAmount = 2;
        }

        if (biome == Biome.taiga) {
            foilageAmount = 1;
        }

        if (biome == Biome.plains) {
            foilageAmount = 10;
        }

        for(int i = 0; i < foilageAmount; ++i) {
            byte data = 1;
            if (biome == Biome.rainForest && this.random.nextInt(3) != 0) {
                data = 2;
            }

            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new GrassFeature(Tile.tallgrass.id, data).place(this.level, this.random, x, y, z);
        }

        foilageAmount = 0;
        if (biome == Biome.desert) {
            foilageAmount = 2;
        }

        for(int i = 0; i < foilageAmount; ++i) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new BushFeature(Tile.deadBush.id).place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(2) == 0) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new FlowerFeature(Tile.rose.id).place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(4) == 0) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new FlowerFeature(Tile.mushroom1.id).place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(8) == 0) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new FlowerFeature(Tile.mushroom2.id).place(this.level, this.random, x, y, z);
        }

        for(int i = 0; i < 10; ++i) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new ReedsFeature().place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(32) == 0) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new PumpkinFeature().place(this.level, this.random, x, y, z);
        }

        int cactusCount = 0;
        if (biome == Biome.desert) {
            cactusCount += 10;
        }

        for(int i = 0; i < cactusCount; ++i) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new CactusFeature().place(this.level, this.random, x, y, z);
        }

        for(int i = 0; i < 50; ++i) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(this.random.nextInt(120) + 8);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new SpringFeature(Tile.water.id).place(this.level, this.random, x, y, z);
        }

        for(int i = 0; i < 20; ++i) {
            BigInteger x = xt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(this.random.nextInt(this.random.nextInt(112) + 8) + 8);
            BigInteger z = zt.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new SpringFeature(Tile.lava.id).place(this.level, this.random, x, y, z);
        }

        this.temperatures = this.level.getBiomeSource().getTemperatureBlock(this.temperatures, xt.add(BigConstants.EIGHT), zt.add(BigConstants.EIGHT), 16, 16);

        BigInteger cXT = xt.add(BigConstants.EIGHT).add(BigConstants.SIXTEEN);
        BigInteger cZT = zt.add(BigConstants.EIGHT).add(BigConstants.SIXTEEN);
        for(BigInteger x = xt.add(BigConstants.EIGHT); x.compareTo(cXT) < 0; x = x.add(BigInteger.ONE)) {
            for(BigInteger z = zt.add(BigConstants.EIGHT); z.compareTo(cZT) < 0; z = z.add(BigInteger.ONE)) {
                int xIndex = x.subtract(xt.add(BigConstants.EIGHT)).intValue();
                int zIndex = z.subtract(zt.add(BigConstants.EIGHT)).intValue();
                int topTile = this.level.getTopSolidBlock(x, z);
                double temp = this.temperatures[xIndex * 16 + zIndex] - (double)(topTile - 64) / 64.0 * 0.3;
                if (temp < 0.5
                        && topTile > 0
                        && topTile < 128
                        && this.level.isEmptyTile(x, topTile, z)
                        && this.level.getMaterial(x, topTile - 1, z).blocksMotion()
                        && this.level.getMaterial(x, topTile - 1, z) != Material.ice) {
                    this.level.setTile(x, topTile, z, Tile.topSnow.id);
                }
            }
        }

        SandTile.instaFall = false;
    }

    @Override
    public boolean save(boolean force, ProgressListener listener) {
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

    @Environment(EnvType.CLIENT)
    @Override
    public String gatherStats() {
        return "BigRandomLevelSource";
    }
}
