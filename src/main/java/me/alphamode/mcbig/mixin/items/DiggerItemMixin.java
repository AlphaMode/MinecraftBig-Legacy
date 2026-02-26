package me.alphamode.mcbig.mixin.items;

import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(DiggerItem.class)
public abstract class DiggerItemMixin extends Item {
    protected DiggerItemMixin(int id) {
        super(id);
    }

    @Override
    public boolean mineBlock(ItemInstance item, int tile, BigInteger x, int y, BigInteger z, Mob entity) {
        item.hurtAndBreak(1, entity);
        return true;
    }
}
