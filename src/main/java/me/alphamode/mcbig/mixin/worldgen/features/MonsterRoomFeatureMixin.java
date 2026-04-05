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
        int hr = 3;
        int xr = random.nextInt(2) + 2;
        int zr = random.nextInt(2) + 2;
        BigInteger xrBig = BigInteger.valueOf(xr);
        BigInteger zrBig = BigInteger.valueOf(zr);

        int holeCount = 0;
        for (BigInteger xx = x.subtract(xrBig).subtract(BigInteger.ONE); xx.compareTo(x.add(xrBig).add(BigInteger.ONE)) <= 0; xx = xx.add(BigInteger.ONE)) {
            for (int yy = y - 1; yy <= y + hr + 1; yy++) {
                for (BigInteger zz = z.subtract(zrBig).subtract(BigInteger.ONE); zz.compareTo(z.add(zrBig).add(BigInteger.ONE)) <= 0; zz = zz.add(BigInteger.ONE)) {
                    Material m = level.getMaterial(xx, yy, zz);
                    if (yy == y - 1 && !m.isSolid()) return false;
                    if (yy == y + hr + 1 && !m.isSolid()) return false;

                    if (( xx.equals(x.subtract(xrBig).subtract(BigInteger.ONE))
                            || xx.equals(x.add(xrBig).add(BigInteger.ONE))
                            || zz.equals(z.subtract(zrBig).subtract(BigInteger.ONE))
                            || zz.equals(z.add(zrBig).add(BigInteger.ONE)) )
                            && yy == y
                            && level.isEmptyTile(xx, yy, zz)
                            && level.isEmptyTile(xx, yy + 1, zz)) {
                        holeCount++;
                    }
                }
            }
        }

        if (holeCount < 1 || holeCount > 5) return false;

        for (BigInteger xx = x.subtract(xrBig).subtract(BigInteger.ONE); xx.compareTo(x.add(xrBig).add(BigInteger.ONE)) <= 0; xx = xx.add(BigInteger.ONE)) {
            for (int yy = y + hr; yy >= y - 1; yy--) {
                for (BigInteger zz = z.subtract(zrBig).subtract(BigInteger.ONE); zz.compareTo(z.add(zrBig).add(BigInteger.ONE)) <= 0; zz = zz.add(BigInteger.ONE)) {
                    if (!xx.equals(x.subtract(xrBig).subtract(BigInteger.ONE))
                        && yy != y - 1
                        && !zz.equals(z.subtract(zrBig).subtract(BigInteger.ONE))
                        && !xx.equals(x.add(xrBig).add(BigInteger.ONE))
                        && yy != y + hr + 1
                        && !zz.equals(z.add(zrBig).add(BigInteger.ONE))
                    ) {
                        level.setTile(xx, yy, zz, 0);
                    } else if (yy >= 0 && !level.getMaterial(xx, yy - 1, zz).isSolid()) {
                        level.setTile(xx, yy, zz, 0);
                    } else if (level.getMaterial(xx, yy, zz).isSolid()) {
                        if (yy == y - 1 && random.nextInt(4) != 0) {
                            level.setTile(xx, yy, zz, Tile.mossyCobblestone.id);
                        } else {
                            level.setTile(xx, yy, zz, Tile.cobblestone.id);
                        }
                    }
                }
            }
        }

        for (int cc = 0; cc < 2; cc++) {
            for (int i = 0; i < 3; i++) {
                BigInteger xc = x.add(BigInteger.valueOf(random.nextInt(xr * 2 + 1) - xr));
                BigInteger zc = z.add(BigInteger.valueOf(random.nextInt(zr * 2 + 1) - zr));
                if (!level.isEmptyTile(xc, y, zc)) continue;

                int count = 0;
                if (level.getMaterial(xc.subtract(BigInteger.ONE), y, zc).isSolid()) count++;
                if (level.getMaterial(xc.add(BigInteger.ONE), y, zc).isSolid()) count++;
                if (level.getMaterial(xc, y, zc.subtract(BigInteger.ONE)).isSolid()) count++;
                if (level.getMaterial(xc, y, zc.add(BigInteger.ONE)).isSolid()) count++;

                if (count != 1) continue;

                level.setTile(xc, y, zc, Tile.chest.id);
                ChestTileEntity chest = (ChestTileEntity) level.getTileEntity(xc, y, zc);

                for (int j = 0; j < 8; j++) {
                    ItemInstance item = generateLoot(random);
                    if (item != null) chest.setItem(random.nextInt(chest.getContainerSize()), item);
                }
                break;
            }
        }

        level.setTile(x, y, z, Tile.mobSpawner.id);
        MobSpawnerTileEntity entity = (MobSpawnerTileEntity) level.getTileEntity(x, y, z);
        entity.setEntityId(randomEntityId(random));
        return true;
    }
}
