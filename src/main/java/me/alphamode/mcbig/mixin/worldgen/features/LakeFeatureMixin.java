package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import me.alphamode.mcbig.math.BigConstants;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(LakeFeature.class)
public class LakeFeatureMixin implements BigFeatureExtension {
    @Shadow
    private int tile;

    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        x = x.subtract(BigConstants.EIGHT);
        z = z.subtract(BigConstants.EIGHT);

        while (y > 0 && level.isEmptyTile(x, y, z))
            y--;

        y -= 4;

        boolean[] grid = new boolean[/*2048*/16 * 16 * 8];
        int spots = random.nextInt(4) + 4;

        for (int i = 0; i < spots; ++i) {
            double xr = random.nextDouble() * 6 + 3;
            double yr = random.nextDouble() * 4 + 2;
            double zr = random.nextDouble() * 6 + 3;

            double xp = random.nextDouble() * (16 - xr - 2) + 1 + xr / 2;
            double yp = random.nextDouble() * (8 - yr - 4) + 2 + yr / 2;
            double zp = random.nextDouble() * (16 - zr - 2) + 1 + zr / 2;

            for (int xx = 1; xx < 15; ++xx) {
                for (int zz = 1; zz < 15; ++zz) {
                    for (int yy = 1; yy < 7; ++yy) {
                        double xd = (xx - xp) / (xr / 2);
                        double yd = (yy - yp) / (yr / 2);
                        double zd = (zz - zp) / (zr / 2);
                        double d = xd * xd + yd * yd + zd * zd;
                        if (d < (double) 1.0F) {
                            grid[(xx * 16 + zz) * 8 + yy] = true;
                        }
                    }
                }
            }
        }

        for (int xx = 0; xx < 16; ++xx) {
            for (int zz = 0; zz < 16; ++zz) {
                for (int yy = 0; yy < 8; ++yy) {
                    boolean check = !grid[(xx * 16 + zz) * 8 + yy] && (
                            xx < 15 && grid[((xx + 1) * 16 + zz) * 8 + yy]
                            || xx > 0 && grid[((xx - 1) * 16 + zz) * 8 + yy]
                            || zz < 15 && grid[(xx * 16 + zz + 1) * 8 + yy]
                            || zz > 0 && grid[(xx * 16 + (zz - 1)) * 8 + yy]
                            || yy < 7 && grid[(xx * 16 + zz) * 8 + yy + 1]
                            || yy > 0 && grid[(xx * 16 + zz) * 8 + (yy - 1)]);
                    if (check) {
                        Material m = level.getMaterial(x.add(BigInteger.valueOf(xx)), y + yy, z.add(BigInteger.valueOf(zz)));
                        if (yy >= 4 && m.isLiquid()) {
                            return false;
                        }

                        if (yy < 4 && !m.isSolid() && level.getTile(x.add(BigInteger.valueOf(xx)), y + yy, z.add(BigInteger.valueOf(zz))) != this.tile) {
                            return false;
                        }
                    }
                }
            }
        }

        for (int xx = 0; xx < 16; ++xx) {
            for (int zz = 0; zz < 16; ++zz) {
                for (int yy = 0; yy < 8; ++yy) {
                    if (grid[(xx * 16 + zz) * 8 + yy]) {
                        level.setTileNoUpdate(x.add(BigInteger.valueOf(xx)), y + yy, z.add(BigInteger.valueOf(zz)), yy >= 4 ? 0 : this.tile);
                    }
                }
            }
        }

        for (int xx = 0; xx < 16; ++xx) {
            for (int zz = 0; zz < 16; ++zz) {
                for (int yy = 4; yy < 8; ++yy) {
                    if (grid[(xx * 16 + zz) * 8 + yy] && level.getTile(x.add(BigInteger.valueOf(xx)), y + yy - 1, z.add(BigInteger.valueOf(zz))) == Tile.DIRT.id && level.getBrightness(LightLayer.SKY, x.add(BigInteger.valueOf(xx)), y + yy, z.add(BigInteger.valueOf(zz))) > 0) {
                        level.setTileNoUpdate(x.add(BigInteger.valueOf(xx)), y + yy - 1, z.add(BigInteger.valueOf(zz)), Tile.GRASS.id);
                    }
                }
            }
        }

        if (Tile.tiles[this.tile].material == Material.LAVA) {
            for (int xx = 0; xx < 16; ++xx) {
                for (int zz = 0; zz < 16; ++zz) {
                    for (int yy = 0; yy < 8; ++yy) {
                        boolean check = !grid[(xx * 16 + zz) * 8 + yy] && (
                                xx < 15 && grid[((xx + 1) * 16 + zz) * 8 + yy]
                                || xx > 0 && grid[((xx - 1) * 16 + zz) * 8 + yy]
                                || zz < 15 && grid[(xx * 16 + zz + 1) * 8 + yy]
                                || zz > 0 && grid[(xx * 16 + (zz - 1)) * 8 + yy]
                                || yy < 7 && grid[(xx * 16 + zz) * 8 + yy + 1]
                                || yy > 0 && grid[(xx * 16 + zz) * 8 + (yy - 1)]);
                        if (check && (yy < 4 || random.nextInt(2) != 0) && level.getMaterial(x.add(BigInteger.valueOf(xx)), y + yy, z.add(BigInteger.valueOf(zz))).isSolid()) {
                            level.setTileNoUpdate(x.add(BigInteger.valueOf(xx)), y + yy, z.add(BigInteger.valueOf(zz)), Tile.STONE.id);
                        }
                    }
                }
            }
        }

        return true;
    }
}
