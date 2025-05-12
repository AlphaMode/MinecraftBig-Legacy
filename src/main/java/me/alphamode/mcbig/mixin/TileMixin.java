package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.BigTileExtension;
import me.alphamode.mcbig.world.phys.BigHitResult;
import net.minecraft.util.Facing;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

@Mixin(Tile.class)
public abstract class TileMixin implements BigTileExtension {
    @Shadow
    public abstract AABB getAABB(Level level, int x, int y, int z);

    @Shadow
    public abstract HitResult clip(Level level, int x, int y, int z, Vec3 vec1, Vec3 vec2);

    @Shadow
    public double xx0;

    @Shadow
    public double yy0;

    @Shadow
    public double zz0;

    @Shadow
    public double xx1;

    @Shadow
    public double yy1;

    @Shadow
    public double zz1;

    @Shadow protected abstract boolean containsX(Vec3 vec);

    @Shadow protected abstract boolean containsY(Vec3 vec);

    @Shadow protected abstract boolean containsZ(Vec3 vec);

    @Shadow @Final public static int[] lightEmission;

    @Shadow @Final public int id;

    @Shadow public abstract int getTexture(int side, int data);

    @Shadow public abstract int getResourceCount(Random random);

    @Shadow public abstract int getResource(int meta, Random random);

    @Shadow protected abstract int getSpawnResourcesAuxValue(int meta);

    @Override
    public AABB getTileAABB(Level level, BigInteger x, int y, BigInteger z) {
        return AABB.newTemp(x.doubleValue() + this.xx0, (double) y + this.yy0, z.doubleValue() + this.zz0, x.doubleValue() + this.xx1, (double) y + this.yy1, z.doubleValue() + this.zz1);
    }

    @Override
    public void addAABBs(Level level, BigInteger x, int y, BigInteger z, AABB bb, List<AABB> boxes) {
        AABB aabb = getAABB(level, x, y, z);
        if (aabb != null && bb.intersects(aabb)) {
            boxes.add(aabb);
        }
    }

    @Override
    public AABB getAABB(Level level, BigInteger x, int y, BigInteger z) {
        return AABB.newTemp((double)x.doubleValue() + this.xx0, (double)y + this.yy0, (double)z.doubleValue() + this.zz0, (double)x.doubleValue() + this.xx1, (double)y + this.yy1, (double)z.doubleValue() + this.zz1);
    }

    @Override
    public float getBrightness(LevelSource level, BigInteger x, int y, BigInteger z) {
        return level.getBrightness(x, y, z, this.lightEmission[this.id]);
    }

    @Override
    public boolean shouldRenderFace(LevelSource level, BigInteger x, int y, BigInteger z, int face) {
        if (face == Facing.DOWN && this.yy0 > 0.0) {
            return true;
        } else if (face == Facing.UP && this.yy1 < 1.0) {
            return true;
        } else if (face == Facing.NORTH && this.zz0 > 0.0) {
            return true;
        } else if (face == Facing.SOUTH && this.zz1 < 1.0) {
            return true;
        } else if (face == Facing.WEST && this.xx0 > 0.0) {
            return true;
        } else if (face == Facing.EAST && this.xx1 < 1.0) {
            return true;
        } else {
            return !level.isSolidTile(x, y, z);
        }
    }

    @Override
    public int getTexture(LevelSource level, BigInteger x, int y, BigInteger z, int side) {
        return getTexture(side, level.getData(x, y, z));
    }

    @Override
    public final void dropResources(Level level, BigInteger x, int y, BigInteger z, int meta) {
        this.dropResources(level, x, y, z, meta, 1.0F);
    }

    @Override
    public void dropResources(Level level, BigInteger x, int y, BigInteger z, int meta, float f) {
        if (!level.isClientSide) {
            int var7 = this.getResourceCount(level.random);

            for(int var8 = 0; var8 < var7; ++var8) {
                if (!(level.random.nextFloat() > f)) {
                    int var9 = this.getResource(meta, level.random);
                    if (var9 > 0) {
                        this.popResource(level, x, y, z, new ItemInstance(var9, 1, this.getSpawnResourcesAuxValue(meta)));
                    }
                }
            }
        }
    }

    @Override
    public void popResource(Level level, BigInteger x, int y, BigInteger z, ItemInstance item) {
        if (!level.isClientSide) {
            float var6 = 0.7F;
            double var7 = (double)(level.random.nextFloat() * var6) + (double)(1.0F - var6) * 0.5;
            double var9 = (double)(level.random.nextFloat() * var6) + (double)(1.0F - var6) * 0.5;
            double var11 = (double)(level.random.nextFloat() * var6) + (double)(1.0F - var6) * 0.5;
            ItemEntity var13 = new ItemEntity(level, (double)x.doubleValue() + var7, (double)y + var9, (double)z.doubleValue() + var11, item);
            var13.throwTime = 10;
            level.addEntity(var13);
        }
    }

    @Override
    public HitResult clip(Level level, BigInteger x, int y, BigInteger z, Vec3 vec1, Vec3 vec2) {
        updateShape(level, x, y, z);
        vec1 = vec1.add((double)(x.negate()).doubleValue(), (double)(-y), (double)(z.negate()).doubleValue());
        vec2 = vec2.add((double)(x.negate()).doubleValue(), (double)(-y), (double)(z.negate()).doubleValue());
        Vec3 var7 = vec1.clipX(vec2, this.xx0);
        Vec3 var8 = vec1.clipX(vec2, this.xx1);
        Vec3 var9 = vec1.clipY(vec2, this.yy0);
        Vec3 var10 = vec1.clipY(vec2, this.yy1);
        Vec3 var11 = vec1.clipZ(vec2, this.zz0);
        Vec3 var12 = vec1.clipZ(vec2, this.zz1);
        if (!this.containsX(var7)) {
            var7 = null;
        }

        if (!containsX(var8)) {
            var8 = null;
        }

        if (!containsY(var9)) {
            var9 = null;
        }

        if (!containsY(var10)) {
            var10 = null;
        }

        if (!containsZ(var11)) {
            var11 = null;
        }

        if (!containsZ(var12)) {
            var12 = null;
        }

        Vec3 var13 = null;
        if (var7 != null && (var13 == null || vec1.distanceTo(var7) < vec1.distanceTo(var13))) {
            var13 = var7;
        }

        if (var8 != null && (var13 == null || vec1.distanceTo(var8) < vec1.distanceTo(var13))) {
            var13 = var8;
        }

        if (var9 != null && (var13 == null || vec1.distanceTo(var9) < vec1.distanceTo(var13))) {
            var13 = var9;
        }

        if (var10 != null && (var13 == null || vec1.distanceTo(var10) < vec1.distanceTo(var13))) {
            var13 = var10;
        }

        if (var11 != null && (var13 == null || vec1.distanceTo(var11) < vec1.distanceTo(var13))) {
            var13 = var11;
        }

        if (var12 != null && (var13 == null || vec1.distanceTo(var12) < vec1.distanceTo(var13))) {
            var13 = var12;
        }

        if (var13 == null) {
            return null;
        } else {
            byte face = -1;
            if (var13 == var7) {
                face = Facing.WEST;
            }

            if (var13 == var8) {
                face = Facing.EAST;
            }

            if (var13 == var9) {
                face = Facing.DOWN;
            }

            if (var13 == var10) {
                face = Facing.UP;
            }

            if (var13 == var11) {
                face = Facing.NORTH;
            }

            if (var13 == var12) {
                face = Facing.SOUTH;
            }

            return new BigHitResult(x, y, z, face, var13.add((double)x.doubleValue(), (double)y, (double)z.doubleValue()));
        }
    }
}
