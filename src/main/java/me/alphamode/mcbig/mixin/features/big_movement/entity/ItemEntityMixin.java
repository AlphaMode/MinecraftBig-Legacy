package me.alphamode.mcbig.mixin.features.big_movement.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.math.BigMath;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.math.BigInteger;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements BigEntityExtension {
    @Shadow
    public int throwTime;

    @Shadow
    public int age;

    public ItemEntityMixin(Level level) {
        super(level);
    }

    @WrapOperation(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemInstance;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setPos(DDD)V"))
    private void dontSetPos(ItemEntity instance, double x, double y, double z, Operation<Void> original) {
        if (!isBigMovementEnabled())
            original.call(instance, x, y, z);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tick() {
        super.tick();
        if (this.throwTime > 0) {
            this.throwTime--;
        }

        boolean big = isBigMovementEnabled();

        BigInteger xt;
        BigInteger zt;

        if (big) {
            setXO(getX());
            setZO(getZ());

            this.xo = getXO().doubleValue();
            this.zo = getZO().doubleValue();
            xt = BigMath.floor(getX());
            zt = BigMath.floor(getZ());
        } else {
            this.xo = this.x;
            this.zo = this.z;
            xt = BigMath.floor(this.x);
            zt = BigMath.floor(this.z);
        }

        this.yo = this.y;

        this.yd -= 0.04F;
        if (this.level.getMaterial(xt, Mth.floor(this.y), zt) == Material.lava) {
            this.yd = 0.2F;
            this.xd = (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
            this.zd = (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
            this.level.playSound(this, "random.fizz", 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
        }

        if (big) {
            checkInBlock(getX(), (this.bb.y0 + this.bb.y1) / 2.0, getZ());
            bigMove(this.xd, this.yd, this.zd);
        } else {
            checkInBlock(this.x, (this.bb.y0 + this.bb.y1) / 2.0, this.z);
            move(this.xd, this.yd, this.zd);
        }
        float friction = 0.98F;
        if (this.onGround) {
            friction = 0.6f * 0.98f;
            int t = big ? this.level.getTile(BigMath.floor(getX()), Mth.floor(this.bb.y0) - 1, BigMath.floor(getZ())) : this.level.getTile(BigMath.floor(this.x), Mth.floor(this.bb.y0) - 1, BigMath.floor(this.z));
            if (t > 0) {
                friction = Tile.tiles[t].friction * 0.98F;
            }
        }

        this.xd *= friction;
        this.yd *= 0.98F;
        this.zd *= friction;
        if (this.onGround) {
            this.yd *= -0.5;
        }

        this.tickCount++;
        this.age++;
        if (this.age >= 6000) {
            this.remove();
        }
    }

    @Override
    public boolean isBigMovementEnabled() {
        return false;
    }
}
