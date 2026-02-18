package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.ChestTileEntity;
import net.minecraft.world.level.tile.entity.MobSpawnerTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(MonsterRoomFeature.class)
public abstract class MonsterRoomFeatureMixin implements BigFeatureExtension {
    @Shadow
    protected abstract ItemInstance generateLoot(Random random);

    @Shadow
    protected abstract String randomEntityId(Random random);

    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        byte var6 = 3;
        int irX = random.nextInt(2) + 2;
        int irZ = random.nextInt(2) + 2;
        BigInteger rX = BigInteger.valueOf(irX);
        BigInteger rZ = BigInteger.valueOf(irZ);
        int var9 = 0;

        for (BigInteger xt = x.subtract(rX).subtract(BigInteger.ONE); xt.compareTo(x.add(rX).add(BigInteger.ONE)) <= 0; xt = xt.add(BigInteger.ONE)) {
            for (int yt = y - 1; yt <= y + var6 + 1; yt++) {
                for (BigInteger zt = z.subtract(rZ).subtract(BigInteger.ONE); zt.compareTo(z.add(rZ).add(BigInteger.ONE)) <= 0; zt = zt.add(BigInteger.ONE)) {
                    Material var13 = level.getMaterial(xt, yt, zt);
                    if (yt == y - 1 && !var13.isSolid()) {
                        return false;
                    }

                    if (yt == y + var6 + 1 && !var13.isSolid()) {
                        return false;
                    }

                    if ((xt.equals(x.subtract(rX).subtract(BigInteger.ONE)) || xt.equals(x.add(rX).add(BigInteger.ONE)) || zt.equals(z.subtract(rZ).subtract(BigInteger.ONE)) || zt.equals(z.add(rZ).add(BigInteger.ONE)))
                            && yt == y
                            && level.isEmptyTile(xt, yt, zt)
                            && level.isEmptyTile(xt, yt + 1, zt)) {
                        var9++;
                    }
                }
            }
        }

        if (var9 >= 1 && var9 <= 5) {
            for (BigInteger xt = x.subtract(rX).subtract(BigInteger.ONE); xt.compareTo(x.add(rX).add(BigInteger.ONE)) <= 0; xt = xt.add(BigInteger.ONE)) {
                for (int yt = y + var6; yt >= y - 1; yt--) {
                    for (BigInteger zt = z.subtract(rZ).subtract(BigInteger.ONE); zt.compareTo(z.add(rZ).add(BigInteger.ONE)) <= 0; zt = zt.add(BigInteger.ONE)) {
                        if (!xt.equals(x.subtract(rX).subtract(BigInteger.ONE)) && yt != y - 1 && !zt.equals(z.subtract(rZ).subtract(BigInteger.ONE)) && !xt.equals(x.add(rX).add(BigInteger.ONE)) && yt != y + var6 + 1 && !zt.equals(z.add(rZ).add(BigInteger.ONE))) {
                            level.setTile(xt, yt, zt, 0);
                        } else if (yt >= 0 && !level.getMaterial(xt, yt - 1, zt).isSolid()) {
                            level.setTile(xt, yt, zt, 0);
                        } else if (level.getMaterial(xt, yt, zt).isSolid()) {
                            if (yt == y - 1 && random.nextInt(4) != 0) {
                                level.setTile(xt, yt, zt, Tile.MOSS_STONE.id);
                            } else {
                                level.setTile(xt, yt, zt, Tile.COBBLESTONE.id);
                            }
                        }
                    }
                }
            }

            for (int var20 = 0; var20 < 2; var20++) {
                for (int var23 = 0; var23 < 3; var23++) {
                    BigInteger xt = x.add(BigInteger.valueOf(random.nextInt(irX * 2 + 1) - irX));
                    BigInteger zt = z.add(BigInteger.valueOf(random.nextInt(irZ * 2 + 1) - irZ));
                    if (level.isEmptyTile(xt, y, zt)) {
                        int var15 = 0;
                        if (level.getMaterial(xt.subtract(BigInteger.ONE), y, zt).isSolid()) {
                            var15++;
                        }

                        if (level.getMaterial(xt.add(BigInteger.ONE), y, zt).isSolid()) {
                            var15++;
                        }

                        if (level.getMaterial(xt, y, zt.subtract(BigInteger.ONE)).isSolid()) {
                            var15++;
                        }

                        if (level.getMaterial(xt, y, zt.add(BigInteger.ONE)).isSolid()) {
                            var15++;
                        }

                        if (var15 == 1) {
                            level.setTile(xt, y, zt, Tile.CHEST.id);
                            ChestTileEntity chest = (ChestTileEntity)level.getTileEntity(xt, y, zt);

                            for (int var17 = 0; var17 < 8; var17++) {
                                ItemInstance var18 = this.generateLoot(random);
                                if (var18 != null) {
                                    chest.setItem(random.nextInt(chest.getContainerSize()), var18);
                                }
                            }
                            break;
                        }
                    }
                }
            }

            level.setTile(x, y, z, Tile.SPAWNER.id);
            MobSpawnerTileEntity var21 = (MobSpawnerTileEntity)level.getTileEntity(x, y, z);
            var21.setEntityId(this.randomEntityId(random));
            return true;
        } else {
            return false;
        }
    }
}
