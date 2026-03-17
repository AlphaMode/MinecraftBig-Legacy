package me.alphamode.mcbig.mixin.features.big_movement;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.extensions.features.big_movement.BigMobExtension;
import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.phys.BigVec3;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
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

    @Shadow
    public abstract boolean onLadder();

    @Shadow
    public float walkAnimSpeedO;

    @Shadow
    public float walkAnimSpeed;

    @Shadow
    public float walkAnimPos;

    public MobMixin(Level level) {
        super(level);
    }

//    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;move(DDD)V"))
//    private void bigMove(Mob instance, double xa, double ya, double za, Operation<Void> original) {
//        if (isBigMovementEnabled()) {
//            ((BigEntityExtension) instance).bigMove(xa, ya, za);
//        } else {
//            original.call(instance, xa, ya, za);
//        }
//    }

    @Definition(id = "x", field = "Lnet/minecraft/world/entity/Mob;x:D")
    @Definition(id = "xo", field = "Lnet/minecraft/world/entity/Mob;xo:D")
    @Expression("this.x - this.xo")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private double fixXRel(double original) {
        if (!isBigMovementEnabled()) {
            return original;
        }
        return this.getX().subtract(this.getXO()).doubleValue();
    }

    @Definition(id = "z", field = "Lnet/minecraft/world/entity/Mob;z:D")
    @Definition(id = "zo", field = "Lnet/minecraft/world/entity/Mob;zo:D")
    @Expression("this.z - this.zo")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private double fixZRel(double original) {
        if (!isBigMovementEnabled()) {
            return original;
        }
        return this.getZ().subtract(this.getZO()).doubleValue();
    }

    @WrapMethod(method = "travel")
    private void bigTravel(float xa, float ya, Operation<Void> original) {
        if (!isBigMovementEnabled()) {
            original.call(xa, ya);
            return;
        }

        if (isInWater()) {
            double yo = this.y;
            moveRelative(xa, ya, 0.02F);
            bigMove(this.xd, this.yd, this.zd);

            this.xd *= 0.8F;
            this.yd *= 0.8F;
            this.zd *= 0.8F;
            this.yd -= 0.02;

            if (this.horizontalCollision && isFree(this.xd, this.yd + 0.6F - this.y + yo, this.zd)) {
                this.yd = 0.3F;
            }
        } else if (isInLava()) {
            double yo = this.y;
            moveRelative(xa, ya, 0.02F);
            bigMove(this.xd, this.yd, this.zd);
            this.xd *= 0.5;
            this.yd *= 0.5;
            this.zd *= 0.5;
            this.yd -= 0.02;

            if (this.horizontalCollision && isFree(this.xd, this.yd + 0.6F - this.y + yo, this.zd)) {
                this.yd = 0.3F;
            }
        } else {
            float friction = 0.91F;
            if (this.onGround) {
                friction = 0.6f * 0.91f;
                int t = this.level.getTile(BigMath.floor(getX()), Mth.floor(this.bb.y0) - 1, BigMath.floor(getZ()));
                if (t > 0) {
                    friction = Tile.tiles[t].friction * 0.91F;
                }
            }

            float friction2 = 0.16277136F /*(0.6f * 0.6f * 0.91f * 0.91f * 0.6f * 0.91f)*/ / (friction * friction * friction);
            moveRelative(xa, ya, this.onGround ? 0.1F * friction2 : 0.02F);
            friction = 0.91F;
            if (this.onGround) {
                friction = 0.6f * 0.91f;
                int t = this.level.getTile(BigMath.floor(getX()), Mth.floor(this.bb.y0) - 1, BigMath.floor(getZ()));
                if (t > 0) {
                    friction = Tile.tiles[t].friction * 0.91F;
                }
            }

            if (onLadder()) {
                float max = 0.15F;
                if (this.xd < -max) {
                    this.xd = -max;
                }

                if (this.xd > max) {
                    this.xd = max;
                }

                if (this.zd < -max) {
                    this.zd = -max;
                }

                if (this.zd > max) {
                    this.zd = max;
                }

                this.fallDistance = 0.0F;
                if (this.yd < -0.15) {
                    this.yd = -0.15;
                }

                if (isSneaking() && this.yd < 0.0) {
                    this.yd = 0.0;
                }
            }

            bigMove(this.xd, this.yd, this.zd);
            if (this.horizontalCollision && onLadder()) {
                this.yd = 0.2;
            }

            this.yd -= 0.08;
            this.yd *= 0.98F;
            this.xd *= friction;
            this.zd *= friction;
        }

        this.walkAnimSpeedO = this.walkAnimSpeed;
        double xxd = getX().subtract(getXO()).doubleValue();
        double zzd = getZ().subtract(getZO()).doubleValue();
        float wst = Mth.sqrt(xxd * xxd + zzd * zzd) * 4.0F;
        if (wst > 1.0F) {
            wst = 1.0F;
        }

        this.walkAnimSpeed = this.walkAnimSpeed + (wst - this.walkAnimSpeed) * 0.4F;
        this.walkAnimPos = this.walkAnimPos + this.walkAnimSpeed;
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
