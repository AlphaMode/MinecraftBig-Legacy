package me.alphamode.mcbig.mixin.commands;

import me.alphamode.mcbig.extensions.CommandPlayerExtension;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "isInWall", at = @At("HEAD"), cancellable = true)
    private void canNoclip(CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof CommandPlayerExtension plr && plr.canNoclip()) {
            cir.setReturnValue(false);
        }
    }
}
