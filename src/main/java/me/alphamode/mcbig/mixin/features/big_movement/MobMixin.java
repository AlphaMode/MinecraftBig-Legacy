package me.alphamode.mcbig.mixin.features.big_movement;

import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mob.class)
public class MobMixin {
    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;move(DDD)V"))
    private void bigMove(Mob mob, double x, double y, double z) {
        ((BigEntityExtension) mob).bigMove(x, y, z);
    }
}
