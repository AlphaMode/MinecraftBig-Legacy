package me.alphamode.mcbig.mixin.worldgen;

import me.alphamode.mcbig.extensions.BigChunkSourceExtension;
import me.alphamode.mcbig.extensions.BigPerlinNoiseExtension;
import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import me.alphamode.mcbig.math.BigConstants;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.LargeFeature;
import net.minecraft.world.level.levelgen.SkyLevelSource;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.SandTile;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(SkyLevelSource.class)
public abstract class SkyLevelSourceMixin implements ChunkSource, BigChunkSourceExtension {

    private static final int CHUNK_HEIGHT = 4;
    private static final int CHUNK_WIDTH = 8;

    @Shadow
    private Random random;
    @Shadow
    private PerlinNoise lperlinNoise1;
    @Shadow
    private PerlinNoise lperlinNoise2;
    @Shadow
    private PerlinNoise perlinNoise1;
    @Shadow
    private PerlinNoise perlinNoise2;
    @Shadow
    private PerlinNoise perlinNoise3;
    @Shadow
    public PerlinNoise scaleNoise;
    @Shadow
    public PerlinNoise depthNoise;
    @Shadow
    public PerlinNoise forestNoise;

    @Shadow
    private Level level;
    @Shadow
    private double[] buffer;
    @Shadow
    private double[] sandBuffer;
    @Shadow
    private double[] gravelBuffer;
    @Shadow
    private double[] depthBuffer;

    @Shadow
    private LargeFeature carver;
    @Shadow
    private Biome[] biomes;

    @Shadow
    private double[] pnr;
    @Shadow
    private double[] ar;
    @Shadow
    private double[] br;
    @Shadow
    private double[] sr;
    @Shadow
    private double[] dr;

    @Shadow
    private double[] temperatures;

    public void prepareHeights(BigInteger xOffs, BigInteger zOffs, byte[] blocks, Biome[] biomes, double[] temperatures) {
        int xChunks = 16 / CHUNK_WIDTH;//2;
        BigInteger xChunksBig = BigInteger.valueOf(xChunks);

        int xSize = xChunks + 1;
        int ySize = 33;
        int zSize = xChunks + 1;
        this.buffer = getHeights(this.buffer, xOffs.multiply(xChunksBig), 0, zOffs.multiply(xChunksBig), xSize, ySize, zSize);

        for (int xc = 0; xc < xChunks; xc++) {
            for (int zc = 0; zc < xChunks; zc++) {
                for (int yc = 0; yc < 32; yc++) {
                    double yStep = 1 / (double) CHUNK_HEIGHT;//0.25;
                    double s0 = this.buffer[((xc + 0) * zSize + zc + 0) * ySize + yc + 0];
                    double s1 = this.buffer[((xc + 0) * zSize + zc + 1) * ySize + yc + 0];
                    double s2 = this.buffer[((xc + 1) * zSize + zc + 0) * ySize + yc + 0];
                    double s3 = this.buffer[((xc + 1) * zSize + zc + 1) * ySize + yc + 0];

                    double s0a = (this.buffer[((xc + 0) * zSize + zc + 0) * ySize + yc + 1] - s0) * yStep;
                    double s1a = (this.buffer[((xc + 0) * zSize + zc + 1) * ySize + yc + 1] - s1) * yStep;
                    double s2a = (this.buffer[((xc + 1) * zSize + zc + 0) * ySize + yc + 1] - s2) * yStep;
                    double s3a = (this.buffer[((xc + 1) * zSize + zc + 1) * ySize + yc + 1] - s3) * yStep;

                    for (int y = 0; y < 4; y++) {
                        double xStep = 1 / (double) CHUNK_WIDTH;//0.125;

                        double _s0 = s0;
                        double _s1 = s1;
                        double _s0a = (s2 - s0) * xStep;
                        double _s1a = (s3 - s1) * xStep;

                        for (int x = 0; x < 8; x++) {
                            int offs = x + xc * CHUNK_WIDTH << 11 | 0 + zc * CHUNK_WIDTH << 7 | yc * CHUNK_HEIGHT + y;
                            int step = 1 << 7;//128;
                            double zStep = 1 / (double) CHUNK_WIDTH;//0.125;

                            double val = _s0;
                            double vala = (_s1 - _s0) * zStep;

                            for (int z = 0; z < CHUNK_WIDTH; z++) {
                                int tileId = 0;
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
        double s = 1 / 32.0;//0.03125;
        double xx = xOffs.multiply(BigConstants.SIXTEEN).doubleValue();
        double zz = zOffs.multiply(BigConstants.SIXTEEN).doubleValue();
        this.sandBuffer = this.perlinNoise2.getRegion(this.sandBuffer, xx, zz, 0.0, 16, 16, 1, s, s, 1.0);
        this.gravelBuffer = this.perlinNoise2.getRegion(this.gravelBuffer, xx, 109.0134, zz, 16, 1, 16, s, 1.0, s);
        this.depthBuffer = this.perlinNoise3.getRegion(this.depthBuffer, xx, zz, 0.0, 16, 16, 1, s * 2.0, s * 2.0, s * 2.0);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Biome b = biomes[x + z * 16];
                int runDepth = (int)(this.depthBuffer[x + z * 16] / 3.0 + 3.0 + this.random.nextDouble() * 0.25);

                int run = -1;

                byte top = b.topMaterial;
                byte material = b.material;

                for (int y = 127; y >= 0; y--) {
                    int offs = (z * 16 + x) * 128 + y;
                    byte old = blocks[offs];
                    if (old == 0) {
                        run = -1;
                    } else if (old == Tile.stone.id) {
                        if (run == -1) {
                            if (runDepth <= 0) {
                                top = 0;
                                material = (byte)Tile.stone.id;
                            }

                            run = runDepth;
                            if (y >= 0) {
                                blocks[offs] = top;
                            } else {
                                blocks[offs] = material;
                            }
                        } else if (run > 0) {
                            run--;
                            blocks[offs] = material;
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

    @Override
    public LevelChunk loadChunk(BigInteger x, BigInteger z) {
        return getChunk(x, z);
    }

    @Override
    public LevelChunk getChunk(BigInteger x, BigInteger z) {
        this.random.setSeed(x.longValue() * 341873128712L + z.longValue() * 132897987541L);
        byte[] blocks = new byte[32768];
        LevelChunk lc = new BigLevelChunk(this.level, blocks, x, z);
        this.biomes = this.level.getBiomeSource().getBiomeBlock(this.biomes, x.multiply(BigConstants.SIXTEEN), z.multiply(BigConstants.SIXTEEN), 16, 16);
        double[] temperatures = this.level.getBiomeSource().temperatures;
        prepareHeights(x, z, blocks, this.biomes, temperatures);
        buildSurfaces(x, z, blocks, this.biomes);
        this.carver.apply(this, this.level, x, z, blocks);
        lc.recalcHeightmap();
        return lc;
    }

    private double[] getHeights(double[] buffer, BigInteger x, int y, BigInteger z, int xSize, int ySize, int zSize) {
        if (buffer == null) {
            buffer = new double[xSize * ySize * zSize];
        }

        double s = 1 * 684.412;
        double hs = 1 * 684.412;
        double[] temperatures = this.level.getBiomeSource().temperatures;
        double[] downfalls = this.level.getBiomeSource().downfalls;
        double xD = x.doubleValue();
        double zD = z.doubleValue();
        this.sr = ((BigPerlinNoiseExtension) this.scaleNoise).getRegion(this.sr, x, z, xSize, zSize, 1.121, 1.121, 0.5);
        this.dr = ((BigPerlinNoiseExtension) this.depthNoise).getRegion(this.dr, x, z, xSize, zSize, 200.0, 200.0, 0.5);

        s *= 2.0;
        this.pnr = this.perlinNoise1.getRegion(this.pnr, xD, y, zD, xSize, ySize, zSize, s / 80.0, hs / 160.0, s / 80.0);
        this.ar = this.lperlinNoise1.getRegion(this.ar, xD, y, zD, xSize, ySize, zSize, s, hs, s);
        this.br = this.lperlinNoise2.getRegion(this.br, xD, y, zD, xSize, ySize, zSize, s, hs, s);

        int p = 0;
        int pp = 0;

        int wScale = 16 / xSize;
        for (int xx = 0; xx < xSize; xx++) {
            int xp = xx * wScale + wScale / 2;

            for (int zz = 0; zz < zSize; zz++) {
                int zp = zz * wScale + wScale / 2;
                double temperature = temperatures[xp * 16 + zp];
                double downfall = downfalls[xp * 16 + zp] * temperature;
                double dd = 1 - downfall;
                dd *= dd;
                dd *= dd;
                dd = 1.0 - dd;

                double scale = (this.sr[pp] + 256.0) / 512.0;
                scale *= dd;
                if (scale > 1) scale = 1;

                double depth = this.dr[pp] / 8000.0;
                if (depth < 0) depth = -depth * 0.3;
                depth = depth * 3.0 - 2.0;

                if (depth > 1) {
                    depth = 1;
                }

                depth /= 8.0;
                depth = 0.0;
                if (scale < 0.0) {
                    scale = 0.0;
                }

                scale += 0.5;
                depth = depth * ySize / 16.0;
                pp++;
                double yCenter = ySize / 2.0;

                for (int yy = 0; yy < ySize; yy++) {
                    double val = 0;
                    double yOffs = (yy - yCenter) * 8 / scale;
                    if (yOffs < 0) yOffs *= -1;

                    double bb = this.ar[p] / 512;
                    double cc = this.br[p] / 512;

                    double v = (this.pnr[p] / 10.0 + 1.0) / 2.0;
                    if (v < 0.0) {
                        val = bb;
                    } else if (v > 1.0) {
                        val = cc;
                    } else {
                        val = bb + (cc - bb) * v;
                    }

                    val -= 8.0;
                    byte var44 = 32;
                    if (yy > ySize - var44) {
                        double slide = (yy - (ySize - var44)) / (var44 - 1.0F);
                        val = val * (1.0 - slide) + -30.0 * slide;
                    }

                    var44 = 8;
                    if (yy < var44) {
                        double slide = (var44 - yy) / (var44 - 1.0F);
                        val = val * (1.0 - slide) + -30.0 * slide;
                    }

                    buffer[p] = val;
                    p++;
                }
            }
        }

        return buffer;
    }

    @Override
    public boolean hasChunk(BigInteger x, BigInteger z) {
        return true;
    }

    @Override
    public void postProcess(ChunkSource parent, BigInteger xt, BigInteger zt) {
        SandTile.instaFall = true;
        BigInteger xo = xt.multiply(BigConstants.SIXTEEN);
        BigInteger zo = zt.multiply(BigConstants.SIXTEEN);
        Biome biome = this.level.getBiomeSource().getBiome(xo.add(BigConstants.SIXTEEN), zo.add(BigConstants.SIXTEEN));
        this.random.setSeed(this.level.getSeed());
        long xScale = this.random.nextLong() / 2L * 2L + 1L;
        long zScale = this.random.nextLong() / 2L * 2L + 1L;
        this.random.setSeed(xt.longValue() * xScale + zt.longValue() * zScale ^ this.level.getSeed());
        double ss = 0.25;
        if (this.random.nextInt(4) == 0) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new LakeFeature(Tile.calmWater.id).place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(8) == 0) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(this.random.nextInt(120) + 8);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            if (y < 64 || this.random.nextInt(10) == 0) {
                new LakeFeature(Tile.calmLava.id).place(this.level, this.random, x, y, z);
            }
        }

        for (int i = 0; i < 8; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new MonsterRoomFeature().place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 10; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(128);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16)));
            new ClayFeature(32).place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 20; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(128);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16)));
            new OreFeature(Tile.dirt.id, 32).place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 10; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(128);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16)));
            new OreFeature(Tile.gravel.id, 32).place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 20; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(128);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16)));
            new OreFeature(Tile.coalOre.id, 16).place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 20; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(64);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16)));
            new OreFeature(Tile.ironOre.id, 8).place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 2; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(32);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16)));
            new OreFeature(Tile.goldOre.id, 8).place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 8; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(16);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16)));
            new OreFeature(Tile.redStoneOre.id, 7).place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 1; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(16);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16)));
            new OreFeature(Tile.diamondOre.id, 7).place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 1; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(16) + this.random.nextInt(16);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16)));
            new OreFeature(Tile.lapisOre.id, 6).place(this.level, this.random, x, y, z);
        }

        ss = 0.5;
        int oFor = (int)((this.forestNoise.getValue(xo.doubleValue() * ss, zo.doubleValue() * ss) / 8.0 + this.random.nextDouble() * 4.0 + 4.0) / 3.0);
        int forests = 0;
        if (this.random.nextInt(10) == 0) forests++;

        if (biome == Biome.forest) forests += oFor + 5;
        if (biome == Biome.rainForest) forests += oFor + 5;
        if (biome == Biome.seasonalForest) forests += oFor + 2;
        if (biome == Biome.taiga) forests += oFor + 5;
        if (biome == Biome.desert) forests -= 20;
        if (biome == Biome.tundra) forests -= 20;
        if (biome == Biome.plains) forests -= 20;

        for (int i = 0; i < forests; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            Feature tree = biome.getTreeFeature(this.random);
            tree.init(1.0, 1.0, 1.0);
            tree.place(this.level, this.random, x, this.level.getHeightmap(x, z), z);
        }

        for (int i = 0; i < 2; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new FlowerFeature(Tile.flower.id).place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(2) == 0) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new FlowerFeature(Tile.rose.id).place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(4) == 0) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new FlowerFeature(Tile.mushroom1.id).place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(8) == 0) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new FlowerFeature(Tile.mushroom2.id).place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 10; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new ReedsFeature().place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(32) == 0) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new PumpkinFeature().place(this.level, this.random, x, y, z);
        }

        int cacti = 0;
        if (biome == Biome.desert) cacti += 10;

        for (int i = 0; i < cacti; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new CactusFeature().place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 50; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(this.random.nextInt(120) + 8);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new SpringFeature(Tile.water.id).place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 20; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(this.random.nextInt(this.random.nextInt(112) + 8) + 8);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new SpringFeature(Tile.lava.id).place(this.level, this.random, x, y, z);
        }

        this.temperatures = this.level.getBiomeSource().getTemperatureBlock(this.temperatures, xo.add(BigConstants.EIGHT), zo.add(BigConstants.EIGHT), 16, 16);

        final BigInteger xoPlusEight = xo.add(BigConstants.EIGHT);
        final BigInteger zoPlusEight = zo.add(BigConstants.EIGHT);
        final BigInteger snowCheckX = xoPlusEight.add(BigConstants.SIXTEEN);
        final BigInteger snowCheckZ = zoPlusEight.add(BigConstants.SIXTEEN);
        for (BigInteger x = xoPlusEight; x.compareTo(snowCheckX) < 0; x = x.add(BigInteger.ONE)) {
            for (BigInteger z = zoPlusEight; z.compareTo(snowCheckZ) < 0; z = z.add(BigInteger.ONE)) {
                int xp = x.subtract(xoPlusEight).intValue();
                int zp = z.subtract(zoPlusEight).intValue();
                int y = this.level.getTopSolidBlock(x, z);
                double temp = this.temperatures[xp * 16 + zp] - (y - 64) / 64.0 * 0.3;
                if (temp < 0.5 && y > 0 && y < 128 && this.level.isEmptyTile(x, y, z) && this.level.getMaterial(x, y - 1, z).blocksMotion()) {
                    if (this.level.getMaterial(x, y - 1, z) != Material.ice) this.level.setTile(x, y, z, Tile.topSnow.id);
                }
            }
        }

        SandTile.instaFall = false;
    }
}
