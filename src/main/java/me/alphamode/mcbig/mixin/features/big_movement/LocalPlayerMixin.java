package me.alphamode.mcbig.mixin.features.big_movement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.alphamode.mcbig.extensions.CommandPlayerExtension;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.math.BigMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.input.Input;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigDecimal;
import java.math.BigInteger;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player implements CommandPlayerExtension, BigEntityExtension {
    @Shadow public abstract void move(double x, double y, double z);

    @Shadow public Input input;

    @Shadow
    protected Minecraft minecraft;

    public LocalPlayerMixin(Level level) {
        super(level);
    }

    @Inject(method = "chat", at = @At("HEAD"))
    private void onChat(String msg, CallbackInfo ci) {
        this.minecraft.gui.addMessage("<" + this.name + "> " + msg);
    }

    private int jumpTriggerTime;

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void checkWasJumping(CallbackInfo ci, @Share("was_jumping") LocalBooleanRef flag) {
        if (this.jumpTriggerTime > 0) {
            this.jumpTriggerTime--;
        }

        flag.set(this.input.jumping);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;aiStep()V", shift = At.Shift.BEFORE))
    private void flyHeight(CallbackInfo ci, @Share("was_jumping") LocalBooleanRef flag) {

        if (canFly()) {
            if (!flag.get() && this.input.jumping) {
                if (this.jumpTriggerTime == 0) {
                    this.jumpTriggerTime = 7;
                } else {
                    setFlying(!isFlying());
                    if (isFlying() && this.onGround) {
                        this.jumpFromGround();
                    }

                    this.jumpTriggerTime = 0;
                }
            }
        }

        if (isFlying()) {
            int j = 0;
            if (this.input.sneaking) {
                j--;
            }

            if (this.input.jumping) {
                j++;
            }

            if (j != 0) {
                this.yd += (float)j * getFlySpeed() * 3.0F;
            }
        }
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void toggleFly(CallbackInfo ci) {
        if (!canFly()) {
            setFlying(false);
        }
        if (this.onGround && isFlying()) {
            setFlying(false);
        }
    }

    private boolean isSolidTile(BigInteger x, int y, BigInteger z) {
        return this.level.isSolidBlockingTile(x, y, z);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;checkInBlock(DDD)Z", ordinal = 0))
    private void calculateBigBBWidth(CallbackInfo ci, @Share("bb")LocalRef<BigDecimal> bbRef) {
        if (isBigMovementEnabled()) {
            bbRef.set(new BigDecimal(this.bbWidth * 0.35));
        }
    }

    @WrapOperation(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;checkInBlock(DDD)Z",
                    ordinal = 0
            )
    )
    private boolean useBigCheckInBlock0(LocalPlayer instance, double x, double y, double z, Operation<Boolean> original, @Share("bb")LocalRef<BigDecimal> bbRef) {
        if (isBigMovementEnabled()) {
            BigDecimal bbWidth = bbRef.get();
            return checkInBlock(getX().subtract(bbWidth), this.bb.y0 + 0.5, getZ().add(bbWidth)); // 0
        }
        return original.call(instance, x, y, z);
    }

    @WrapOperation(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;checkInBlock(DDD)Z",
                    ordinal = 1
            )
    )
    private boolean useBigCheckInBlock1(LocalPlayer instance, double x, double y, double z, Operation<Boolean> original, @Share("bb")LocalRef<BigDecimal> bbRef) {
        if (isBigMovementEnabled()) {
            BigDecimal bbWidth = bbRef.get();
            return checkInBlock(getX().subtract(bbWidth), this.bb.y0 + 0.5, getZ().subtract(bbWidth)); // 1
        }
        return original.call(instance, x, y, z);
    }

    @WrapOperation(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;checkInBlock(DDD)Z",
                    ordinal = 2
            )
    )
    private boolean useBigCheckInBlock2(LocalPlayer instance, double x, double y, double z, Operation<Boolean> original, @Share("bb")LocalRef<BigDecimal> bbRef) {
        if (isBigMovementEnabled()) {
            BigDecimal bbWidth = bbRef.get();
            return checkInBlock(getX().add(bbWidth), this.bb.y0 + 0.5, getZ().subtract(bbWidth)); // 2
        }
        return original.call(instance, x, y, z);
    }

    @WrapOperation(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;checkInBlock(DDD)Z",
                    ordinal = 3
            )
    )
    private boolean useBigCheckInBlock3(LocalPlayer instance, double x, double y, double z, Operation<Boolean> original, @Share("bb")LocalRef<BigDecimal> bbRef) {
        if (isBigMovementEnabled()) {
            BigDecimal bbWidth = bbRef.get();
            return checkInBlock(getX().add(bbWidth), this.bb.y0 + 0.5, getZ().add(bbWidth)); // 3
        }
        return original.call(instance, x, y, z);
    }

    @Override
    public boolean checkInBlock(BigDecimal x, double y, BigDecimal z) {
        if (canNoclip())
            return false;
        BigInteger xTile = BigMath.floor(x);
        int yTile = Mth.floor(y);
        BigInteger zTile = BigMath.floor(z);
        double xd = x.subtract(new BigDecimal(xTile)).doubleValue();
        double zd = z.subtract(new BigDecimal(zTile)).doubleValue();
        if (this.isSolidTile(xTile, yTile, zTile) || this.isSolidTile(xTile, yTile + 1, zTile)) {
            BigInteger xMinusOne = xTile.subtract(BigInteger.ONE);
            BigInteger xPlusOne = xTile.add(BigInteger.ONE);
            BigInteger zMinusOne = zTile.subtract(BigInteger.ONE);
            BigInteger zPlusOne = zTile.add(BigInteger.ONE);
            boolean west = !this.isSolidTile(xMinusOne, yTile, zTile) && !this.isSolidTile(xMinusOne, yTile + 1, zTile);
            boolean east = !this.isSolidTile(xPlusOne, yTile, zTile) && !this.isSolidTile(xPlusOne, yTile + 1, zTile);
            boolean north = !this.isSolidTile(xTile, yTile, zMinusOne) && !this.isSolidTile(xTile, yTile + 1, zMinusOne);
            boolean south = !this.isSolidTile(xTile, yTile, zPlusOne) && !this.isSolidTile(xTile, yTile + 1, zPlusOne);
            int dir = -1;
            double closest = 9999.0;
            if (west && xd < closest) {
                closest = xd;
                dir = 0;
            }

            if (east && 1.0 - xd < closest) {
                closest = 1.0 - xd;
                dir = 1;
            }

            if (north && zd < closest) {
                closest = zd;
                dir = 4;
            }

            if (south && 1.0 - zd < closest) {
                closest = 1.0 - zd;
                dir = 5;
            }

            float speed = 0.1F;
            if (dir == 0) this.xd = -speed;
            if (dir == 1) this.xd = speed;
            if (dir == 4) this.zd = -speed;
            if (dir == 5) this.zd = speed;
        }

        return false;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean checkInBlock(double x, double y, double z) {
        if (canNoclip())
            return false;
        BigInteger xTile = BigMath.floor(x);
        int yTile = Mth.floor(y);
        BigInteger zTile = BigMath.floor(z);
        double xd = x - (double)xTile.doubleValue();
        double zd = z - (double)zTile.doubleValue();
        if (this.isSolidTile(xTile, yTile, zTile) || this.isSolidTile(xTile, yTile + 1, zTile)) {
            boolean west = !this.isSolidTile(xTile.subtract(BigInteger.ONE), yTile, zTile) && !this.isSolidTile(xTile.subtract(BigInteger.ONE), yTile + 1, zTile);
            boolean east = !this.isSolidTile(xTile.add(BigInteger.ONE), yTile, zTile) && !this.isSolidTile(xTile.add(BigInteger.ONE), yTile + 1, zTile);
            boolean north = !this.isSolidTile(xTile, yTile, zTile.subtract(BigInteger.ONE)) && !this.isSolidTile(xTile, yTile + 1, zTile.subtract(BigInteger.ONE));
            boolean south = !this.isSolidTile(xTile, yTile, zTile.add(BigInteger.ONE)) && !this.isSolidTile(xTile, yTile + 1, zTile.add(BigInteger.ONE));
            int dir = -1;
            double closest = 9999.0;
            if (west && xd < closest) {
                closest = xd;
                dir = 0;
            }

            if (east && 1.0 - xd < closest) {
                closest = 1.0 - xd;
                dir = 1;
            }

            if (north && zd < closest) {
                closest = zd;
                dir = 4;
            }

            if (south && 1.0 - zd < closest) {
                closest = 1.0 - zd;
                dir = 5;
            }

            float speed = 0.1F;
            if (dir == 0) this.xd = -speed;
            if (dir == 1) this.xd = speed;
            if (dir == 4) this.zd = -speed;
            if (dir == 5) this.zd = speed;
        }

        return false;
    }
}
