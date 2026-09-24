//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.math.BigConstants;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeMushroomFeature;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(HugeMushroomFeature.class)
public abstract class HugeMushroomFeatureMixin extends Feature {
    @Shadow
    private int forcedType;

    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        int type = random.nextInt(2);
        if (this.forcedType >= 0) type = this.forcedType;

        int treeHeight = random.nextInt(3) + 4;

        boolean free = true;
        if (y >= 1 && y + treeHeight + 1 <= 128) {
            for (int yy = y; yy <= y + 1 + treeHeight; yy++) {
                BigInteger r = BigConstants.THREE;
                if (yy == y) {
                    r = BigInteger.ZERO;
                }

                for (var xx = x.subtract(r); xx.compareTo(x.add(r)) <= 0 && free; xx = xx.add(BigInteger.ONE)) {
                    for (var zz = z.subtract(r); zz.compareTo(z.add(r)) <= 0 && free; zz = zz.add(BigInteger.ONE)) {
                        if (yy >= 0 && yy < 128) {
                            int tt = level.getTile(xx, yy, zz);
                            if (tt != 0 && tt != Tile.leaves.id) {
                                free = false;
                            }
                        } else {
                            free = false;
                        }
                    }
                }
            }

            if (!free) {
                return false;
            } else if (!Tile.mushroom1.mayPlace(level, x, y, z)) {
                return false;
            } else {
                level.setTileNoUpdate(x, y - 1, z, Tile.dirt.id);
                int var15 = y + treeHeight;
                if (type == 1) {
                    var15 = y + treeHeight - 3;
                }

                for (int yy = var15; yy <= y + treeHeight; yy++) {
                    BigInteger offs = BigInteger.ONE;
                    if (yy < y + treeHeight) offs = offs.add(BigInteger.ONE);
                    if (type == 0) offs = BigConstants.THREE;

                    for (var xx = x.subtract(offs); xx.compareTo(x.add(offs)) <= 0; xx = xx.add(BigInteger.ONE)) {
                        for (var zz = z.subtract(offs); zz.compareTo(z.add(offs)) <= 0; zz = zz.add(BigInteger.ONE)) {
                            int data = 5;
                            if (xx.equals(x.subtract(offs))) data--;
                            if (xx.equals(x.add(offs))) data++;
                            if (zz.equals(z.subtract(offs))) data -= 3;
                            if (zz.equals(z.add(offs))) data += 3;

                            if (type == 0 || yy < y + treeHeight) {
                                if ((xx.equals(x.subtract(offs)) || xx.equals(x.add(offs))) && (zz.equals(z.subtract(offs)) || zz.equals(z.add(offs)))) continue;

                                if (xx.equals(x.subtract((offs.subtract(BigInteger.ONE)))) && zz.equals(z.subtract(offs))) data = 1;
                                if (xx.equals(x.subtract(offs)) && zz.equals(z.subtract((offs.subtract(BigInteger.ONE))))) data = 1;

                                if (xx.equals(x.add((offs.subtract(BigInteger.ONE)))) && zz.equals(z.subtract(offs))) data = 3;
                                if (xx.equals(x.add(offs)) && zz.equals(z.subtract((offs.subtract(BigInteger.ONE))))) data = 3;

                                if (xx.equals(x.subtract((offs.subtract(BigInteger.ONE)))) && zz.equals(z.add(offs))) data = 7;
                                if (xx.equals(x.subtract(offs)) && zz.equals(z.add((offs.subtract(BigInteger.ONE))))) data = 7;

                                if (xx.equals(x.add((offs.subtract(BigInteger.ONE)))) && zz.equals(z.add(offs))) data = 9;
                                if (xx.equals(x.add(offs)) && zz.equals(z.add((offs.subtract(BigInteger.ONE))))) data = 9;
                            }

                            if (data == 5 && yy < y + treeHeight) {
                                data = 0;
                            }

                            if ((data != 0 || y >= y + treeHeight - 1) && !Tile.solid[level.getTile(xx, yy, zz)]) {
                                level.setTileAndDataNoUpdate(xx, yy, zz, Tile.hugeMushroom_brown.id + type, data);
                            }
                        }
                    }
                }

                for (int hh = 0; hh < treeHeight; hh++) {
                    int t = level.getTile(x, y + hh, z);
                    if (!Tile.solid[t]) {
                        level.setTileAndDataNoUpdate(x, y + hh, z, Tile.hugeMushroom_brown.id + type, 10);
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }
}
*///? }