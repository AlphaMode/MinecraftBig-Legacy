package me.alphamode.mcbig.mixin.features.big_movement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.extensions.features.big_movement.BigMobExtension;
import me.alphamode.mcbig.world.phys.BigVec3;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.math.BigDecimal;

@Mixin(Mob.class)
public abstract class MobMixin extends Entity implements BigEntityExtension, BigMobExtension {
    @Shadow
    public abstract Vec3 getViewVector(float partialTick);

    public MobMixin(Level level) {
        super(level);
    }

    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;move(DDD)V"))
    private void bigMove(Mob instance, double xa, double ya, double za, Operation<Void> original) {
        if (isBigMovementEnabled()) {
            ((BigEntityExtension) instance).bigMove(xa, ya, za);
        } else {
            original.call(instance, xa, ya, za);
        }
    }

    @Override
    public BigVec3 getBigPos(float a) {
        if (a == 1.0F) {
            return BigVec3.newTemp(this.getX(), this.y, this.getZ());
        } else {
            BigDecimal bigAlpha = BigDecimal.valueOf(a);
            BigDecimal xp = this.getXO().add(this.getX().subtract(this.getXO()).multiply(bigAlpha));
            double yp = this.yo + (this.y - this.yo) * a;
            BigDecimal zp = this.getZO().add(this.getZ().subtract(this.getZO()).multiply(bigAlpha));
            return BigVec3.newTemp(xp, yp, zp);
        }
    }

    /**
     * @author
     * @reason
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public HitResult pick(double pickRange, float a) {
        BigVec3 pos = this.getBigPos(a);
        Vec3 view = this.getViewVector(a);
        BigVec3 var6 = pos.add(view.x * pickRange, view.y * pickRange, view.z * pickRange);
        return this.level.clip(pos, var6);
    }
}
