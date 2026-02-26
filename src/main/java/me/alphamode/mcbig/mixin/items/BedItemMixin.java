package me.alphamode.mcbig.mixin.items;

import net.minecraft.util.Facing;
import net.minecraft.util.Mth;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.BedTile;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(BedItem.class)
public abstract class BedItemMixin extends Item {
    protected BedItemMixin(int id) {
        super(id);
    }

    @Override
    public boolean useOn(ItemInstance item, Player player, Level level, BigInteger x, int y, BigInteger z, int face) {
        if (face != Facing.UP) {
            return false;
        } else {
            y++;
            BedTile bed = (BedTile) Tile.BED;
            int dir = Mth.floor(player.yRot * 4.0F / 360.0F + 0.5) & 3;
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

            if (level.isEmptyTile(x, y, z)
                    && level.isEmptyTile(x.add(xo), y, z.add(zo))
                    && level.isSolidBlockingTile(x, y - 1, z)
                    && level.isSolidBlockingTile(x.add(xo), y - 1, z.add(zo))) {
                level.setTileAndData(x, y, z, bed.id, dir);
                level.setTileAndData(x.add(xo), y, z.add(zo), bed.id, dir + 8);
                item.count--;
                return true;
            } else {
                return false;
            }
        }
    }
}
