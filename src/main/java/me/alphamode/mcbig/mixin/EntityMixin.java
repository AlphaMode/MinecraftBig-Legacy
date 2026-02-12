package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.BigEntityExtension;
import me.alphamode.mcbig.extensions.PlayerExtension;
import me.alphamode.mcbig.math.BigMath;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.LiquidTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

@Mixin(Entity.class)
public abstract class EntityMixin implements BigEntityExtension {

    public BigInteger xChunkBig = BigInteger.ZERO;
    public BigInteger zChunkBig = BigInteger.ZERO;

    @Shadow public float bbWidth;

    @Shadow public abstract float getHeadHeight();

    @Shadow public double x;

    @Shadow public double y;

    @Shadow public double z;

    @Shadow public Level level;

    @Shadow @Final public AABB bb;

    @Shadow public float defaultBrightness;

    @Shadow public float heightOffset;

    @Shadow public float yRot;

    @Shadow public double xd;

    @Shadow public double zd;

    @Shadow public boolean noPhysics;

    @Shadow public float ySlideOffset;

    @Shadow public boolean stuckInBlock;

    @Shadow public double yd;

    @Shadow public boolean onGround;

    @Shadow public abstract boolean isSneaking();

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

    @Shadow public abstract boolean isInWaterOrRain();

    @Shadow protected abstract void burn(int damage);

    @Shadow public int onFire;

    @Shadow public int flameTime;

    @Shadow protected Random random;

    @Override
    public void setXChunk(BigInteger x) {
        this.xChunkBig = x;
    }

    @Override
    public void setZChunk(BigInteger z) {
        this.zChunkBig = z;
    }

    @Override
    public BigInteger getXChunk() {
        return this.xChunkBig;
    }

    @Override
    public BigInteger getZChunk() {
        return this.zChunkBig;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean isInWall() {
        if (isBigMovementEnabled()) {
            for(int i = 0; i < 8; ++i) {
                me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension bigEntity = (me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension) this;
                float var2 = ((float)((i >> 0) % 2) - 0.5F) * this.bbWidth * 0.9F;
                float var3 = ((float)((i >> 1) % 2) - 0.5F) * 0.1F;
                float var4 = ((float)((i >> 2) % 2) - 0.5F) * this.bbWidth * 0.9F;
                BigInteger var5 = BigMath.floor(bigEntity.getX().add(new BigDecimal(var2)));
                int var6 = Mth.floor(this.y + (double)this.getHeadHeight() + (double)var3);
                BigInteger var7 = BigMath.floor(bigEntity.getZ().add(new BigDecimal(var4)));
                if (this.level.isSolidBlockingTile(var5, var6, var7)) {
                    return true;
                }
            }

            return false;
        }
        for(int i = 0; i < 8; ++i) {
            float var2 = ((float)((i >> 0) % 2) - 0.5F) * this.bbWidth * 0.9F;
            float var3 = ((float)((i >> 1) % 2) - 0.5F) * 0.1F;
            float var4 = ((float)((i >> 2) % 2) - 0.5F) * this.bbWidth * 0.9F;
            BigInteger var5 = BigMath.floor(this.x + (double)var2);
            int var6 = Mth.floor(this.y + (double)this.getHeadHeight() + (double)var3);
            BigInteger var7 = BigMath.floor(this.z + (double)var4);
            if (this.level.isSolidBlockingTile(var5, var6, var7)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean isUnderLiquid(Material material) {
        double var2 = this.y + (double)this.getHeadHeight();
        BigInteger xt = BigMath.floor(this.x);
        int yt = Mth.floor((float)Mth.floor(var2));
        BigInteger zt = BigMath.floor(this.z);
        int tile = this.level.getTile(xt, yt, zt);
        if (tile != 0 && Tile.tiles[tile].material == material) {
            float height = LiquidTile.getHeight(this.level.getData(xt, yt, zt)) - 0.11111111F;
            float headHeight = (float)(yt + 1) - height;
            return var2 < (double)headHeight;
        } else {
            return false;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public float getBrightness(float partialTick) {
        BigInteger xt = BigMath.floor(this.x);
        double headHeight = (this.bb.y1 - this.bb.y0) * 0.66;
        int yt = Mth.floor(this.y - (double)this.heightOffset + headHeight);
        BigInteger zt = BigMath.floor(this.z);
        if (this.level
                .hasChunksAt(BigMath.floor(this.bb.x0), Mth.floor(this.bb.y0), BigMath.floor(this.bb.z0), BigMath.floor(this.bb.x1), Mth.floor(this.bb.y1), BigMath.floor(this.bb.z1))) {
            float br = this.level.getBrightness(xt, yt, zt);
            if (br < this.defaultBrightness) {
                br = this.defaultBrightness;
            }

            return br;
        } else {
            return this.defaultBrightness;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void moveRelative(float front, float right, float speed) {
        if (this instanceof PlayerExtension plr) {
            speed = plr.getFlySpeed();
        }
        float var4 = Mth.sqrt(front * front + right * right);
        if (!(var4 < 0.01F)) {
            if (var4 < 1.0F) {
                var4 = 1.0F;
            }

            var4 = speed / var4;
            front *= var4;
            right *= var4;
            float sin = Mth.sin(this.yRot * (float) Math.PI / 180.0F);
            float cos = Mth.cos(this.yRot * (float) Math.PI / 180.0F);
            this.xd += (double)(front * cos - right * sin);
            this.zd += (double)(right * cos + front * sin);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void move(double x, double y, double z) {
        if (this.noPhysics) {
            this.bb.grow(x, y, z);
            this.x = (this.bb.x0 + this.bb.x1) / 2.0;
            this.y = this.bb.y0 + (double)this.heightOffset - (double)this.ySlideOffset;
            this.z = (this.bb.z0 + this.bb.z1) / 2.0;
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
            if (!this.slide && var11 != x) {
                z = 0.0;
                y = 0.0;
                x = 0.0;
            }

            for(int var39 = 0; var39 < var36.size(); ++var39) {
                z = ((AABB)var36.get(var39)).clipZCollide(this.bb, z);
            }

            this.bb.grow(0.0, 0.0, z);
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
                if (!this.slide && var13 != y) {
                    z = 0.0;
                    y = 0.0;
                    x = 0.0;
                }

                for(int var48 = 0; var48 < var36.size(); ++var48) {
                    x = ((AABB)var36.get(var48)).clipXCollide(this.bb, x);
                }

                this.bb.grow(x, 0.0, 0.0);
                if (!this.slide && var11 != x) {
                    z = 0.0;
                    y = 0.0;
                    x = 0.0;
                }

                for(int var49 = 0; var49 < var36.size(); ++var49) {
                    z = ((AABB)var36.get(var49)).clipZCollide(this.bb, z);
                }

                this.bb.grow(0.0, 0.0, z);
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
                this.walkDist = (float)((double)this.walkDist + (double)Mth.sqrt(var41 * var41 + var42 * var42) * 0.6);
                BigInteger var43 = BigMath.floor(this.x);
                int var26 = Mth.floor(this.y - 0.2F - (double)this.heightOffset);
                BigInteger var46 = BigMath.floor(this.z);
                int var52 = this.level.getTile(var43, var26, var46);
                if (this.level.getTile(var43, var26 - 1, var46) == Tile.OAK_FENCE.id) {
                    var52 = this.level.getTile(var43, var26 - 1, var46);
                }

                if (this.walkDist > (float)this.nextStep && var52 > 0) {
                    ++this.nextStep;
                    Tile.SoundType var29 = Tile.tiles[var52].soundType;
                    if (this.level.getTile(var43, var26 + 1, var46) == Tile.SNOW_LAYER.id) {
                        var29 = Tile.SNOW_LAYER.soundType;
                        this.level.playSound((Entity) (Object) this, var29.getStepSound(), var29.getVolume() * 0.15F, var29.getPitch());
                    } else if (!Tile.tiles[var52].material.isLiquid()) {
                        this.level.playSound((Entity) (Object) this, var29.getStepSound(), var29.getVolume() * 0.15F, var29.getPitch());
                    }

                    Tile.tiles[var52].stepOn(this.level, var43, var26, var46, (Entity) (Object) this);
                }
            }

            BigInteger var44 = BigMath.floor(this.bb.x0 + 0.001);
            int var45 = Mth.floor(this.bb.y0 + 0.001);
            BigInteger var47 = BigMath.floor(this.bb.z0 + 0.001);
            BigInteger var53 = BigMath.floor(this.bb.x1 - 0.001);
            int var55 = Mth.floor(this.bb.y1 - 0.001);
            BigInteger var30 = BigMath.floor(this.bb.z1 - 0.001);
            if (this.level.hasChunksAt(var44, var45, var47, var53, var55, var30)) {
                for(BigInteger var31 = var44; var31.compareTo(var53) <= 0; var31 = var31.add(BigInteger.ONE)) {
                    for(int var32 = var45; var32 <= var55; ++var32) {
                        for(BigInteger var33 = var47; var33.compareTo(var30) <= 0; var33 = var33.add(BigInteger.ONE)) {
                            int var34 = this.level.getTile(var31, var32, var33);
                            if (var34 > 0) {
                                Tile.tiles[var34].entityInside(this.level, var31, var32, var33, (Entity) (Object) this);
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
        double yOff = y - (double)yt;
        double zOff = z - (double)zt.doubleValue();
        if (this.level.isSolidBlockingTile(xt, yt, zt)) {
            boolean var16 = !this.level.isSolidBlockingTile(xt.subtract(BigInteger.ONE), yt, zt);
            boolean var17 = !this.level.isSolidBlockingTile(xt.add(BigInteger.ONE), yt, zt);
            boolean var18 = !this.level.isSolidBlockingTile(xt, yt - 1, zt);
            boolean var19 = !this.level.isSolidBlockingTile(xt, yt + 1, zt);
            boolean var20 = !this.level.isSolidBlockingTile(xt, yt, zt.subtract(BigInteger.ONE));
            boolean var21 = !this.level.isSolidBlockingTile(xt, yt, zt.add(BigInteger.ONE));
            byte facing = -1;
            double var23 = 9999.0;
            if (var16 && xOff < var23) {
                var23 = xOff;
                facing = 0;
            }

            if (var17 && 1.0 - xOff < var23) {
                var23 = 1.0 - xOff;
                facing = 1;
            }

            if (var18 && yOff < var23) {
                var23 = yOff;
                facing = 2;
            }

            if (var19 && 1.0 - yOff < var23) {
                var23 = 1.0 - yOff;
                facing = 3;
            }

            if (var20 && zOff < var23) {
                var23 = zOff;
                facing = 4;
            }

            if (var21 && 1.0 - zOff < var23) {
                var23 = 1.0 - zOff;
                facing = 5;
            }

            float var25 = this.random.nextFloat() * 0.2F + 0.1F;
            if (facing == 0) {
                this.xd = (double)(-var25);
            }

            if (facing == 1) {
                this.xd = (double)var25;
            }

            if (facing == 2) {
                this.yd = (double)(-var25);
            }

            if (facing == 3) {
                this.yd = (double)var25;
            }

            if (facing == 4) {
                this.zd = (double)(-var25);
            }

            if (facing == 5) {
                this.zd = (double)var25;
            }
        }

        return false;
    }
}
