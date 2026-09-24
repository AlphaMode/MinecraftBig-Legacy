package me.alphamode.mcbig.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import me.alphamode.mcbig.extensions.BigPlayerExtension;
import me.alphamode.mcbig.extensions.CommandPlayerExtension;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import net.minecraft.world.entity.Entity;
//? >=1.0.0-beta.8.0.r
//import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigDecimal;
import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin extends Mob implements BigPlayerExtension, CommandPlayerExtension {

    //? >=1.0.0-beta.8.0.r
    //@Shadow public Abilities abilities;
    private boolean noclip = false;
    //? <1.0.0-beta.8.0.r {
    private boolean canFly = false;
    private boolean flying = false;
    private float flyingSpeed = 0.05F;
    //? }

    public PlayerMixin(Level level) {
        super(level);
    }

    @Override
    public boolean canFly() {
        //? >=1.0.0-beta.8.0.r {
        /*return this.abilities.mayfly;
        *///? } else
        return this.canFly;
    }

    @Override
    public void setCanFly(boolean canFly) {
        //? >=1.0.0-beta.8.0.r {
        /*this.abilities.mayfly = canFly;
        *///? } else
        this.canFly = canFly;
    }

    @Override
    public boolean isFlying() {
        //? >=1.0.0-beta.8.0.r {
        /*return this.abilities.flying;
        *///? } else
        return this.flying;
    }

    @Override
    public void setFlying(boolean flying) {
        //? >=1.0.0-beta.8.0.r {
        /*this.abilities.flying = flying;
        *///? } else
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

    @Override
    public void setNoclip(boolean noclip) {
        this.noPhysics = noclip;
        this.noclip = noclip;
    }

    @Override
    public boolean canNoclip() {
        return this.noclip;
    }

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private List<Entity> bigGetEntities(Level instance, Entity entity, AABB bb, Operation<List<Entity>> original) {
        if (isBigMovementEnabled())
            return instance.getEntities(entity, ((BigEntityExtension) this).getBigBB().inflate(1.0, 0.0, 1.0));
        return original.call(instance, entity, bb);
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

    @Override
    public void teleport(BigDecimal x, double y, BigDecimal z) {
        ((BigEntityExtension) this).setPos(x, y, z);
    }
}
