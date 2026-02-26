package me.alphamode.mcbig.mixin.items;

import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.WeaponItem;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(WeaponItem.class)
public abstract class WeaponItemMixin extends Item {
    protected WeaponItemMixin(int id) {
        super(id);
    }

    @Override
    public boolean mineBlock(ItemInstance item, int tile, BigInteger x, int y, BigInteger z, Mob entity) {
        item.hurtAndBreak(2, entity);
        return true;
    }
}
