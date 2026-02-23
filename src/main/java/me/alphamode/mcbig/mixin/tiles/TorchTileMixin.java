package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.TorchTile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;
import java.util.Random;

@Mixin(TorchTile.class)
public abstract class TorchTileMixin extends Tile implements BigTileExtension {
    protected TorchTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public AABB getAABB(Level level, BigInteger x, int y, BigInteger z) {
        return null;
    }

    @Override
    public BigAABB getBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        return null;
    }

    private boolean canSupport(Level level, BigInteger x, int y, BigInteger z) {
        return level.isSolidBlockingTile(x, y, z) || level.getTile(x, y, z) == Tile.OAK_FENCE.id;
    }

    @Override
    public boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        if (level.isSolidBlockingTile(x.subtract(BigInteger.ONE), y, z)) {
            return true;
        } else if (level.isSolidBlockingTile(x.add(BigInteger.ONE), y, z)) {
            return true;
        } else if (level.isSolidBlockingTile(x, y, z.subtract(BigInteger.ONE))) {
            return true;
        } else {
            return level.isSolidBlockingTile(x, y, z.add(BigInteger.ONE)) ? true : this.canSupport(level, x, y - 1, z);
        }
    }

    @Override
    public void setPlacedOnFace(Level level, BigInteger x, int y, BigInteger z, int facing) {
        int side = level.getData(x, y, z);
        if (facing == 1 && this.canSupport(level, x, y - 1, z)) {
            side = 5;
        }

        if (facing == 2 && level.isSolidBlockingTile(x, y, z.add(BigInteger.ONE))) {
            side = 4;
        }

        if (facing == 3 && level.isSolidBlockingTile(x, y, z.subtract(BigInteger.ONE))) {
            side = 3;
        }

        if (facing == 4 && level.isSolidBlockingTile(x.add(BigInteger.ONE), y, z)) {
            side = 2;
        }

        if (facing == 5 && level.isSolidBlockingTile(x.subtract(BigInteger.ONE), y, z)) {
            side = 1;
        }

        level.setData(x, y, z, side);
    }

    @Override
    public void tick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        super.tick(level, x, y, z, random);
        if (level.getData(x, y, z) == 0) {
            this.onPlace(level, x, y, z);
        }
    }

    @Override
    public void onPlace(Level level, BigInteger x, int y, BigInteger z) {
        if (level.isSolidBlockingTile(x.subtract(BigInteger.ONE), y, z)) {
            level.setData(x, y, z, 1);
        } else if (level.isSolidBlockingTile(x.add(BigInteger.ONE), y, z)) {
            level.setData(x, y, z, 2);
        } else if (level.isSolidBlockingTile(x, y, z.subtract(BigInteger.ONE))) {
            level.setData(x, y, z, 3);
        } else if (level.isSolidBlockingTile(x, y, z.add(BigInteger.ONE))) {
            level.setData(x, y, z, 4);
        } else if (this.canSupport(level, x, y - 1, z)) {
            level.setData(x, y, z, 5);
        }

        this.checkCanSurvive(level, x, y, z);
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {
        if (this.checkCanSurvive(level, x, y, z)) {
            int data = level.getData(x, y, z);
            boolean canSupport = false;
            if (!level.isSolidBlockingTile(x.subtract(BigInteger.ONE), y, z) && data == 1) {
                canSupport = true;
            }

            if (!level.isSolidBlockingTile(x.add(BigInteger.ONE), y, z) && data == 2) {
                canSupport = true;
            }

            if (!level.isSolidBlockingTile(x, y, z.subtract(BigInteger.ONE)) && data == 3) {
                canSupport = true;
            }

            if (!level.isSolidBlockingTile(x, y, z.add(BigInteger.ONE)) && data == 4) {
                canSupport = true;
            }

            if (!this.canSupport(level, x, y - 1, z) && data == 5) {
                canSupport = true;
            }

            if (canSupport) {
                this.dropResources(level, x, y, z, level.getData(x, y, z));
                level.setTile(x, y, z, 0);
            }
        }
    }

    private boolean checkCanSurvive(Level level, BigInteger x, int y, BigInteger z) {
        if (!this.mayPlace(level, x, y, z)) {
            this.dropResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public HitResult clip(Level level, BigInteger x, int y, BigInteger z, Vec3 vec1, Vec3 vec2) {
        int side = level.getData(x, y, z) & 7;
        float var8 = 0.15F;
        if (side == 1) {
            this.setShape(0.0F, 0.2F, 0.5F - var8, var8 * 2.0F, 0.8F, 0.5F + var8);
        } else if (side == 2) {
            this.setShape(1.0F - var8 * 2.0F, 0.2F, 0.5F - var8, 1.0F, 0.8F, 0.5F + var8);
        } else if (side == 3) {
            this.setShape(0.5F - var8, 0.2F, 0.0F, 0.5F + var8, 0.8F, var8 * 2.0F);
        } else if (side == 4) {
            this.setShape(0.5F - var8, 0.2F, 1.0F - var8 * 2.0F, 0.5F + var8, 0.8F, 1.0F);
        } else {
            var8 = 0.1F;
            this.setShape(0.5F - var8, 0.0F, 0.5F - var8, 0.5F + var8, 0.6F, 0.5F + var8);
        }

        return super.clip(level, x, y, z, vec1, vec2);
    }

    @Override
    public void animateTick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        int data = level.getData(x, y, z);
        double var7 = x.doubleValue() + 0.5F;
        double var9 = y + 0.7F;
        double var11 = z.doubleValue() + 0.5F;
        double var13 = 0.22F;
        double var15 = 0.27F;
        if (data == 1) {
            level.addParticle("smoke", var7 - var15, var9 + var13, var11, 0.0, 0.0, 0.0);
            level.addParticle("flame", var7 - var15, var9 + var13, var11, 0.0, 0.0, 0.0);
        } else if (data == 2) {
            level.addParticle("smoke", var7 + var15, var9 + var13, var11, 0.0, 0.0, 0.0);
            level.addParticle("flame", var7 + var15, var9 + var13, var11, 0.0, 0.0, 0.0);
        } else if (data == 3) {
            level.addParticle("smoke", var7, var9 + var13, var11 - var15, 0.0, 0.0, 0.0);
            level.addParticle("flame", var7, var9 + var13, var11 - var15, 0.0, 0.0, 0.0);
        } else if (data == 4) {
            level.addParticle("smoke", var7, var9 + var13, var11 + var15, 0.0, 0.0, 0.0);
            level.addParticle("flame", var7, var9 + var13, var11 + var15, 0.0, 0.0, 0.0);
        } else {
            level.addParticle("smoke", var7, var9, var11, 0.0, 0.0, 0.0);
            level.addParticle("flame", var7, var9, var11, 0.0, 0.0, 0.0);
        }
    }
}
