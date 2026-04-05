package me.alphamode.mcbig.mixin.items;

import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyePowderItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.CropTile;
import net.minecraft.world.level.tile.Sapling;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(DyePowderItem.class)
public abstract class DyePowderItemMixin extends Item {
    protected DyePowderItemMixin(int id) {
        super(id);
    }

    @Override
    public boolean useOn(ItemInstance item, Player player, Level level, BigInteger x, int y, BigInteger z, int face) {
        if (item.getAuxValue() == 15) { // white (bonemeal)
            int t = level.getTile(x, y, z);
            if (t == Tile.sapling.id) {
                if (!level.isClientSide) {
                    ((Sapling) Tile.sapling).growTree(level, x, y, z, level.random);
                    item.count--;
                }

                return true;
            }

            if (t == Tile.wheat.id) {
                if (!level.isClientSide) {
                    ((CropTile) Tile.wheat).growCropsToMax(level, x, y, z);
                    item.count--;
                }

                return true;
            }

            // I'm not even going to try to understand what the decompiler did here
            if (t == Tile.grass.id) {
                if (!level.isClientSide) {
                    item.count--;

                    label53:
                    for (int var9 = 0; var9 < 128; var9++) {
                        BigInteger tx = x;
                        int ty = y + 1;
                        BigInteger tz = z;

                        for (int var13 = 0; var13 < var9 / 16; var13++) {
                            tx = tx.add(BigInteger.valueOf(random.nextInt(3) - 1));
                            ty += (random.nextInt(3) - 1) * random.nextInt(3) / 2;
                            tz = tz.add(BigInteger.valueOf(random.nextInt(3) - 1));
                            if (level.getTile(tx, ty - 1, tz) != Tile.grass.id || level.isSolidBlockingTile(tx, ty, tz)) {
                                continue label53;
                            }
                        }

                        if (level.getTile(tx, ty, tz) == 0) {
                            if (random.nextInt(10) != 0) {
                                level.setTileAndData(tx, ty, tz, Tile.tallgrass.id, 1);
                            } else if (random.nextInt(3) != 0) {
                                level.setTile(tx, ty, tz, Tile.flower.id);
                            } else {
                                level.setTile(tx, ty, tz, Tile.rose.id);
                            }
                        }
                    }
                }

                return true;
            }
        }

        return false;
    }
}
