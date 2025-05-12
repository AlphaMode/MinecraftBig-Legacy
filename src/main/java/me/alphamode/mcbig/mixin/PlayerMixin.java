package me.alphamode.mcbig.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import me.alphamode.mcbig.extensions.PlayerExtension;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity implements PlayerExtension {

    private boolean flying = false;
    private float flyingSpeed = 0.05F;

    public PlayerMixin(Level level) {
        super(level);
    }

    @Override
    public boolean isFlying() {
        return this.flying;
    }

    @Override
    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    @Override
    public void setFlySpeed(float speed) {
        this.flyingSpeed = speed;
    }

    @Override
    public float getFlySpeed() {
        return this.flyingSpeed;
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void checkWasJumping(CallbackInfo ci, @Share("was_jumping") LocalBooleanRef flag) {
        if (isFlying()) {
            this.fallDistance = 0.0F;
        }
    }

    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;travel(FF)V"))
    private void preventFall(Player instance, float xxa, float zza, Operation<Void> original) {
        if (isFlying()) {
            double oldYd = this.yd;
            original.call(instance, xxa, zza);
            this.yd = oldYd * 0.6;
        } else {
            original.call(instance, xxa, zza);
        }
    }
}
