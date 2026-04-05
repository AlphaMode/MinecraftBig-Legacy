package me.alphamode.mcbig.mixin.items;

import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(HoeItem.class)
public abstract class HoeItemMixin extends Item {
    protected HoeItemMixin(int id) {
        super(id);
    }

    @Override
    public boolean useOn(ItemInstance item, Player player, Level level, BigInteger x, int y, BigInteger z, int face) {
        int targetType = level.getTile(x, y, z);
        int above = level.getTile(x, y + 1, z);

        if (face != 0 && above == 0 || (targetType == Tile.grass.id || targetType == Tile.dirt.id)) {
            Tile tile = Tile.farmland;
            level.playSound(x.doubleValue() + 0.5F, y + 0.5F, z.doubleValue() + 0.5F, tile.soundType.getStepSound(), (tile.soundType.getVolume() + 1.0F) / 2.0F, tile.soundType.getPitch() * 0.8F);

            if (level.isClientSide) return true;
            level.setTile(x, y, z, tile.id);
            item.hurtAndBreak(1, player);
            return true;
        }

        return false;
    }
}
