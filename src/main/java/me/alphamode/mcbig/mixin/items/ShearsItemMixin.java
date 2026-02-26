package me.alphamode.mcbig.mixin.items;

import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(ShearsItem.class)
public abstract class ShearsItemMixin extends Item {
    protected ShearsItemMixin(int id) {
        super(id);
    }

    @Override
    public boolean mineBlock(ItemInstance item, int tile, BigInteger x, int y, BigInteger z, Mob entity) {
        if (tile == Tile.LEAVES.id || tile == Tile.WEB.id) {
            item.hurtAndBreak(1, entity);
        }

        return super.mineBlock(item, tile, x, y, z, entity);
    }
}
