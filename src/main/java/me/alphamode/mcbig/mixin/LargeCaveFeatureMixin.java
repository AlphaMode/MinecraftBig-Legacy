package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.BigLargeFeatureExtension;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.LargeCaveFeature;
import net.minecraft.world.level.levelgen.LargeFeature;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;
import java.util.Random;

@Mixin(LargeCaveFeature.class)
public abstract class LargeCaveFeatureMixin extends LargeFeature implements BigLargeFeatureExtension {
    protected void addRoom(BigInteger xOffs, BigInteger zOffs, byte[] blocks, double xRoom, double yRoom, double zRoom) {
        addTunnel(xOffs, zOffs, blocks, xRoom, yRoom, zRoom, 1.0F + this.random.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5);
    }

    protected void addTunnel(BigInteger xOffs, BigInteger zOffs, byte[] blocks, double xCave, double yCave, double zCave, float thickness,
                             float yRot, float xRot, int step, int dist, double yScale) {
        double xMid = (double) (xOffs.longValue() * 16 + 8);
        double zMid = (double) (zOffs.longValue() * 16 + 8);

        float yRota = 0;
        float xRota = 0;

        Random random = new Random(this.random.nextLong());
        if (dist <= 0) {
            int max = this.radius * 16 - 16;
            dist = max - random.nextInt(max / 4);
        }

        boolean singleStep = false;
        if (step == -1) {
            step = dist / 2;
            singleStep = true;
        }

        int splitPoint = random.nextInt(dist / 2) + dist / 4;
        boolean steep = random.nextInt(6) == 0;

        for(; step < dist; step++) {
            double rad = 1.5 + (Mth.sin(step * (float) Math.PI / dist) * thickness * 1.0F);
            double yRad = rad * yScale;

            float xc = Mth.cos(xRot);
            float xs = Mth.sin(xRot);
            xCave += Mth.cos(yRot) * xc;
            yCave += xs;
            zCave += Mth.sin(yRot) * xc;

            if (steep) {
                xRot *= 0.92F;
            } else {
                xRot *= 0.7F;
            }

            xRot += xRota * 0.1F;
            yRot += yRota * 0.1F;

            xRota *= 0.9F;
            yRota *= 0.75F;
            xRota += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            yRota += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (!singleStep && step == splitPoint && thickness > 1.0F) {
                addTunnel(xOffs, zOffs, blocks, xCave, yCave, zCave, random.nextFloat() * 0.5F + 0.5F, yRot - (float) (Math.PI / 2), xRot / 3.0F, step, dist, 1.0);
                addTunnel(xOffs, zOffs, blocks, xCave, yCave, zCave, random.nextFloat() * 0.5F + 0.5F, yRot + (float) (Math.PI / 2), xRot / 3.0F, step, dist, 1.0);
                return;
            }

            if (!singleStep && random.nextInt(4) == 0) {
                double xd = xCave - xMid;
                double zd = zCave - zMid;
                double remaining = dist - step;
                double rr = (thickness + 2) + 16;
                if (xd * xd + zd * zd - remaining * remaining > rr * rr) {
                    return;
                }
            }

            if (!(xCave < xMid - 16.0 - rad * 2.0) && !(zCave < zMid - 16.0 - rad * 2.0) && !(xCave > xMid + 16.0 + rad * 2.0) && !(zCave > zMid + 16.0 + rad * 2.0)) {
                int x0 = Mth.floor(xCave - rad) - xOffs.intValue() * 16 - 1;
                int x1 = Mth.floor(xCave + rad) - xOffs.intValue() * 16 + 1;

                int y0 = Mth.floor(yCave - yRad) - 1;
                int y1 = Mth.floor(yCave + yRad) + 1;

                int z0 = Mth.floor(zCave - rad) - zOffs.intValue() * 16 - 1;
                int z1 = Mth.floor(zCave + rad) - zOffs.intValue() * 16 + 1;

                if (x0 < 0) x0 = 0;
                if (x1 > 16) x1 = 16;

                if (y0 < 1) y0 = 1;
                if (y1 > 120) y1 = 120;

                if (z0 < 0) z0 = 0;
                if (z1 > 16) z1 = 16;

                boolean detectedWater = false;

                for(int xx = x0; !detectedWater && xx < x1; ++xx) {
                    for(int zz = z0; !detectedWater && zz < z1; ++zz) {
                        for(int yy = y1 + 1; !detectedWater && yy >= y0 - 1; --yy) {
                            int p = (xx * 16 + zz) * 128 + yy;
                            if (yy >= 0 && yy < 128) {
                                if (blocks[p] == Tile.FLOWING_WATER.id || blocks[p] == Tile.WATER.id) {
                                    detectedWater = true;
                                }

                                if (yy != y0 - 1 && xx != x0 && xx != x1 - 1 && zz != z0 && zz != z1 - 1) {
                                    yy = y0;
                                }
                            }
                        }
                    }
                }

                if (!detectedWater) {
                    for(int xx = x0; xx < x1; ++xx) {
                        double xd = ((xx + xOffs.longValue() * 16) + 0.5 - xCave) / rad;

                        for(int zz = z0; zz < z1; ++zz) {
                            double zd = ((zz + zOffs.longValue() * 16) + 0.5 - zCave) / rad;
                            int p = (xx * 16 + zz) * 128 + y1;
                            boolean hasGrass = false;
                            if (xd * xd + zd * zd < 1.0) {
                                for(int yy = y1 - 1; yy >= y0; --yy) {
                                    double yd = ((double)yy + 0.5 - yCave) / yRad;
                                    if (yd > -0.7 && xd * xd + yd * yd + zd * zd < 1.0) {
                                        int block = blocks[p];
                                        if (block == Tile.GRASS.id) {
                                            hasGrass = true;
                                        }

                                        if (block == Tile.STONE.id || block == Tile.DIRT.id || block == Tile.GRASS.id) {
                                            if (yy < 10) {
                                                blocks[p] = (byte)Tile.FLOWING_LAVA.id;
                                            } else {
                                                blocks[p] = 0;
                                                if (hasGrass && blocks[p - 1] == Tile.DIRT.id) {
                                                    blocks[p - 1] = (byte)Tile.GRASS.id;
                                                }
                                            }
                                        }
                                    }

                                    --p;
                                }
                            }
                        }
                    }

                    if (singleStep) {
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void addFeature(Level level, BigInteger x, BigInteger z, BigInteger xOffs, BigInteger zOffs, byte[] blocks) {
        int caves = this.random.nextInt(this.random.nextInt(this.random.nextInt(40) + 1) + 1);
        if (this.random.nextInt(15) != 0) caves = 0;

        for(int cave = 0; cave < caves; ++cave) {
            double xCave = x.longValue() * 16 + this.random.nextInt(16);
            double yCave = this.random.nextInt(this.random.nextInt(120) + 8);
            double zCave = z.longValue() * 16 + this.random.nextInt(16);

            int tunnels = 1;
            if (this.random.nextInt(4) == 0) {
                addRoom(xOffs, zOffs, blocks, xCave, yCave, zCave);
                tunnels += this.random.nextInt(4);
            }

            for(int i = 0; i < tunnels; ++i) {
                float yRot = this.random.nextFloat() * (float) Math.PI * 2.0F;
                float xRot = (this.random.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float thickness = this.random.nextFloat() * 2.0F + this.random.nextFloat();
                addTunnel(xOffs, zOffs, blocks, xCave, yCave, zCave, thickness, yRot, xRot, 0, 0, 1.0);
            }
        }
    }
}
