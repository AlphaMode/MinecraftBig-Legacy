package me.alphamode.mcbig.mixin.items;

import net.minecraft.util.Facing;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.item.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PaintingItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(PaintingItem.class)
public abstract class PaintingItemMixin extends Item {
    protected PaintingItemMixin(int id) {
        super(id);
    }

    @Override
    public boolean useOn(ItemInstance item, Player player, Level level, BigInteger x, int y, BigInteger z, int face) {
        if (face == Facing.DOWN) {
            return false;
        } else if (face == Facing.UP) {
            return false;
        } else {
            byte dir = 0;
            if (face == Facing.WEST) {
                dir = 1;
            }

            if (face == Facing.SOUTH) {
                dir = 2;
            }

            if (face == Facing.EAST) {
                dir = 3;
            }

            Painting painting = new Painting(level, x.intValue(), y, z.intValue(), dir);
            if (painting.survives()) {
                if (!level.isClientSide) {
                    level.addEntity(painting);
                }

                item.count--;
            }

            return true;
        }
    }
}
