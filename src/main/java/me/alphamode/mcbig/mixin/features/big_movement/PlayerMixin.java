package me.alphamode.mcbig.mixin.features.big_movement;

import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.math.BigDecimal;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity implements BigEntityExtension {
    public PlayerMixin(Level level) {
        super(level);
    }

    /**
     * @author
     * @reason
     */
    @Override
    public void setPos(double x, double y, double z) {
        setPos(BigDecimal.valueOf(x), y, BigDecimal.valueOf(z));
    }

    @Redirect(method = "drop(Lnet/minecraft/world/item/ItemInstance;Z)V", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemInstance;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemEntity createBigItem(Level level, double x, double y, double z, ItemInstance item) {
        ItemEntity entity = new ItemEntity(level, x, y, z, item);
        if (entity.isBigMovementEnabled() && isBigMovementEnabled()) {
            ((BigEntityExtension) entity).setPos(getX(), y, getZ());
            return entity;
        }
        entity.setPos(x, y, z);
        return entity;
    }

    @Override
    public boolean isBigMovementEnabled() {
        return true;
    }
}
