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
    private static final int CHUNK_HEIGHT = 8;
    private static final int CHUNK_WIDTH = 4;

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

    public void prepareHeights(BigInteger xOffs, BigInteger zOffs, byte[] blocks) {
        int xChunks = 16 / CHUNK_WIDTH;
        BigInteger xChunksBig = BigConstants.FOUR;
        int waterHeight = 32;
        int xSize = xChunks + 1;
        int ySize = 17;
        int zSize = xChunks + 1;
        this.buffer = getHeights(this.buffer, xOffs.multiply(xChunksBig), 0, zOffs.multiply(xChunksBig), xSize, ySize, zSize);

        for (int xc = 0; xc < xChunks; xc++) {
            for (int zc = 0; zc < xChunks; zc++) {
                for (int yc = 0; yc < 16; yc++) {
                    double yStep = 1 / (double) CHUNK_HEIGHT;//0.125;
                    double s0 = this.buffer[((xc + 0) * zSize + zc + 0) * ySize + yc + 0];
                    double s1 = this.buffer[((xc + 0) * zSize + zc + 1) * ySize + yc + 0];
                    double s2 = this.buffer[((xc + 1) * zSize + zc + 0) * ySize + yc + 0];
                    double s3 = this.buffer[((xc + 1) * zSize + zc + 1) * ySize + yc + 0];

                    double s0a = (this.buffer[((xc + 0) * zSize + zc + 0) * ySize + yc + 1] - s0) * yStep;
                    double s1a = (this.buffer[((xc + 0) * zSize + zc + 1) * ySize + yc + 1] - s1) * yStep;
                    double s2a = (this.buffer[((xc + 1) * zSize + zc + 0) * ySize + yc + 1] - s2) * yStep;
                    double s3a = (this.buffer[((xc + 1) * zSize + zc + 1) * ySize + yc + 1] - s3) * yStep;

                    for (int y = 0; y < 8; y++) {
                        double xStep = 1 / (float) CHUNK_WIDTH;//0.25;

                        double _s0 = s0;
                        double _s1 = s1;
                        double _s0a = (s2 - s0) * xStep;
                        double _s1a = (s3 - s1) * xStep;

                        for (int x = 0; x < CHUNK_WIDTH; x++) {
                            int offs = x + xc * CHUNK_WIDTH << 11 | 0 + zc * CHUNK_WIDTH << 7 | yc * 8 + y;
                            int step = 1 << 7; // 7 = levelDepthBits //128;
                            double zStep = 1 / (double) CHUNK_WIDTH; //0.25;

                            double val = _s0;
                            double vala = (_s1 - _s0) * zStep;

                            for (int z = 0; z < CHUNK_WIDTH; z++) {
                                int tileId = 0;
                                if (yc * 8 + y < waterHeight) {
                                    tileId = Tile.LAVA.id;
                                }

                                if (val > 0.0) {
                                    tileId = Tile.NETHERRACK.id;
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

    public void buildSurfaces(BigInteger xOffs, BigInteger zOffs, byte[] blocks) {
        int waterHeight = 64;
        double s = 1 / 32.0;//0.03125;
        this.sandBuffer = this.perlinNoise2.getRegion(this.sandBuffer, xOffs.doubleValue() * 16, zOffs.doubleValue() * 16, 0.0, 16, 16, 1, s, s, 1.0);
        this.gravelBuffer = this.perlinNoise2.getRegion(this.gravelBuffer, xOffs.doubleValue() * 16, 109.0134, zOffs.doubleValue() * 16, 16, 1, 16, s, 1.0, s);
        this.depthBuffer = this.perlinNoise3.getRegion(this.depthBuffer, xOffs.doubleValue() * 16, zOffs.doubleValue() * 16, 0.0, 16, 16, 1, s * 2.0, s * 2.0, s * 2.0);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                boolean sand = this.sandBuffer[x + z * 16] + this.random.nextDouble() * 0.2 > 0.0;
                boolean gravel = this.gravelBuffer[x + z * 16] + this.random.nextDouble() * 0.2 > 0.0;
                int runDepth = (int)(this.depthBuffer[x + z * 16] / 3.0 + 3.0 + this.random.nextDouble() * 0.25);

                int run = -1;

                byte top = (byte)Tile.NETHERRACK.id;
                byte material = (byte)Tile.NETHERRACK.id;

                for (int y = 127; y >= 0; y--) {
                    int offs = (z * 16 + x) * 128 + y;
                    if (y >= 127 - this.random.nextInt(5)) {
                        blocks[offs] = (byte)Tile.BEDROCK.id;
                    } else if (y <= 0 + this.random.nextInt(5)) {
                        blocks[offs] = (byte)Tile.BEDROCK.id;
                    } else {
                        byte old = blocks[offs];
                        if (old == 0) {
                            run = -1;
                        } else if (old == Tile.NETHERRACK.id) {
                            if (run == -1) {
                                if (runDepth <= 0) {
                                    top = 0;
                                    material = (byte)Tile.NETHERRACK.id;
                                } else if (y >= waterHeight - 4 && y <= waterHeight + 1) {
                                    top = (byte)Tile.NETHERRACK.id;
                                    material = (byte)Tile.NETHERRACK.id;
                                    if (gravel) {
                                        top = (byte)Tile.GRAVEL.id;
                                    }

                                    if (gravel) {
                                        material = (byte)Tile.NETHERRACK.id;
                                    }

                                    if (sand) {
                                        top = (byte)Tile.SOUL_SAND.id;
                                    }

                                    if (sand) {
                                        material = (byte)Tile.SOUL_SAND.id;
                                    }
                                }

                                if (y < waterHeight && top == 0) {
                                    top = (byte)Tile.LAVA.id;
                                }

                                run = runDepth;
                                if (y >= waterHeight - 1) {
                                    blocks[offs] = top;
                                } else {
                                    blocks[offs] = material;
                                }
                            } else if (run > 0) {
                                run--;
                                blocks[offs] = material;
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
        byte[] blocks = new byte[32768];
        prepareHeights(x, z, blocks);
        buildSurfaces(x, z, blocks);
        this.caveFeature.apply((ChunkSource) this, this.level, x, z, blocks);
        return new BigLevelChunk(this.level, blocks, x, z);
    }

    private double[] getHeights(double[] buffer, BigInteger x, int y, BigInteger z, int xSize, int ySize, int zSize) {
        if (buffer == null) {
            buffer = new double[xSize * ySize * zSize];
        }

        double s = 1 * 684.412;
        double hs = 1 * 684.412 * 3;//2053.236;
        this.sr = this.scaleNoise.getRegion(this.sr, x.doubleValue(), y, z.doubleValue(), xSize, 1, zSize, 1.0, 0.0, 1.0);
        this.dr = this.depthNoise.getRegion(this.dr, x.doubleValue(), y, z.doubleValue(), xSize, 1, zSize, 100.0, 0.0, 100.0);

        this.pnr = this.perlinNoise1.getRegion(this.pnr, x.doubleValue(), y, z.doubleValue(), xSize, ySize, zSize, s / 80.0, hs / 60.0, s / 80.0);
        this.ar = this.lperlinNoise1.getRegion(this.ar, x.doubleValue(), y, z.doubleValue(), xSize, ySize, zSize, s, hs, s);
        this.br = this.lperlinNoise2.getRegion(this.br, x.doubleValue(), y, z.doubleValue(), xSize, ySize, zSize, s, hs, s);

        int p = 0;
        int pp = 0;
        double[] yoffs = new double[ySize];

        for (int yy = 0; yy < ySize; yy++) {
            yoffs[yy] = Math.cos(yy * Math.PI * 6.0 / ySize) * 2.0;
            double dd = yy;
            if (yy > ySize / 2) {
                dd = ySize - 1 - yy;
            }

            if (dd < 4.0) {
                dd = 4.0 - dd;
                yoffs[yy] -= dd * dd * dd * 10.0;
            }
        }

        for (int xx = 0; xx < xSize; xx++) {
            for (int zz = 0; zz < zSize; zz++) {
                double scale = (this.sr[pp] + 256.0) / 512.0;
                if (scale > 1.0) {
                    scale = 1.0;
                }

                double floating = 0.0;

                double depth = this.dr[pp] / 8000.0;
                if (depth < 0.0) {
                    depth = -depth;
                }

                depth = depth * 3.0 - 3.0;
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

                    depth /= 6.0;
                }

                scale += 0.5;
                depth = depth * ySize / 16.0;
                pp++;

                for (int yy = 0; yy < ySize; yy++) {
                    double val = 0;

                    double yOffs = yoffs[yy];

                    double bb = this.ar[p] / 512;
                    double cc = this.br[p] / 512;

                    double v = (this.pnr[p] / 10 + 1) / 2;
                    if (v < 0.0) {
                        val = bb;
                    } else if (v > 1.0) {
                        val = cc;
                    } else {
                        val = bb + (cc - bb) * v;
                    }

                    val -= yOffs;
                    if (yy > ySize - 4) {
                        double slide = (yy - (ySize - 4)) / 3.0F;
                        val = val * (1.0 - slide) + -10.0 * slide;
                    }

                    if (yy < floating) {
                        double slide = (floating - yy) / 4.0;
                        if (slide < 0.0) {
                            slide = 0.0;
                        }

                        if (slide > 1.0) {
                            slide = 1.0;
                        }

                        val = val * (1.0 - slide) + -10.0 * slide;
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

        for (int i = 0; i < 8; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(120) + 4;
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new HellSpringFeature(Tile.FLOWING_LAVA.id).place(this.level, this.random, x, y, z);
        }

        int count = this.random.nextInt(this.random.nextInt(10) + 1) + 1;

        for (int i = 0; i < count; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(120) + 4;
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new HellFireFeature().place(this.level, this.random, x, y, z);
        }

        count = this.random.nextInt(this.random.nextInt(10) + 1);

        for (int i = 0; i < count; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(120) + 4;
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new LightGemFeature().place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 10; i++) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new HellPortalFeature().place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(1) == 0) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new FlowerFeature(Tile.BROWN_MUSHROOM.id).place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(1) == 0) {
            BigInteger x = xo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = zo.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new FlowerFeature(Tile.RED_MUSHROOM.id).place(this.level, this.random, x, y, z);
        }

        SandTile.instaFall = false;
    }
}
