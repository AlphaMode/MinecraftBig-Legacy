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
        int t = level.getTile(x, y, z);
        int aboveT = level.getTile(x, y + 1, z);
        if ((face == 0 || aboveT != 0 || t != Tile.GRASS.id) && t != Tile.DIRT.id) {
            return false;
        } else {
            Tile var10 = Tile.FARMLAND;
            level.playSound(x.doubleValue() + 0.5F, y + 0.5F, z.doubleValue() + 0.5F, var10.soundType.getStepSound(), (var10.soundType.getVolume() + 1.0F) / 2.0F, var10.soundType.getPitch() * 0.8F);
            if (level.isClientSide) {
                return true;
            } else {
                level.setTile(x, y, z, var10.id);
                item.hurtAndBreak(1, player);
                return true;
            }
        }
    }
}
