package me.alphamode.mcbig.mixin.items;

import net.minecraft.util.Facing;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(FlintAndSteelItem.class)
public abstract class FlintAndSteelItemMixin extends Item {
    protected FlintAndSteelItemMixin(int id) {
        super(id);
    }

    @Override
    public boolean useOn(ItemInstance item, Player player, Level level, BigInteger x, int y, BigInteger z, int face) {
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

        int t = level.getTile(x, y, z);
        if (t == 0) {
            level.playSound(x.doubleValue() + 0.5, y + 0.5, z.doubleValue() + 0.5, "fire.ignite", 1.0F, random.nextFloat() * 0.4F + 0.8F);
            level.setTile(x, y, z, Tile.FIRE.id);
        }

        item.hurtAndBreak(1, player);
        return true;
    }
}
