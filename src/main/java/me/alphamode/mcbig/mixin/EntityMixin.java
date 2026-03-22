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
        if (this instanceof PlayerExtension plr && plr.isFlying()) {
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
    public void move(double xa, double ya, double za) {
        if (this.noPhysics) {
            this.bb.grow(xa, ya, za);
            this.x = (this.bb.x0 + this.bb.x1) / 2.0;
            this.y = this.bb.y0 + (double)this.heightOffset - (double)this.ySlideOffset;
            this.z = (this.bb.z0 + this.bb.z1) / 2.0;
        } else {
            this.ySlideOffset *= 0.4F;

            double xo = this.x;
            double zo = this.z;

            if (this.stuckInBlock) {
                this.stuckInBlock = false;
                xa *= 0.25;
                ya *= 0.05F;
                za *= 0.25;
                this.xd = 0.0;
                this.yd = 0.0;
                this.zd = 0.0;
            }

            double xaOrg = xa;
            double yaOrg = ya;
            double zaOrg = za;

            AABB bbOrg = this.bb.copy();

            boolean sneaking = this.onGround && this.isSneaking();

            if (sneaking) {
                double d = 0.05;
                while (xa != 0 && this.level.getCubes((Entity) (Object) this, this.bb.offset(xa, -1.0, 0.0)).size() == 0) {
                    if (xa < d && xa >= -d) xa = 0.0;
                    else if (xa > 0.0) xa -= d;
                    else xa += d;
                    xaOrg = xa;
                }

                while (za != 0 && this.level.getCubes((Entity) (Object) this, this.bb.offset(0.0, -1.0, za)).size() == 0) {
                    if (za < d && za >= -d) za = 0.0;
                    else if (za > 0.0) za -= d;
                    else za += d;
                    zaOrg = za;
                }
            }

            List<AABB> aABBs = this.level.getCubes((Entity) (Object) this, this.bb.expand(xa, ya, za));

            // LAND FIRST, then x and z
            for(int i = 0; i < aABBs.size(); ++i) {
                ya = aABBs.get(i).clipYCollide(this.bb, ya);
            }

            this.bb.grow(0.0, ya, 0.0);
            if (!this.slide && yaOrg != ya) {
                za = 0.0;
                ya = 0.0;
                xa = 0.0;
            }

            boolean og = this.onGround || yaOrg != ya && yaOrg < 0.0;

            for (int i = 0; i < aABBs.size(); ++i) {
                xa = aABBs.get(i).clipXCollide(this.bb, xa);
            }

            this.bb.grow(xa, 0.0, 0.0);

            if (!this.slide && xaOrg != xa) {
                za = 0.0;
                ya = 0.0;
                xa = 0.0;
            }

            for(int i = 0; i < aABBs.size(); ++i) {
                za = aABBs.get(i).clipZCollide(this.bb, za);
            }

            this.bb.grow(0.0, 0.0, za);
            if (!this.slide && zaOrg != za) {
                za = 0.0;
                ya = 0.0;
                xa = 0.0;
            }

            if (this.footSize > 0.0F && og && (sneaking || this.ySlideOffset < 0.05F) && (xaOrg != xa || zaOrg != za)) {
                double xaN = xa;
                double yaN = ya;
                double zaN = za;

                xa = xaOrg;
                ya = this.footSize;
                za = zaOrg;

                AABB normal = this.bb.copy();
                this.bb.copyFrom(bbOrg);
                aABBs = this.level.getCubes((Entity) (Object) this, this.bb.expand(xaOrg, ya, zaOrg));

                // LAND FIRST, then x and z
                for(int i = 0; i < aABBs.size(); ++i) {
                    ya = aABBs.get(i).clipYCollide(this.bb, ya);
                }

                this.bb.grow(0.0, ya, 0.0);
                if (!this.slide && yaOrg != ya) {
                    za = 0.0;
                    ya = 0.0;
                    xa = 0.0;
                }

                for(int i = 0; i < aABBs.size(); ++i) {
                    xa = aABBs.get(i).clipXCollide(this.bb, xa);
                }

                this.bb.grow(xa, 0.0, 0.0);
                if (!this.slide && xaOrg != xa) {
                    za = 0.0;
                    ya = 0.0;
                    xa = 0.0;
                }

                for(int i = 0; i < aABBs.size(); ++i) {
                    za = aABBs.get(i).clipZCollide(this.bb, za);
                }

                this.bb.grow(0.0, 0.0, za);
                if (!this.slide && zaOrg != za) {
                    za = 0.0;
                    ya = 0.0;
                    xa = 0.0;
                }

                if (!this.slide && yaOrg != ya) {
                    za = 0.0;
                    ya = 0.0;
                    xa = 0.0;
                } else {
                    ya = -this.footSize;

                    for(int i = 0; i < aABBs.size(); ++i) {
                        ya = aABBs.get(i).clipYCollide(this.bb, ya);
                    }

                    this.bb.grow(0.0, ya, 0.0);
                }

                if (xaN * xaN + zaN * zaN >= xa * xa + za * za) {
                    xa = xaN;
                    ya = yaN;
                    za = zaN;
                    this.bb.copyFrom(normal);
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
            this.horizontalCollision = xaOrg != xa || zaOrg != za;
            this.verticalCollision = yaOrg != ya;
            this.onGround = yaOrg != ya && yaOrg < 0.0;
            this.collision = this.horizontalCollision || this.verticalCollision;
            this.checkFallDamage(ya, this.onGround);
            if (xaOrg != xa) {
                this.xd = 0.0;
            }

            if (yaOrg != ya) {
                this.yd = 0.0;
            }

            if (zaOrg != za) {
                this.zd = 0.0;
            }

            double xm = this.x - xo;
            double zm = this.z - zo;

            if (this.isMovementNoisy() && !sneaking && this.riding == null) {
                this.walkDist = (float)((double)this.walkDist + (double)Mth.sqrt(xm * xm + zm * zm) * 0.6);
                BigInteger xt = BigMath.floor(this.x);
                int yt = Mth.floor(this.y - 0.2F - (double)this.heightOffset);
                BigInteger zt = BigMath.floor(this.z);

                int t = this.level.getTile(xt, yt, zt);
                if (this.level.getTile(xt, yt - 1, zt) == Tile.OAK_FENCE.id) {
                    t = this.level.getTile(xt, yt - 1, zt);
                }

                if (this.walkDist > (float)this.nextStep && t > 0) {
                    ++this.nextStep;
                    Tile.SoundType var29 = Tile.tiles[t].soundType;
                    if (this.level.getTile(xt, yt + 1, zt) == Tile.SNOW_LAYER.id) {
                        var29 = Tile.SNOW_LAYER.soundType;
                        this.level.playSound((Entity) (Object) this, var29.getStepSound(), var29.getVolume() * 0.15F, var29.getPitch());
                    } else if (!Tile.tiles[t].material.isLiquid()) {
                        this.level.playSound((Entity) (Object) this, var29.getStepSound(), var29.getVolume() * 0.15F, var29.getPitch());
                    }

                    Tile.tiles[t].stepOn(this.level, xt, yt, zt, (Entity) (Object) this);
                }
            }

            BigInteger x0 = BigMath.floor(this.bb.x0 + 0.001);
            int y0 = Mth.floor(this.bb.y0 + 0.001);
            BigInteger z0 = BigMath.floor(this.bb.z0 + 0.001);
            BigInteger x1 = BigMath.floor(this.bb.x1 - 0.001);
            int y1 = Mth.floor(this.bb.y1 - 0.001);
            BigInteger z1 = BigMath.floor(this.bb.z1 - 0.001);

            if (this.level.hasChunksAt(x0, y0, z0, x1, y1, z1)) {
                for(BigInteger x = x0; x.compareTo(x1) <= 0; x = x.add(BigInteger.ONE)) {
                    for(int y = y0; y <= y1; ++y) {
                        for(BigInteger z = z0; z.compareTo(z1) <= 0; z = z.add(BigInteger.ONE)) {
                            int t = this.level.getTile(x, y, z);
                            if (t > 0) {
                                Tile.tiles[t].entityInside(this.level, x, y, z, (Entity) (Object) this);
                            }
                        }
                    }
                }
            }

            boolean water = this.isInWaterOrRain();
            if (this.level.containsFireTile(this.bb.deflate(0.001, 0.001, 0.001))) {
                this.burn(1);
                if (!water) {
                    this.onFire++;
                    if (this.onFire == 0) {
                        this.onFire = 300;
                    }
                }
            } else if (this.onFire <= 0) {
                this.onFire = -this.flameTime;
            }

            if (water && this.onFire > 0) {
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
