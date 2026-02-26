package me.alphamode.mcbig.mixin.items;

import net.minecraft.util.Facing;
import net.minecraft.util.Mth;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DoorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(DoorItem.class)
public abstract class DoorItemMixin extends Item {
    @Shadow
    private Material material;

    protected DoorItemMixin(int id) {
        super(id);
    }

    @Override
    public boolean useOn(ItemInstance item, Player player, Level level, BigInteger x, int y, BigInteger z, int face) {
        if (face != Facing.UP) {
            return false;
        } else {
            y++;
            Tile door;
            if (this.material == Material.WOOD) {
                door = Tile.DOOR;
            } else {
                door = Tile.IRON_DOOR;
            }

            if (!door.mayPlace(level, x, y, z)) {
                return false;
            } else {
                int dir = Mth.floor((player.yRot + 180.0F) * 4.0F / 360.0F - 0.5) & 3;
                BigInteger xo = BigInteger.ZERO;
                BigInteger zo = BigInteger.ZERO;
                if (dir == 0) {
                    zo = BigInteger.ONE;
                }

                if (dir == 1) {
                    xo = BigInteger.ONE.negate();
                }

                if (dir == 2) {
                    zo = BigInteger.ONE.negate();
                }

                if (dir == 3) {
                    xo = BigInteger.ONE;
                }

                int var12 = (level.isSolidBlockingTile(x.subtract(xo), y, z.subtract(zo)) ? 1 : 0) + (level.isSolidBlockingTile(x.subtract(xo), y + 1, z.subtract(zo)) ? 1 : 0);
                int var13 = (level.isSolidBlockingTile(x.add(xo), y, z.add(zo)) ? 1 : 0) + (level.isSolidBlockingTile(x.add(xo), y + 1, z.add(zo)) ? 1 : 0);
                boolean isDoor1 = level.getTile(x.subtract(xo), y, z.subtract(zo)) == door.id || level.getTile(x.subtract(xo), y + 1, z.subtract(zo)) == door.id;
                boolean isDoor2 = level.getTile(x.add(xo), y, z.add(zo)) == door.id || level.getTile(x.add(xo), y + 1, z.add(zo)) == door.id;
                boolean var16 = false;
                if (isDoor1 && !isDoor2) {
                    var16 = true;
                } else if (var13 > var12) {
                    var16 = true;
                }

                if (var16) {
                    dir = dir - 1 & 3;
                    dir += 4;
                }

                level.noNeighborUpdate = true;
                level.setTileAndData(x, y, z, door.id, dir);
                level.setTileAndData(x, y + 1, z, door.id, dir + 8);
                level.noNeighborUpdate = false;
                level.updateNeighborsAt(x, y, z, door.id);
                level.updateNeighborsAt(x, y + 1, z, door.id);
                item.count--;
                return true;
            }
        }
    }
}
