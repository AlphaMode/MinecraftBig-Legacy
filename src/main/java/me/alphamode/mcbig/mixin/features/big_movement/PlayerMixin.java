package me.alphamode.mcbig.mixin.features.big_movement;

import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

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
}
