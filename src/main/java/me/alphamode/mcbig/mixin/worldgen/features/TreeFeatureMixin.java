package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;
import java.util.Random;

@Mixin(TreeFeature.class)
public class TreeFeatureMixin implements BigFeatureExtension {
    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        int var6 = random.nextInt(3) + 4;
        boolean var7 = true;
        if (y >= 1 && y + var6 + 1 <= 128) {
            for (int yt = y; yt <= y + 1 + var6; yt++) {
                byte var9 = 1;
                if (yt == y) {
                    var9 = 0;
                }

                if (yt >= y + 1 + var6 - 2) {
                    var9 = 2;
                }

                BigInteger bigvar9 = BigInteger.valueOf(var9);

                for (BigInteger xt = x.subtract(bigvar9); xt.compareTo(x.add(bigvar9)) <= 0 && var7; xt = xt.add(BigInteger.ONE)) {
                    for (BigInteger zt = z.subtract(bigvar9); zt.compareTo(z.add(bigvar9)) <= 0 && var7; zt = zt.add(BigInteger.ONE)) {
                        if (yt >= 0 && yt < 128) {
                            int var12 = level.getTile(xt, yt, zt);
                            if (var12 != 0 && var12 != Tile.LEAVES.id) {
                                var7 = false;
                            }
                        } else {
                            var7 = false;
                        }
                    }
                }
            }

            if (!var7) {
                return false;
            } else {
                int var16 = level.getTile(x, y - 1, z);
                if ((var16 == Tile.GRASS.id || var16 == Tile.DIRT.id) && y < 128 - var6 - 1) {
                    level.setTileNoUpdate(x, y - 1, z, Tile.DIRT.id);

                    for (int yt = y - 3 + var6; yt <= y + var6; yt++) {
                        int var19 = yt - (y + var6);
                        int var21 = 1 - var19 / 2;
                        BigInteger bigvar21 = BigInteger.valueOf(var21);

                        for (BigInteger xt = x.subtract(bigvar21); xt.compareTo(x.add(bigvar21)) <= 0; xt = xt.add(BigInteger.ONE)) {
                            int var13 = xt.subtract(x).intValue();

                            for (BigInteger zt = z.subtract(bigvar21); zt.compareTo(z.add(bigvar21)) <= 0; zt = zt.add(BigInteger.ONE)) {
                                int var15 = zt.subtract(z).intValue();
                                if ((Math.abs(var13) != var21 || Math.abs(var15) != var21 || random.nextInt(2) != 0 && var19 != 0) && !Tile.solid[level.getTile(xt, yt, zt)]) {
                                    level.setTileNoUpdate(xt, yt, zt, Tile.LEAVES.id);
                                }
                            }
                        }
                    }

                    for (int var18 = 0; var18 < var6; var18++) {
                        int var20 = level.getTile(x, y + var18, z);
                        if (var20 == 0 || var20 == Tile.LEAVES.id) {
                            level.setTileNoUpdate(x, y + var18, z, Tile.LOG.id);
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }
}
