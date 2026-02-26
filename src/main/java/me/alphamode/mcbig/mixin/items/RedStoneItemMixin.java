package me.alphamode.mcbig.mixin.items;

import net.minecraft.util.Facing;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RedStoneItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(RedStoneItem.class)
public abstract class RedStoneItemMixin extends Item {
    protected RedStoneItemMixin(int id) {
        super(id);
    }

    @Override
    public boolean useOn(ItemInstance item, Player player, Level level, BigInteger x, int y, BigInteger z, int face) {
        if (level.getTile(x, y, z) != Tile.SNOW_LAYER.id) {
            if (face == Facing.DOWN) {
                y--;
            }

            if (face == Facing.UP) {
                y++;
            }

            if (face == Facing.NORTH) {
                z = z.subtract(BigInteger.ONE);
            }

            if (face == Facing.SOUTH) {
                z = z.add(BigInteger.ONE);
            }

            if (face == Facing.WEST) {
                x = x.subtract(BigInteger.ONE);
            }

            if (face == Facing.EAST) {
                x = x.add(BigInteger.ONE);
            }

            if (!level.isEmptyTile(x, y, z)) {
                return false;
            }
        }

        if (Tile.REDSTONE.mayPlace(level, x, y, z)) {
            item.count--;
            level.setTile(x, y, z, Tile.REDSTONE.id);
        }

        return true;
    }
}
