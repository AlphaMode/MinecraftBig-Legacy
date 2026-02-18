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

        for (z = z.subtract(BigConstants.EIGHT); y > 0 && level.isEmptyTile(x, y, z); --y) {
        }

        y -= 4;
        boolean[] var6 = new boolean[2048];
        int var7 = random.nextInt(4) + 4;

        for (int var8 = 0; var8 < var7; ++var8) {
            double var9 = random.nextDouble() * (double) 6.0F + (double) 3.0F;
            double var11 = random.nextDouble() * (double) 4.0F + (double) 2.0F;
            double var13 = random.nextDouble() * (double) 6.0F + (double) 3.0F;
            double var15 = random.nextDouble() * ((double) 16.0F - var9 - (double) 2.0F) + (double) 1.0F + var9 / (double) 2.0F;
            double var17 = random.nextDouble() * ((double) 8.0F - var11 - (double) 4.0F) + (double) 2.0F + var11 / (double) 2.0F;
            double var19 = random.nextDouble() * ((double) 16.0F - var13 - (double) 2.0F) + (double) 1.0F + var13 / (double) 2.0F;

            for (int var21 = 1; var21 < 15; ++var21) {
                for (int var22 = 1; var22 < 15; ++var22) {
                    for (int var23 = 1; var23 < 7; ++var23) {
                        double var24 = ((double) var21 - var15) / (var9 / (double) 2.0F);
                        double var26 = ((double) var23 - var17) / (var11 / (double) 2.0F);
                        double var28 = ((double) var22 - var19) / (var13 / (double) 2.0F);
                        double var30 = var24 * var24 + var26 * var26 + var28 * var28;
                        if (var30 < (double) 1.0F) {
                            var6[(var21 * 16 + var22) * 8 + var23] = true;
                        }
                    }
                }
            }
        }

        for (int var35 = 0; var35 < 16; ++var35) {
            for (int var39 = 0; var39 < 16; ++var39) {
                for (int var10 = 0; var10 < 8; ++var10) {
                    boolean var46 = !var6[(var35 * 16 + var39) * 8 + var10] && (var35 < 15 && var6[((var35 + 1) * 16 + var39) * 8 + var10] || var35 > 0 && var6[((var35 - 1) * 16 + var39) * 8 + var10] || var39 < 15 && var6[(var35 * 16 + var39 + 1) * 8 + var10] || var39 > 0 && var6[(var35 * 16 + (var39 - 1)) * 8 + var10] || var10 < 7 && var6[(var35 * 16 + var39) * 8 + var10 + 1] || var10 > 0 && var6[(var35 * 16 + var39) * 8 + (var10 - 1)]);
                    if (var46) {
                        Material var12 = level.getMaterial(x.add(BigInteger.valueOf(var35)), y + var10, z.add(BigInteger.valueOf(var39)));
                        if (var10 >= 4 && var12.isLiquid()) {
                            return false;
                        }

                        if (var10 < 4 && !var12.isSolid() && level.getTile(x.add(BigInteger.valueOf(var35)), y + var10, z.add(BigInteger.valueOf(var39))) != this.tile) {
                            return false;
                        }
                    }
                }
            }
        }

        for (int var36 = 0; var36 < 16; ++var36) {
            for (int var40 = 0; var40 < 16; ++var40) {
                for (int var43 = 0; var43 < 8; ++var43) {
                    if (var6[(var36 * 16 + var40) * 8 + var43]) {
                        level.setTileNoUpdate(x.add(BigInteger.valueOf(var36)), y + var43, z.add(BigInteger.valueOf(var40)), var43 >= 4 ? 0 : this.tile);
                    }
                }
            }
        }

        for (int var37 = 0; var37 < 16; ++var37) {
            for (int var41 = 0; var41 < 16; ++var41) {
                for (int var44 = 4; var44 < 8; ++var44) {
                    if (var6[(var37 * 16 + var41) * 8 + var44] && level.getTile(x.add(BigInteger.valueOf(var37)), y + var44 - 1, z.add(BigInteger.valueOf(var41))) == Tile.DIRT.id && level.getBrightness(LightLayer.SKY, x.add(BigInteger.valueOf(var37)), y + var44, z.add(BigInteger.valueOf(var41))) > 0) {
                        level.setTileNoUpdate(x.add(BigInteger.valueOf(var37)), y + var44 - 1, z.add(BigInteger.valueOf(var41)), Tile.GRASS.id);
                    }
                }
            }
        }

        if (Tile.tiles[this.tile].material == Material.LAVA) {
            for (int var38 = 0; var38 < 16; ++var38) {
                for (int var42 = 0; var42 < 16; ++var42) {
                    for (int var45 = 0; var45 < 8; ++var45) {
                        boolean var47 = !var6[(var38 * 16 + var42) * 8 + var45] && (var38 < 15 && var6[((var38 + 1) * 16 + var42) * 8 + var45] || var38 > 0 && var6[((var38 - 1) * 16 + var42) * 8 + var45] || var42 < 15 && var6[(var38 * 16 + var42 + 1) * 8 + var45] || var42 > 0 && var6[(var38 * 16 + (var42 - 1)) * 8 + var45] || var45 < 7 && var6[(var38 * 16 + var42) * 8 + var45 + 1] || var45 > 0 && var6[(var38 * 16 + var42) * 8 + (var45 - 1)]);
                        if (var47 && (var45 < 4 || random.nextInt(2) != 0) && level.getMaterial(x.add(BigInteger.valueOf(var38)), y + var45, z.add(BigInteger.valueOf(var42))).isSolid()) {
                            level.setTileNoUpdate(x.add(BigInteger.valueOf(var38)), y + var45, z.add(BigInteger.valueOf(var42)), Tile.STONE.id);
                        }
                    }
                }
            }
        }

        return true;
    }
}
