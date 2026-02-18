package me.alphamode.mcbig.mixin.features.big_movement;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import me.alphamode.mcbig.extensions.PlayerExtension;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.math.BigMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.input.Input;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.BirchFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigDecimal;
import java.math.BigInteger;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player implements PlayerExtension, BigEntityExtension {
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
            if (this.input.isSneaking) {
                j--;
            }

            if (this.input.jumping) {
                j++;
            }

            if (j != 0) {
                this.yd += (double)((float)j * getFlySpeed() * 3.0F);
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

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean checkInBlock(double x, double y, double z) {
        BigInteger xt = BigMath.floor(x);
        int yt = Mth.floor(y);
        BigInteger zt = BigMath.floor(z);
        double xOff = x - (double)xt.doubleValue();
        double zOff = z - (double)zt.doubleValue();
        if (this.isSolidTile(xt, yt, zt) || this.isSolidTile(xt, yt + 1, zt)) {
            boolean var14 = !this.isSolidTile(xt.subtract(BigInteger.ONE), yt, zt) && !this.isSolidTile(xt.subtract(BigInteger.ONE), yt + 1, zt);
            boolean var15 = !this.isSolidTile(xt.add(BigInteger.ONE), yt, zt) && !this.isSolidTile(xt.add(BigInteger.ONE), yt + 1, zt);
            boolean var16 = !this.isSolidTile(xt, yt, zt.subtract(BigInteger.ONE)) && !this.isSolidTile(xt, yt + 1, zt.subtract(BigInteger.ONE));
            boolean var17 = !this.isSolidTile(xt, yt, zt.add(BigInteger.ONE)) && !this.isSolidTile(xt, yt + 1, zt.add(BigInteger.ONE));
            byte var18 = -1;
            double var19 = 9999.0;
            if (var14 && xOff < var19) {
                var19 = xOff;
                var18 = 0;
            }

            if (var15 && 1.0 - xOff < var19) {
                var19 = 1.0 - xOff;
                var18 = 1;
            }

            if (var16 && zOff < var19) {
                var19 = zOff;
                var18 = 4;
            }

            if (var17 && 1.0 - zOff < var19) {
                var19 = 1.0 - zOff;
                var18 = 5;
            }

            float var21 = 0.1F;
            if (var18 == 0) {
                this.xd = (double)(-var21);
            }

            if (var18 == 1) {
                this.xd = (double)var21;
            }

            if (var18 == 4) {
                this.zd = (double)(-var21);
            }

            if (var18 == 5) {
                this.zd = (double)var21;
            }
        }

        return false;
    }
}
