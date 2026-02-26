package me.alphamode.mcbig.mixin.items;

import net.minecraft.util.Facing;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SeedItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(SeedItem.class)
public abstract class SeedItemMixin extends Item {
    @Shadow
    private int plantId;

    protected SeedItemMixin(int id) {
        super(id);
    }

    @Override
    public boolean useOn(ItemInstance item, Player player, Level level, BigInteger x, int y, BigInteger z, int face) {
        if (face != Facing.UP) {
            return false;
        } else {
            int t = level.getTile(x, y, z);
            if (t == Tile.FARMLAND.id && level.isEmptyTile(x, y + 1, z)) {
                level.setTile(x, y + 1, z, this.plantId);
                item.count--;
                return true;
            } else {
                return false;
            }
        }
    }
}
