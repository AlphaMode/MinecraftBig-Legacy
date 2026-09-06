package me.alphamode.mcbig.mixin.features.big_movement;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.DoubleTag;
import com.mojang.nbt.FloatTag;
import com.mojang.nbt.ListTag;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.phys.BigAABB;
import me.alphamode.mcbig.world.phys.DelegatingBigAABB;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
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
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@Mixin(Entity.class)
public abstract class EntityMixin implements BigEntityExtension, me.alphamode.mcbig.extensions.BigEntityExtension {
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
    @Shadow
    public double xo;
    @Shadow
    public double zo;
    @Shadow
    public double xOld;
    @Shadow
    public double zOld;
    @Shadow
    public double yo;
    @Shadow
    public float yRotO;
    @Shadow
    public float xRotO;
    @Shadow
    public float yRot;
    @Shadow
    public float xRot;

    @Shadow
    protected abstract void setRot(float yRot, float xRot);

    @Shadow
    public abstract void move(double x, double y, double z);

    @Shadow
    public abstract void setPos(double x, double y, double z);

    @Shadow
    public double yOld;
    @Shadow
    protected float fallDistance;
    @Shadow
    public int airSupply;

    @Shadow
    protected abstract void readAdditionalSaveData(CompoundTag tag);

    private static final int ENTITY_SCALE = 12;

    public BigDecimal xoBig = BigDecimal.ZERO;
    public BigDecimal zoBig = BigDecimal.ZERO;
    public BigDecimal xBig = BigDecimal.ZERO;
    public BigDecimal zBig = BigDecimal.ZERO;

    public BigDecimal xOldBig = BigDecimal.ZERO;
    public BigDecimal zOldBig = BigDecimal.ZERO;

    public final DelegatingBigAABB bbBig = new DelegatingBigAABB(BigDecimal.ZERO, 0.0, BigDecimal.ZERO, BigDecimal.ZERO, 0.0, BigDecimal.ZERO);

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"))
    private void bigPosInit(Entity instance, double x, double y, double z) {
        if (isBigMovementEnabled()) {
            this.bbBig.setDelegate(this.bb);
            ((BigEntityExtension) instance).setPos(BigDecimal.ZERO, y, BigDecimal.ZERO);
        }
    }

    @Environment(EnvType.CLIENT)
    @Redirect(method = "resetPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"))
    private void bigPosReset(Entity instance, double x, double y, double z) {
        ((BigEntityExtension)instance).setPos(this.xBig, y, this.zBig);
    }

    @Override
    public BigAABB getBigBB() {
        return this.bbBig;
    }

    @Inject(method = "setPos", at = @At("HEAD"))
    private void setBigPos(double x, double y, double z, CallbackInfo ci) {
        if (isBigMovementEnabled()) {
            this.xBig = BigDecimal.valueOf(x);
            this.zBig = BigDecimal.valueOf(z);
        }
    }

    @Override
    public void setPos(BigDecimal x, double y, BigDecimal z) {
        setX(x);
        this.y = y;
        setZ(z);

        float w = this.bbWidth / 2.0F;
        float h = this.bbHeight;
        BigDecimal wBig = BigDecimal.valueOf(w);
        this.bbBig
                .set(
                        x.subtract(wBig, BigMath.CONTEXT),
                        y - (double)this.heightOffset + (double)this.ySlideOffset,
                        z.subtract(wBig, BigMath.CONTEXT),
                        x.add(wBig),
                        y - (double)this.heightOffset + (double)this.ySlideOffset + (double)h,
                        z.add(wBig, BigMath.CONTEXT)
                );
    }

    @Inject(method = "baseTick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;xo:D"))
    private void updateOldPos(CallbackInfo ci) {
        if (isBigMovementEnabled()) {
            this.xoBig = getX();
            this.zoBig = getZ();
        }
    }

    @Override
    public void absMoveTo(BigDecimal x, double y, BigDecimal z, float yRot, float xRot) {
        this.xoBig = this.xBig = x;
        this.yo = this.y = y;
        this.zoBig = this.zBig = z;
        this.yRotO = this.yRot = yRot;
        this.xRotO = this.xRot = xRot;
        this.ySlideOffset = 0.0F;
        double var9 = this.yRotO - yRot;
        if (var9 < -180.0) {
            this.yRotO += 360.0F;
        }

        if (var9 >= 180.0) {
            this.yRotO -= 360.0F;
        }

        this.setPos(this.xBig, this.y, this.zBig);
        this.setRot(yRot, xRot);
    }

    @Override
    public void moveTo(BigDecimal x, double y, BigDecimal z, float yRot, float xRot) {
        this.xOldBig = this.xoBig = this.xBig = x;
        this.yOld = this.yo = this.y = y + this.heightOffset;
        this.zOldBig = this.zoBig = this.zBig = z;
        this.yRot = yRot;
        this.xRot = xRot;
        this.setPos(this.x, this.y, this.z);
    }

    @Override
    public void bigMove(double xa, double ya, double za) {
        if (this.noPhysics) {
            this.bbBig.grow(xa, ya, za);
            this.setX(this.bbBig.x0().add(this.bbBig.x1()).divide(BigDecimal.TWO, RoundingMode.HALF_EVEN));
            this.y = this.bbBig.y0() + this.heightOffset - this.ySlideOffset;
            this.setZ(this.bbBig.z0().add(this.bbBig.z1()).divide(BigDecimal.TWO, RoundingMode.HALF_EVEN));
        } else {
            this.ySlideOffset *= 0.4F;
            BigDecimal xo = this.getX();
            BigDecimal zo = this.getZ();

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
            BigAABB bbOrg = this.bbBig.copy();
            boolean sneaking = this.onGround && this.isSneaking();
            if (sneaking) {
                double d = 0.05;
                while (xa != 0.0 && this.level.getCubes((Entity) (Object) this, this.bbBig.offset(xa, -1.0, 0.0)).size() == 0) {
                    if (xa < d && xa >= -d) xa = 0.0;
                    else if (xa > 0.0) xa -= d;
                    else xa += d;
                    xaOrg = xa;
                }

                while (za != 0.0 && this.level.getCubes((Entity) (Object) this, this.bbBig.offset(0.0, -1.0, za)).size() == 0) {
                    if (za < d && za >= -d) za = 0.0;
                    else if (za > 0.0) za -= d;
                    else za += d;
                    zaOrg = za;
                }
            }

            List<BigAABB> cubes = this.level.getCubes((Entity) (Object) this, this.bbBig.expand(xa, ya, za));

            // LAND FIRST, then x and z
            for (int i = 0; i < cubes.size(); i++) {
                ya = cubes.get(i).clipYCollide(this.bbBig, ya);
            }

            this.bbBig.grow(0.0, ya, 0.0);
            if (!this.slide && yaOrg != ya) {
                za = 0.0;
                ya = 0.0;
                xa = 0.0;
            }

            boolean og = this.onGround || yaOrg != ya && yaOrg < 0.0;

            for (int i = 0; i < cubes.size(); i++) {
                xa = cubes.get(i).clipXCollide(this.bbBig, xa);
            }

            this.bbBig.grow(xa, 0.0, 0.0);
            if (!this.slide && xaOrg != xa) {
                za = 0.0;
                ya = 0.0;
                xa = 0.0;
            }

            for (int i = 0; i < cubes.size(); i++) {
                za = cubes.get(i).clipZCollide(this.bbBig, za);
            }

            this.bbBig.grow(0.0, 0.0, za);
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
                BigAABB var27 = this.bbBig.copy();
                this.bbBig.copyFrom(bbOrg);
                cubes = this.level.getCubes((Entity) (Object) this, this.bbBig.expand(xaOrg, ya, zaOrg));

                // LAND FIRST, then x and z
                for (int i = 0; i < cubes.size(); i++) {
                    ya = cubes.get(i).clipYCollide(this.bbBig, ya);
                }

                this.bbBig.grow(0.0, ya, 0.0);
                if (!this.slide && yaOrg != ya) {
                    za = 0.0;
                    ya = 0.0;
                    xa = 0.0;
                }

                for (int i = 0; i < cubes.size(); i++) {
                    xa = cubes.get(i).clipXCollide(this.bbBig, xa);
                }

                this.bbBig.grow(xa, 0.0, 0.0);
                if (!this.slide && xaOrg != xa) {
                    za = 0.0;
                    ya = 0.0;
                    xa = 0.0;
                }

                for (int i = 0; i < cubes.size(); i++) {
                    za = cubes.get(i).clipZCollide(this.bbBig, za);
                }

                this.bbBig.grow(0.0, 0.0, za);
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

                    for (int i = 0; i < cubes.size(); i++) {
                        ya = cubes.get(i).clipYCollide(this.bbBig, ya);
                    }

                    this.bbBig.grow(0.0, ya, 0.0);
                }

                if (xaN * xaN + zaN * zaN >= xa * xa + za * za) {
                    xa = xaN;
                    ya = yaN;
                    za = zaN;
                    this.bbBig.copyFrom(var27);
                } else {
                    double var51 = this.bbBig.y0() - (int)this.bbBig.y0();
                    if (var51 > 0.0) {
                        this.ySlideOffset = (float)(this.ySlideOffset + (var51 + 0.01));
                    }
                }
            }

            this.setX(this.bbBig.x0().add(this.bbBig.x1(), BigMath.CONTEXT).divide(BigDecimal.TWO, RoundingMode.HALF_EVEN));
            this.y = this.bbBig.y0() + this.heightOffset - this.ySlideOffset;
            this.setZ(this.bbBig.z0().add(this.bbBig.z1()).divide(BigDecimal.TWO, RoundingMode.HALF_EVEN));

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

            double xm = this.getX().subtract(xo).doubleValue();
            double zm = this.getZ().subtract(zo).doubleValue();
            if (this.isMovementNoisy() && !sneaking && this.riding == null) {
                this.walkDist = (float)(this.walkDist + Mth.sqrt(xm * xm + zm * zm) * 0.6);
                BigInteger xt = BigMath.floor(this.getX());
                int yt = Mth.floor(this.y - 0.2F - this.heightOffset);
                BigInteger zt = BigMath.floor(this.getZ());
                int t = this.level.getTile(xt, yt, zt);
                if (this.level.getTile(xt, yt - 1, zt) == Tile.fence.id) {
                    t = this.level.getTile(xt, yt - 1, zt);
                }

                if (this.walkDist > this.nextStep && t > 0) {
                    this.nextStep++;
                    Tile.SoundType soundType = Tile.tiles[t].soundType;
                    if (this.level.getTile(xt, yt + 1, zt) == Tile.topSnow.id) {
                        soundType = Tile.topSnow.soundType;
                        this.level.playSound((Entity) (Object) this, soundType.getStepSound(), soundType.getVolume() * 0.15F, soundType.getPitch());
                    } else if (!Tile.tiles[t].material.isLiquid()) {
                        this.level.playSound((Entity) (Object) this, soundType.getStepSound(), soundType.getVolume() * 0.15F, soundType.getPitch());
                    }

                    Tile.tiles[t].stepOn(this.level, xt, yt, zt, (Entity) (Object) this);
                }
            }

            BigDecimal elision = BigDecimal.valueOf(0.001);
            BigInteger x0 = BigMath.floor(this.bbBig.x0().add(elision));
            int y0 = Mth.floor(this.bbBig.y0() + 0.001);
            BigInteger z0 = BigMath.floor(this.bbBig.z0().add(elision));
            BigInteger x1 = BigMath.floor(this.bbBig.x1().subtract(elision));
            int y1 = Mth.floor(this.bbBig.y1() - 0.001);
            BigInteger z1 = BigMath.floor(this.bbBig.z1().subtract(elision));
            if (this.level.hasChunksAt(x0, y0, z0, x1, y1, z1)) {
                for (BigInteger x = x0; x.compareTo(x1) <= 0; x = x.add(BigInteger.ONE)) {
                    for (int y = y0; y <= y1; y++) {
                        for (BigInteger z = z0; z.compareTo(z1) <= 0; z = z.add(BigInteger.ONE)) {
                            int t = this.level.getTile(x, y, z);
                            if (t > 0) {
                                Tile.tiles[t].entityInside(this.level, x, y, z, (Entity) (Object) this);
                            }
                        }
                    }
                }
            }

            boolean isWet = this.isInWaterOrRain();
            if (this.level.containsFireTile(this.bb.deflate(0.001, 0.001, 0.001))) {
                this.burn(1);
                if (!isWet) {
                    this.onFire++;
                    if (this.onFire == 0) {
                        this.onFire = 300;
                    }
                }
            } else if (this.onFire <= 0) {
                this.onFire = -this.flameTime;
            }

            if (isWet && this.onFire > 0) {
                this.level.playSound((Entity) (Object) this, "random.fizz", 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                this.onFire = -this.flameTime;
            }
        }
    }

    @Override
    public BigDecimal getX() {
        assert isBigMovementEnabled();
        return this.xBig;//BigDecimal.valueOf(this.x);
    }

    @Override
    public BigDecimal getZ() {
        assert isBigMovementEnabled();
        return this.zBig;//BigDecimal.valueOf(this.z);
    }

    @Override
    public void setX(BigDecimal x) {
        assert isBigMovementEnabled();
        this.xBig = x.setScale(ENTITY_SCALE, RoundingMode.HALF_EVEN);
        this.x = x.doubleValue();
    }

    @Override
    public void setZ(BigDecimal z) {
        assert isBigMovementEnabled();
        this.zBig = z.setScale(ENTITY_SCALE, RoundingMode.HALF_EVEN);
        this.z = z.doubleValue();
    }

    @Override
    public BigDecimal getXO() {
        assert isBigMovementEnabled();
        return this.xoBig;//BigDecimal.valueOf(this.xo);
    }

    @Override
    public BigDecimal getZO() {
        assert isBigMovementEnabled();
        return this.zoBig;//BigDecimal.valueOf(this.zo);
    }

    @Override
    public void setXO(BigDecimal x) {
        assert isBigMovementEnabled();
        this.xoBig = x.setScale(ENTITY_SCALE, RoundingMode.HALF_EVEN);
        this.xo = x.doubleValue();
    }

    @Override
    public void setZO(BigDecimal z) {
        assert isBigMovementEnabled();
        this.zoBig = z.setScale(ENTITY_SCALE, RoundingMode.HALF_EVEN);
        this.zo = z.doubleValue();
    }

    @Override
    public BigDecimal getXOld() {
        assert isBigMovementEnabled();
        return this.xOldBig;//BigDecimal.valueOf(this.xOld);
    }

    @Override
    public BigDecimal getZOld() {
        assert isBigMovementEnabled();
        return this.zOldBig;//BigDecimal.valueOf(this.zOld);
    }

    @Override
    public void setXOld(BigDecimal x) {
        assert isBigMovementEnabled();
        this.xOldBig = x.setScale(ENTITY_SCALE, RoundingMode.HALF_EVEN);
        this.xOld = x.doubleValue();
    }

    @Override
    public void setZOld(BigDecimal z) {
        assert isBigMovementEnabled();
        this.zOldBig = z.setScale(ENTITY_SCALE, RoundingMode.HALF_EVEN);
        this.zOld = z.doubleValue();
    }

    @Inject(method = "moveTo", at = @At("HEAD"))
    private void bigMoveTo(double x, double y, double z, float yRot, float xRot, CallbackInfo ci) {
        if (isBigMovementEnabled()) {
            setXOld(new BigDecimal(x));
            setZOld(new BigDecimal(z));
        }
    }

    @Override
    public double distanceToSqr(BigDecimal x, double y, BigDecimal z) {
        double xd = this.getX().subtract(x).doubleValue();
        double yd = this.y - y;
        double zd = this.getZ().subtract(z).doubleValue();
        return xd * xd + yd * yd + zd * zd;
    }

    @Override
    public boolean checkInBlock(BigDecimal x, double y, BigDecimal z) {
        BigInteger xTile = BigMath.floor(x);
        int yTile = Mth.floor(y);
        BigInteger zTile = BigMath.floor(z);
        double xd = x.subtract(new BigDecimal(xTile)).doubleValue();
        double yd = y - yTile;
        double zd = z.subtract(new BigDecimal(zTile)).doubleValue();
        if (this.level.isSolidBlockingTile(xTile, yTile, zTile)) {
            boolean west = !this.level.isSolidBlockingTile(xTile.subtract(BigInteger.ONE), yTile, zTile);
            boolean east = !this.level.isSolidBlockingTile(xTile.add(BigInteger.ONE), yTile, zTile);
            boolean down = !this.level.isSolidBlockingTile(xTile, yTile - 1, zTile);
            boolean up = !this.level.isSolidBlockingTile(xTile, yTile + 1, zTile);
            boolean north = !this.level.isSolidBlockingTile(xTile, yTile, zTile.subtract(BigInteger.ONE));
            boolean south = !this.level.isSolidBlockingTile(xTile, yTile, zTile.add(BigInteger.ONE));
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

            if (down && yd < closest) {
                closest = yd;
                dir = 2;
            }

            if (up && 1.0 - yd < closest) {
                closest = 1.0 - yd;
                dir = 3;
            }

            if (north && zd < closest) {
                closest = zd;
                dir = 4;
            }

            if (south && 1.0 - zd < closest) {
                closest = 1.0 - zd;
                dir = 5;
            }

            float speed = this.random.nextFloat() * 0.2F + 0.1F;
            if (dir == 0) this.xd = -speed;
            if (dir == 1) this.xd = speed;

            if (dir == 2) this.yd = -speed;
            if (dir == 3) this.yd = speed;

            if (dir == 4) this.zd = -speed;
            if (dir == 5) this.zd = speed;
        }

        return false;
    }

    @Inject(method = "saveWithoutId", at = @At("HEAD"))
    private void saveBigPos(CompoundTag tag, CallbackInfo ci) {
        if (isBigMovementEnabled()) {
            CompoundTag posTag = new CompoundTag();
            posTag.putString("x", getX().toString());
            posTag.putString("z", getZ().toString());
            tag.putTag("PosBig", posTag);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void load(CompoundTag nbt) {
        CompoundTag posBig = nbt.getCompoundTag("PosBig");
        ListTag pos = nbt.getList("Pos");
        ListTag motion = nbt.getList("Motion");
        ListTag rotation = nbt.getList("Rotation");
        this.xd = ((DoubleTag)motion.get(0)).data;
        this.yd = ((DoubleTag)motion.get(1)).data;
        this.zd = ((DoubleTag)motion.get(2)).data;
        if (Math.abs(this.xd) > 10.0) {
            this.xd = 0.0;
        }

        if (Math.abs(this.yd) > 10.0) {
            this.yd = 0.0;
        }

        if (Math.abs(this.zd) > 10.0) {
            this.zd = 0.0;
        }

        this.yo = this.yOld = this.y = ((DoubleTag)pos.get(1)).data;
        if (isBigMovementEnabled()) {
            this.xoBig = this.xOldBig = this.xBig = new BigDecimal(posBig.getString("x"));
            this.zoBig = this.zOldBig = this.zBig = new BigDecimal(posBig.getString("z"));
            this.xo = this.xOld = this.x = this.xBig.doubleValue();
            this.zo = this.zOld = this.z = this.zBig.doubleValue();
        } else {
            this.xo = this.xOld = this.x = ((DoubleTag)pos.get(0)).data;
            this.zo = this.zOld = this.z = ((DoubleTag)pos.get(2)).data;
        }
        this.yRotO = this.yRot = ((FloatTag)rotation.get(0)).data;
        this.xRotO = this.xRot = ((FloatTag)rotation.get(1)).data;
        this.fallDistance = nbt.getFloat("FallDistance");
        this.onFire = nbt.getShort("Fire");
        this.airSupply = nbt.getShort("Air");
        this.onGround = nbt.getBoolean("OnGround");
        if (isBigMovementEnabled())
            this.setPos(this.xBig, this.y, this.zBig);
        else
            this.setPos(this.x, this.y, this.z);
        this.setRot(this.yRot, this.xRot);
        this.readAdditionalSaveData(nbt);
    }
}
