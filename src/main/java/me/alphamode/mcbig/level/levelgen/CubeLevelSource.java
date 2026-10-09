package me.alphamode.mcbig.level.levelgen;

import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import me.alphamode.mcbig.level.cube.CubeSource;
import me.alphamode.mcbig.level.cube.LevelCube;
import me.alphamode.mcbig.math.BigConstants;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.LargeCaveFeature;
import net.minecraft.world.level.levelgen.LargeFeature;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.tile.Tile;

import java.math.BigInteger;
import java.util.Random;

public class CubeLevelSource implements CubeSource {
    private static final int CHUNK_HEIGHT = 8;
    private static final int CHUNK_WIDTH = 4;

    private Random random;
    private PerlinNoise lperlinNoise1;
    private PerlinNoise lperlinNoise2;
    private PerlinNoise perlinNoise1;
    private PerlinNoise perlinNoise2;
    private PerlinNoise perlinNoise3;
    public PerlinNoise scaleNoise;
    public PerlinNoise depthNoise;
    public PerlinNoise forestNoise;
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

    public CubeLevelSource(Level level, long seed) {
        this.level = level;
        this.random = new Random(seed);
        this.lperlinNoise1 = new PerlinNoise(this.random, 16);
        this.lperlinNoise2 = new PerlinNoise(this.random, 16);
        this.perlinNoise1 = new PerlinNoise(this.random, 8);
        this.perlinNoise2 = new PerlinNoise(this.random, 4);
        this.perlinNoise3 = new PerlinNoise(this.random, 4);
        this.scaleNoise = new PerlinNoise(this.random, 10);
        this.depthNoise = new PerlinNoise(this.random, 16);
        this.forestNoise = new PerlinNoise(this.random, 8);
    }


    private static final byte[] BLOCKS;

    static {
        BLOCKS = new byte[4096];
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    if (x % 4 == 0 && y % 4 == 0 && z % 4 == 0) {
                        BLOCKS[x << 8 | z << 4 | y] = (byte) Tile.stone.id;
                    }
                }
            }
        }
    }

    public void prepareHeights(BigInteger xOffs, BigInteger yOffs, BigInteger zOffs, byte[] blocks, Biome[] biomes, double[] temperatures) {
        int xChunks = 16 / CHUNK_WIDTH;
        int yChunks = 16 / CHUNK_HEIGHT;
        //~ if >=1.0.0-beta.8.0.r '64' -> '63'
        var waterHeight = BigInteger.valueOf(64);

        int xSize = xChunks + 1;
        int ySize = 16 / CHUNK_HEIGHT + 1;
        int zSize = xChunks + 1;
        //? >=1.0.0-beta.8.0.r
        //this.biomes = this.level.getBiomeSource().getRawBiomeBlock(this.biomes, xOffs.multiply(BigConstants.FOUR).subtract(BigInteger.TWO), zOffs.multiply(BigConstants.FOUR).subtract(BigInteger.TWO), xSize + 5, zSize + 5);
        this.buffer = this.getHeights(this.buffer, xOffs.multiply(BigInteger.valueOf(xChunks)), yOffs.multiply(BigInteger.valueOf(yChunks)), zOffs.multiply(BigInteger.valueOf(xChunks)), xSize, ySize, zSize);

        for (int xc = 0; xc < xChunks; ++xc) {
            for (int zc = 0; zc < xChunks; ++zc) {
                for (int yc = 0; yc < yChunks; ++yc) {
                    double yStep = 1 / (double) CHUNK_HEIGHT;
                    double s0 = this.buffer[((xc + 0) * zSize + zc + 0) * ySize + yc + 0];
                    double s1 = this.buffer[((xc + 0) * zSize + zc + 1) * ySize + yc + 0];
                    double s2 = this.buffer[((xc + 1) * zSize + zc + 0) * ySize + yc + 0];
                    double s3 = this.buffer[((xc + 1) * zSize + zc + 1) * ySize + yc + 0];

                    double s0a = (this.buffer[((xc + 0) * zSize + zc + 0) * ySize + yc + 1] - s0) * yStep;
                    double s1a = (this.buffer[((xc + 0) * zSize + zc + 1) * ySize + yc + 1] - s1) * yStep;
                    double s2a = (this.buffer[((xc + 1) * zSize + zc + 0) * ySize + yc + 1] - s2) * yStep;
                    double s3a = (this.buffer[((xc + 1) * zSize + zc + 1) * ySize + yc + 1] - s3) * yStep;

                    for (int y = 0; y < CHUNK_HEIGHT; ++y) {
                        var yt = BigInteger.valueOf(yc * CHUNK_HEIGHT + y).add(yOffs);
                        double xStep = 1 / (double) CHUNK_WIDTH;

                        double _s0 = s0;
                        double _s1 = s1;
                        double _s0a = (s2 - s0) * xStep;
                        double _s1a = (s3 - s1) * xStep;

                        for (int x = 0; x < CHUNK_WIDTH; ++x) {
                            int offs = x + xc * CHUNK_WIDTH << 8 | 0 + zc * CHUNK_WIDTH << 4 | yc * CHUNK_HEIGHT + y;
                            int step = 1 << 4;
                            double zStep = 1 / (double) CHUNK_WIDTH;
                            double val = _s0;
                            double vala = (_s1 - _s0) * zStep;

                            for (int z = 0; z < CHUNK_WIDTH; ++z) {
                                //? <1.0.0-beta.8.0.r
                                double temp = temperatures[(xc * CHUNK_WIDTH + x) * 16 + (zc * CHUNK_WIDTH + z)];
                                int tileId = 0;
                                if (-yt.compareTo(waterHeight) < 0) {
                                    //? <1.0.0-beta.8.0.r {
                                    if (temp < 0.5 && yt.compareTo(waterHeight.subtract(BigInteger.ONE)) >= 0) {
                                        tileId = Tile.ice.id;
                                    } else {
                                        tileId = Tile.calmWater.id;
                                    }
                                    //? } else
                                    //tileId = Tile.calmWater.id;
                                }

                                if (val > 0.0) {
                                    tileId = Tile.stone.id;
                                }

                                blocks[offs] = (byte) tileId;
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

    private double[] getHeights(double[] buffer, BigInteger x, BigInteger y, BigInteger z, int xSize, int ySize, int zSize) {
        if (buffer == null) {
            buffer = new double[xSize * ySize * zSize];
        }

        //? >=1.0.0-beta.8.0.r {
        /*if (this.pows == null) {
            this.pows = new float[25];

            for (int xb = -2; xb <= 2; xb++) {
                for (int zb = -2; zb <= 2; zb++) {
                    float ppp = 10.0F / Mth.sqrt(xb * xb + zb * zb + 0.2F);
                    this.pows[xb + 2 + (zb + 2) * 5] = ppp;
                }
            }
        }
        *///? }

        double s = 1 * 684.412;
        double hs = 1 * 684.412;
        //? <1.0.0-beta.8.0.r {
        double[] temperatures = this.level.getBiomeSource().temperatures;
        double[] downfalls = this.level.getBiomeSource().downfalls;
        //? }
        this.sr = this.scaleNoise.getRegion(this.sr, x, z, xSize, zSize, 1.121, 1.121, 0.5);
        this.dr = this.depthNoise.getRegion(this.dr, x, z, xSize, zSize, 200.0, 200.0, 0.5);

        //~ if >=1.0.0-beta.8.0.r ' x.doubleValue(),' -> ' x,' {
        //~ if >=1.0.0-beta.8.0.r ' z.doubleValue(),' -> ' z,' {
        this.pnr = this.perlinNoise1.getRegion(this.pnr, x.doubleValue(), y.doubleValue(), z.doubleValue(), xSize, ySize, zSize, s / 80.0, hs / 160.0, s / 80.0);
        this.ar = this.lperlinNoise1.getRegion(this.ar, x.doubleValue(), y.doubleValue(), z.doubleValue(), xSize, ySize, zSize, s, hs, s);
        this.br = this.lperlinNoise2.getRegion(this.br, x.doubleValue(), y.doubleValue(), z.doubleValue(), xSize, ySize, zSize, s, hs, s);
        //~ }
        //~ }

        int p = 0;
        int pp = 0;

        //? <1.0.0-beta.8.0.r
        int wScale = 16 / xSize;
        for (int xx = 0; xx < xSize; ++xx) {
            //? <1.0.0-beta.8.0.r
            int xp = xx * wScale + wScale / 2;

            for (int zz = 0; zz < zSize; ++zz) {
                //? >=1.0.0-beta.8.0.r {
                /*float sss = 0.0F;
                float ddd = 0.0F;
                float pow = 0.0F;
                byte rr = 2;
                Biome mb = this.biomes[xx + 2 + (zz + 2) * (xSize + 5)];

                for (int xb = -rr; xb <= rr; xb++) {
                    for (int zb = -rr; zb <= rr; zb++) {
                        Biome b = this.biomes[xx + xb + 2 + (zz + zb + 2) * (xSize + 5)];
                        float ppp = this.pows[xb + 2 + (zb + 2) * 5] / (b.depth + 2.0F);
                        if (b.depth > mb.depth) {
                            ppp /= 2.0F;
                        }

                        sss += b.scale * ppp;
                        ddd += b.depth * ppp;
                        pow += ppp;
                    }
                }

                sss /= pow;
                ddd /= pow;
                sss = sss * 0.9F + 0.1F;
                ddd = (ddd * 4.0F - 1.0F) / 8.0F;
                *///? } else {
                int zp = zz * wScale + wScale / 2;
                double temperature = temperatures[xp * 16 + zp];
                double downfall = downfalls[xp * 16 + zp] * temperature;
                double dd = 1 - downfall;
                dd *= dd;
                dd *= dd;
                dd = 1 - dd;

                double scale = (this.sr[pp] + 256.0) / 512.0;
                scale *= dd;
                if (scale > 1) scale = 1;
                //? }
                //~ if >=1.0.0-beta.8.0.r 'depth' -> 'rdepth' {
                double depth = this.dr[pp] / 8000.0;
                if (depth < 0) depth = -depth * 0.3;

                depth = depth * 3.0 - 2.0;
                if (depth < 0) {
                    depth /= 2;
                    if (depth < -1) depth = -1;

                    depth /= 1.4;
                    depth /= 2;
                    //? <1.0.0-beta.8.0.r
                    scale = 0;
                } else {
                    if (depth > 1) depth = 1;
                    depth /= 8;
                }

                //? <1.0.0-beta.8.0.r {
                if (scale < 0) scale = 0;
                scale += 0.5;
                depth = depth * (double) ySize / 16;

                double yCenter = (double) /*ySize*/(17) / 2.0 + depth * 4.0;
                //? }
                //~}

                ++pp;

                for (int yy = 0; yy < ySize; ++yy) {
                    //? >=1.0.0-beta.8.0.r {
                    /*double depth = ddd;
                    double scale = sss;
                    depth += rdepth * 0.2;
                    depth = depth * ySize / 16.0;
                    double yCenter = ySize / 2.0 + depth * 4.0;
                    *///? }
                    double val = 0;
                    double yOffs = ((double) yy - yCenter) * 12 / scale;
                    if (yOffs < 0.0) yOffs *= 4;

                    double bb = this.ar[p] / 512;
                    double cc = this.br[p] / 512;

                    double v = (this.pnr[p] / 10 + 1) / 2;
                    if (v < 0.0) val = bb;
                    else if (v > 1.0) val = cc;
                    else val = bb + (cc - bb) * v;
//                    val -= yOffs;

//                    if (yy > ySize - 4) {
//                        double slide = (float) (yy - (ySize - 4)) / 3.0F;
//                        val = val * (1 - slide) + -10 * slide;
//                    }

                    buffer[p] = val;
                    ++p;
                }
            }
        }

        return buffer;
    }

    @Override
    public boolean hasCube(BigInteger x, BigInteger y, BigInteger z) {
        return true;
    }

    @Override
    public LevelCube getCube(BigInteger x, BigInteger y, BigInteger z) {
        return create(x, y, z);
    }

    @Override
    public LevelCube create(BigInteger x, BigInteger y, BigInteger z) {
        this.random.setSeed(x.longValue() * 550848269883L * y.longValue() * 341873128712L + z.longValue() * 132897987541L);
        byte[] tiles = new byte[LevelCube.SIZE * LevelCube.SIZE * LevelCube.SIZE];
        LevelCube chunk = new LevelCube(this.level, tiles, x, y, z);
        //? >=1.0.0-beta.8.0.r
        //prepareHeights(x, z, tiles);
        this.biomes = this.level.getBiomeSource().getBiomeBlock(this.biomes, x.multiply(BigConstants.SIXTEEN), z.multiply(BigConstants.SIXTEEN), 16, 16);
        //? <1.0.0-beta.8.0.r {
        double[] temps = this.level.getBiomeSource().temperatures;
        prepareHeights(x, y, z, tiles, this.biomes, temps);
        //? }
//        buildSurfaces(x, z, tiles, this.biomes);
//        this.caveFeature.apply(this, this.level, x, z, tiles);
        //? >=1.0.0-beta.8.0.r {
    /*if (this.generateStructures) {
        this.strongholdFeature.apply(this, this.level, x, z, tiles);
        this.mineShaftFeature.apply(this, this.level, x, z, tiles);
        this.villageFeature.apply(this, this.level, x, z, tiles);
    }

    this.canyonFeature.apply(this, this.level, x, z, tiles);
    *///? }

//        chunk.recalcHeightmap();
        return chunk;
    }

    @Override
    public void postProcess(ChunkSource generator, BigInteger x, BigInteger y, BigInteger z) {

    }

    @Override
    public boolean save(boolean force, ProgressListener listener) {
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
        return "";
    }
}
