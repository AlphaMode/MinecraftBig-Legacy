package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.StairsTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

@Mixin(StairsTile.class)
public abstract class StairsTileMixin extends Tile implements BigTileExtension {
    @Shadow
    private Tile base;

    protected StairsTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public void updateShape(LevelSource source, BigInteger x, int y, BigInteger z) {
        this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public AABB getAABB(Level level, BigInteger x, int y, BigInteger z) {
        return super.getAABB(level, x, y, z);
    }

    @Override
    public BigAABB getBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        return super.getBigAABB(level, x, y, z);
    }

    @Override
    public boolean shouldRenderFace(LevelSource level, BigInteger x, int y, BigInteger z, int face) {
        return super.shouldRenderFace(level, x, y, z, face);
    }

    @Override
    public void addAABBs(Level level, BigInteger x, int y, BigInteger z, AABB bb, List<AABB> boxes) {
        int data = level.getData(x, y, z);
        if (data == 0) {
            this.setShape(0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 1.0F);
            super.addAABBs(level, x, y, z, bb, boxes);
            this.setShape(0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            super.addAABBs(level, x, y, z, bb, boxes);
        } else if (data == 1) {
            this.setShape(0.0F, 0.0F, 0.0F, 0.5F, 1.0F, 1.0F);
            super.addAABBs(level, x, y, z, bb, boxes);
            this.setShape(0.5F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
            super.addAABBs(level, x, y, z, bb, boxes);
        } else if (data == 2) {
            this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 0.5F);
            super.addAABBs(level, x, y, z, bb, boxes);
            this.setShape(0.0F, 0.0F, 0.5F, 1.0F, 1.0F, 1.0F);
            super.addAABBs(level, x, y, z, bb, boxes);
        } else if (data == 3) {
            this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.5F);
            super.addAABBs(level, x, y, z, bb, boxes);
            this.setShape(0.0F, 0.0F, 0.5F, 1.0F, 0.5F, 1.0F);
            super.addAABBs(level, x, y, z, bb, boxes);
        }

        this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void addBigAABBs(Level level, BigInteger x, int y, BigInteger z, BigAABB bb, List<BigAABB> boxes) {
        int data = level.getData(x, y, z);
        if (data == 0) {
            this.setShape(0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 1.0F);
            super.addBigAABBs(level, x, y, z, bb, boxes);
            this.setShape(0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            super.addBigAABBs(level, x, y, z, bb, boxes);
        } else if (data == 1) {
            this.setShape(0.0F, 0.0F, 0.0F, 0.5F, 1.0F, 1.0F);
            super.addBigAABBs(level, x, y, z, bb, boxes);
            this.setShape(0.5F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
            super.addBigAABBs(level, x, y, z, bb, boxes);
        } else if (data == 2) {
            this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 0.5F);
            super.addBigAABBs(level, x, y, z, bb, boxes);
            this.setShape(0.0F, 0.0F, 0.5F, 1.0F, 1.0F, 1.0F);
            super.addBigAABBs(level, x, y, z, bb, boxes);
        } else if (data == 3) {
            this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.5F);
            super.addBigAABBs(level, x, y, z, bb, boxes);
            this.setShape(0.0F, 0.0F, 0.5F, 1.0F, 0.5F, 1.0F);
            super.addBigAABBs(level, x, y, z, bb, boxes);
        }

        this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void animateTick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        this.base.animateTick(level, x, y, z, random);
    }

    @Override
    public void attack(Level level, BigInteger x, int y, BigInteger z, Player player) {
        this.base.attack(level, x, y, z, player);
    }

    @Override
    public void destroy(Level level, BigInteger x, int y, BigInteger z, int meta) {
        this.base.destroy(level, x, y, z, meta);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float getBrightness(LevelSource level, BigInteger x, int y, BigInteger z) {
        return this.base.getBrightness(level, x, y, z);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public int getTexture(LevelSource level, BigInteger x, int y, BigInteger z, int side) {
        return this.base.getTexture(level, x, y, z, side);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public AABB getTileAABB(Level level, BigInteger x, int y, BigInteger z) {
        return this.base.getTileAABB(level, x, y, z);
    }

    @Override
    public void handleEntityInside(Level level, BigInteger x, int y, BigInteger z, Entity entity, Vec3 delta) {
        this.base.handleEntityInside(level, x, y, z, entity, delta);
    }

    @Override
    public boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        return this.base.mayPlace(level, x, y, z);
    }

    @Override
    public void onPlace(Level level, BigInteger x, int y, BigInteger z) {
        this.neighborChanged(level, x, y, z, 0);
        this.base.onPlace(level, x, y, z);
    }

    @Override
    public void onRemove(Level level, BigInteger x, int y, BigInteger z) {
        this.base.onRemove(level, x, y, z);
    }

    @Override
    public void dropResources(Level level, BigInteger x, int y, BigInteger z, int meta, float dropChance) {
        this.base.dropResources(level, x, y, z, meta, dropChance);
    }

    @Override
    public void stepOn(Level level, BigInteger x, int y, BigInteger z, Entity entity) {
        this.base.stepOn(level, x, y, z, entity);
    }

    @Override
    public void tick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        this.base.tick(level, x, y, z, random);
    }

    @Override
    public boolean use(Level level, BigInteger x, int y, BigInteger z, Player player) {
        return this.base.use(level, x, y, z, player);
    }

    @Override
    public void wasExploded(Level level, BigInteger x, int y, BigInteger z) {
        this.base.wasExploded(level, x, y, z);
    }

    @Override
    public void setPlacedBy(Level level, BigInteger x, int y, BigInteger z, Mob entity) {
        int var6 = Mth.floor(entity.yRot * 4.0F / 360.0F + 0.5) & 3;
        if (var6 == 0) {
            level.setData(x, y, z, 2);
        }

        if (var6 == 1) {
            level.setData(x, y, z, 1);
        }

        if (var6 == 2) {
            level.setData(x, y, z, 3);
        }

        if (var6 == 3) {
            level.setData(x, y, z, 0);
        }
    }
}
