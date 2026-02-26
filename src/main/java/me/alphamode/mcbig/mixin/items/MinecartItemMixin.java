package me.alphamode.mcbig.mixin.items;

import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.item.Minecart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.RailTile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(MinecartItem.class)
public abstract class MinecartItemMixin extends Item {
    @Shadow
    public int f_66932009;

    protected MinecartItemMixin(int id) {
        super(id);
    }

    @Override
    public boolean useOn(ItemInstance item, Player player, Level level, BigInteger x, int y, BigInteger z, int face) {
        int t = level.getTile(x, y, z);
        if (RailTile.isRail(t)) {
            if (!level.isClientSide) {
                level.addEntity(new Minecart(level, x.doubleValue() + 0.5F, y + 0.5F, z.doubleValue() + 0.5F, this.f_66932009));
            }

            item.count--;
            return true;
        } else {
            return false;
        }
    }
}
