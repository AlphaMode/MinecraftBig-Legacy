package me.alphamode.mcbig.mixin.features.big_movement;

import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.SoundType;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

@Mixin(Entity.class)
public abstract class EntityMixin implements BigEntityExtension {
    @Shadow public float bbWidth;
    @Shadow public float bbHeight;
    @Shadow public float heightOffset;
    @Shadow public float ySlideOffset;
    @Shadow public boolean noPhysics;
    @Shadow @Final public AABB bb;
    @Shadow public double x;
    @Shadow public double y;
    @Shadow public double z;
    @Shadow public boolean stuckInBlock;
    @Shadow public double xd;
    @Shadow public double yd;
    @Shadow public double zd;
    @Shadow public boolean onGround;

    @Shadow public abstract boolean isSneaking();

    @Shadow public Level level;
    @Shadow public boolean slide;
    @Shadow public float footSize;
    @Shadow public boolean horizontalCollision;
    @Shadow public boolean verticalCollision;
    @Shadow public boolean collision;

    @Shadow protected abstract void checkFallDamage(double yVelocity, boolean onGround);

    @Shadow protected abstract boolean isMovementNoisy();

    @Shadow public Entity riding;
    @Shadow public float walkDist;
    @Shadow private int nextStep;
    @Shadow public int onFire;
    @Shadow public int flameTime;

    @Shadow protected abstract void burn(int damage);

    @Shadow public abstract boolean isInWaterOrRain();

    @Shadow protected Random random;
    public BigDecimal xoBig = BigDecimal.ZERO;
    public BigDecimal zoBig = BigDecimal.ZERO;
    public BigDecimal xBig = BigDecimal.ZERO;
    public BigDecimal zBig = BigDecimal.ZERO;

    public BigDecimal xOldBig = BigDecimal.ZERO;
    public BigDecimal zOldBig = BigDecimal.ZERO;

    public final BigAABB bbBig = BigAABB.create(BigDecimal.ZERO, 0.0, BigDecimal.ZERO, BigDecimal.ZERO, 0.0, BigDecimal.ZERO);

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"))
    private void bigPosInit(Entity instance, double x, double y, double z) {
        ((BigEntityExtension) instance).setPos(BigDecimal.ZERO, y, BigDecimal.ZERO);
    }

    @Redirect(method = "resetPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"))
    private void bigPosReset(Entity instance, double x, double y, double z) {
        ((BigEntityExtension)instance).setPos(this.xBig, y, this.zBig);
    }

    @Override
    public void setPos(BigDecimal x, double y, BigDecimal z) {
        this.xBig = x;
        this.zBig = z;

        this.x = this.xBig.doubleValue();
        this.z = this.zBig.doubleValue();

        float w = this.bbWidth / 2.0F;
        float h = this.bbHeight;
        BigDecimal wBig = BigDecimal.valueOf(w);
        this.bbBig
                .set(
                        x.subtract(wBig),
                        y - (double)this.heightOffset + (double)this.ySlideOffset,
                        z.subtract(wBig),
                        x.add(wBig),
                        y - (double)this.heightOffset + (double)this.ySlideOffset + (double)h,
                        z.add(wBig)
                );

        this.bb
                .set(
                        x.doubleValue() - (double)w,
                        y - (double)this.heightOffset + (double)this.ySlideOffset,
                        z.doubleValue() - (double)w,
                        x.doubleValue() + (double)w,
                        y - (double)this.heightOffset + (double)this.ySlideOffset + (double)h,
                        z.doubleValue() + (double)w
                );
    }

    @Inject(method = "baseTick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;xo:D"))
    private void updateOldPos(CallbackInfo ci) {
        this.xoBig = this.xBig;
        this.zoBig = this.zBig;
    }

    @Override
    public void bigMove(double x, double y, double z) {
        if (this.noPhysics) {
            this.bb.grow(x, y, z);
            this.bbBig.grow(x, y, z);
            this.x = (this.bb.x0 + this.bb.x1) / 2.0;
            this.y = this.bb.y0 + (double)this.heightOffset - (double)this.ySlideOffset;
            this.z = (this.bb.z0 + this.bb.z1) / 2.0;

            this.xBig = (this.bbBig.x0.add(this.bbBig.x1)).divide(BigConstants.TWO);
            this.zBig = (this.bbBig.z0.add(this.bbBig.z1)).divide(BigConstants.TWO);
        } else {
            this.ySlideOffset *= 0.4F;
            double var7 = this.x;
            double var9 = this.z;
            if (this.stuckInBlock) {
                this.stuckInBlock = false;
                x *= 0.25;
                y *= 0.05F;
                z *= 0.25;
                this.xd = 0.0;
                this.yd = 0.0;
                this.zd = 0.0;
            }

            double var11 = x;
            double var13 = y;
            double var15 = z;
            AABB var17 = this.bb.copy();
            boolean var18 = this.onGround && this.isSneaking();
            if (var18) {
                double var19;
                for(var19 = 0.05; x != 0.0 && this.level.getCubes((Entity) (Object) this, this.bb.offset(x, -1.0, 0.0)).size() == 0; var11 = x) {
                    if (x < var19 && x >= -var19) {
                        x = 0.0;
                    } else if (x > 0.0) {
                        x -= var19;
                    } else {
                        x += var19;
                    }
                }

                for(; z != 0.0 && this.level.getCubes((Entity) (Object) this, this.bb.offset(0.0, -1.0, z)).size() == 0; var15 = z) {
                    if (z < var19 && z >= -var19) {
                        z = 0.0;
                    } else if (z > 0.0) {
                        z -= var19;
                    } else {
                        z += var19;
                    }
                }
            }

            List var36 = this.level.getCubes((Entity) (Object) this, this.bb.expand(x, y, z));

            for(int var20 = 0; var20 < var36.size(); ++var20) {
                y = ((AABB)var36.get(var20)).clipYCollide(this.bb, y);
            }

            this.bb.grow(0.0, y, 0.0);
            if (!this.slide && var13 != y) {
                z = 0.0;
                y = 0.0;
                x = 0.0;
            }

            boolean var38 = this.onGround || var13 != y && var13 < 0.0;

            for(int var21 = 0; var21 < var36.size(); ++var21) {
                x = ((AABB)var36.get(var21)).clipXCollide(this.bb, x);
            }

            this.bb.grow(x, 0.0, 0.0);
            this.bbBig.grow(x, 0.0, 0.0);
            if (!this.slide && var11 != x) {
                z = 0.0;
                y = 0.0;
                x = 0.0;
            }

            for(int var39 = 0; var39 < var36.size(); ++var39) {
                z = ((AABB)var36.get(var39)).clipZCollide(this.bb, z);
            }

            this.bb.grow(0.0, 0.0, z);
            this.bbBig.grow(0.0, 0.0, z);
            if (!this.slide && var15 != z) {
                z = 0.0;
                y = 0.0;
                x = 0.0;
            }

            if (this.footSize > 0.0F && var38 && (var18 || this.ySlideOffset < 0.05F) && (var11 != x || var15 != z)) {
                double var40 = x;
                double var23 = y;
                double var25 = z;
                x = var11;
                y = (double)this.footSize;
                z = var15;
                AABB var27 = this.bb.copy();
                this.bb.copyFrom(var17);
                var36 = this.level.getCubes((Entity) (Object) this, this.bb.expand(var11, y, var15));

                for(int var28 = 0; var28 < var36.size(); ++var28) {
                    y = ((AABB)var36.get(var28)).clipYCollide(this.bb, y);
                }

                this.bb.grow(0.0, y, 0.0);
                this.bbBig.grow(0.0, y, 0.0);
                if (!this.slide && var13 != y) {
                    z = 0.0;
                    y = 0.0;
                    x = 0.0;
                }

                for(int var48 = 0; var48 < var36.size(); ++var48) {
                    x = ((AABB)var36.get(var48)).clipXCollide(this.bb, x);
                }

                this.bb.grow(x, 0.0, 0.0);
                this.bbBig.grow(x, 0.0, 0.0);
                if (!this.slide && var11 != x) {
                    z = 0.0;
                    y = 0.0;
                    x = 0.0;
                }

                for(int var49 = 0; var49 < var36.size(); ++var49) {
                    z = ((AABB)var36.get(var49)).clipZCollide(this.bb, z);
                }

                this.bb.grow(0.0, 0.0, z);
                this.bbBig.grow(0.0, 0.0, z);
                if (!this.slide && var15 != z) {
                    z = 0.0;
                    y = 0.0;
                    x = 0.0;
                }

                if (!this.slide && var13 != y) {
                    z = 0.0;
                    y = 0.0;
                    x = 0.0;
                } else {
                    y = (double)(-this.footSize);

                    for(int var50 = 0; var50 < var36.size(); ++var50) {
                        y = ((AABB)var36.get(var50)).clipYCollide(this.bb, y);
                    }

                    this.bb.grow(0.0, y, 0.0);
                    this.bbBig.grow(0.0, y, 0.0);
                }

                if (var40 * var40 + var25 * var25 >= x * x + z * z) {
                    x = var40;
                    y = var23;
                    z = var25;
                    this.bb.copyFrom(var27);
                } else {
                    double var51 = this.bb.y0 - (double)((int)this.bb.y0);
                    if (var51 > 0.0) {
                        this.ySlideOffset = (float)((double)this.ySlideOffset + var51 + 0.01);
                    }
                }
            }

            this.x = (this.bb.x0 + this.bb.x1) / 2.0;
            this.y = this.bb.y0 + (double)this.heightOffset - (double)this.ySlideOffset;
            this.z = (this.bb.z0 + this.bb.z1) / 2.0;

            this.xBig = (this.bbBig.x0.add(this.bbBig.x1)).divide(BigConstants.TWO);
            this.zBig = (this.bbBig.z0.add(this.bbBig.z1)).divide(BigConstants.TWO);

            this.horizontalCollision = var11 != x || var15 != z;
            this.verticalCollision = var13 != y;
            this.onGround = var13 != y && var13 < 0.0;
            this.collision = this.horizontalCollision || this.verticalCollision;
            this.checkFallDamage(y, this.onGround);
            if (var11 != x) {
                this.xd = 0.0;
            }

            if (var13 != y) {
                this.yd = 0.0;
            }

            if (var15 != z) {
                this.zd = 0.0;
            }

            double var41 = this.x - var7;
            double var42 = this.z - var9;
            if (this.isMovementNoisy() && !var18 && this.riding == null) {
                this.walkDist = (float)((double)this.walkDist + (double) Mth.sqrt(var41 * var41 + var42 * var42) * 0.6);
                BigInteger xt = BigMath.floor(this.x);
                int yt = Mth.floor(this.y - 0.2F - (double)this.heightOffset);
                BigInteger zt = BigMath.floor(this.z);
                int tt = this.level.getTile(xt, yt, zt);
                if (this.level.getTile(xt, yt - 1, zt) == Tile.OAK_FENCE.id) {
                    tt = this.level.getTile(xt, yt - 1, zt);
                }

                if (this.walkDist > (float)this.nextStep && tt > 0) {
                    ++this.nextStep;
                    SoundType type = Tile.tiles[tt].soundType;
                    if (this.level.getTile(xt, yt + 1, zt) == Tile.SNOW_LAYER.id) {
                        type = Tile.SNOW_LAYER.soundType;
                        this.level.playSound((Entity) (Object) this, type.getStepSound(), type.getVolume() * 0.15F, type.getPitch());
                    } else if (!Tile.tiles[tt].material.isLiquid()) {
                        this.level.playSound((Entity) (Object) this, type.getStepSound(), type.getVolume() * 0.15F, type.getPitch());
                    }

                    Tile.tiles[tt].stepOn(this.level, xt, yt, zt, (Entity) (Object) this);
                }
            }

            BigInteger x0 = BigMath.floor(this.bb.x0 + 0.001);
            int y0 = Mth.floor(this.bb.y0 + 0.001);
            BigInteger z0 = BigMath.floor(this.bb.z0 + 0.001);
            BigInteger x1 = BigMath.floor(this.bb.x1 - 0.001);
            int y1 = Mth.floor(this.bb.y1 - 0.001);
            BigInteger z1 = BigMath.floor(this.bb.z1 - 0.001);
            if (this.level.hasChunksAt(x0, y0, z0, x1, y1, z1)) {
                for(BigInteger xt = x0; xt.compareTo(x1) <= 0; xt = xt.add(BigInteger.ONE)) {
                    for(int yt = y0; yt <= y1; ++yt) {
                        for(BigInteger zt = z0; zt.compareTo(z1) <= 0; zt = zt.add(BigInteger.ONE)) {
                            int var34 = this.level.getTile(xt, yt, zt);
                            if (var34 > 0) {
                                Tile.tiles[var34].entityInside(this.level, xt, yt, zt, (Entity) (Object) this);
                            }
                        }
                    }
                }
            }

            boolean var56 = this.isInWaterOrRain();
            if (this.level.containsFireTile(this.bb.deflate(0.001, 0.001, 0.001))) {
                this.burn(1);
                if (!var56) {
                    ++this.onFire;
                    if (this.onFire == 0) {
                        this.onFire = 300;
                    }
                }
            } else if (this.onFire <= 0) {
                this.onFire = -this.flameTime;
            }

            if (var56 && this.onFire > 0) {
                this.level.playSound((Entity) (Object) this, "random.fizz", 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                this.onFire = -this.flameTime;
            }
        }
    }

    @Override
    public BigDecimal getX() {
        return this.xBig;
    }

    @Override
    public BigDecimal getZ() {
        return this.zBig;
    }

    @Override
    public void setX(BigDecimal x) {
        this.xBig = x;
    }

    @Override
    public void setZ(BigDecimal z) {
        this.zBig = z;
    }

    @Override
    public BigDecimal getXOld() {
        return this.xOldBig;
    }

    @Override
    public BigDecimal getZOld() {
        return this.zOldBig;
    }

    @Override
    public void setXOld(BigDecimal x) {
        this.xOldBig = x;
    }

    @Override
    public void setZOld(BigDecimal z) {
        this.zOldBig = z;
    }
}
