package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.LadderTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(LadderTile.class)
public abstract class LadderTileMixin extends Tile implements BigTileExtension {
    protected LadderTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public AABB getAABB(Level level, BigInteger x, int y, BigInteger z) {
        int data = level.getData(x, y, z);
        float r = 0.125F;
        if (data == 2) {
            this.setShape(0.0F, 0.0F, 1.0F - r, 1.0F, 1.0F, 1.0F);
        }

        if (data == 3) {
            this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, r);
        }

        if (data == 4) {
            this.setShape(1.0F - r, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

        if (data == 5) {
            this.setShape(0.0F, 0.0F, 0.0F, r, 1.0F, 1.0F);
        }

        return super.getAABB(level, x, y, z);
    }

    @Override
    public BigAABB getBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        int data = level.getData(x, y, z);
        float r = 0.125F;
        if (data == 2) {
            this.setShape(0.0F, 0.0F, 1.0F - r, 1.0F, 1.0F, 1.0F);
        }

        if (data == 3) {
            this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, r);
        }

        if (data == 4) {
            this.setShape(1.0F - r, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

        if (data == 5) {
            this.setShape(0.0F, 0.0F, 0.0F, r, 1.0F, 1.0F);
        }

        return super.getBigAABB(level, x, y, z);
    }

    @Override
    public AABB getTileAABB(Level level, BigInteger x, int y, BigInteger z) {
        int data = level.getData(x, y, z);
        float var6 = 0.125F;
        if (data == 2) {
            this.setShape(0.0F, 0.0F, 1.0F - var6, 1.0F, 1.0F, 1.0F);
        }

        if (data == 3) {
            this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var6);
        }

        if (data == 4) {
            this.setShape(1.0F - var6, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

        if (data == 5) {
            this.setShape(0.0F, 0.0F, 0.0F, var6, 1.0F, 1.0F);
        }

        return super.getTileAABB(level, x, y, z);
    }

    @Override
    public BigAABB getTileBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        int data = level.getData(x, y, z);
        float var6 = 0.125F;
        if (data == 2) {
            this.setShape(0.0F, 0.0F, 1.0F - var6, 1.0F, 1.0F, 1.0F);
        }

        if (data == 3) {
            this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var6);
        }

        if (data == 4) {
            this.setShape(1.0F - var6, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

        if (data == 5) {
            this.setShape(0.0F, 0.0F, 0.0F, var6, 1.0F, 1.0F);
        }

        return super.getTileBigAABB(level, x, y, z);
    }

    @Override
    public boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        if (level.isSolidBlockingTile(x.subtract(BigInteger.ONE), y, z)) {
            return true;
        } else if (level.isSolidBlockingTile(x.add(BigInteger.ONE), y, z)) {
            return true;
        } else {
            return level.isSolidBlockingTile(x, y, z.subtract(BigInteger.ONE)) ? true : level.isSolidBlockingTile(x, y, z.add(BigInteger.ONE));
        }
    }

    @Override
    public void setPlacedOnFace(Level level, BigInteger x, int y, BigInteger z, int facing) {
        int data = level.getData(x, y, z);
        if ((data == 0 || facing == 2) && level.isSolidBlockingTile(x, y, z.add(BigInteger.ONE))) {
            data = 2;
        }

        if ((data == 0 || facing == 3) && level.isSolidBlockingTile(x, y, z.subtract(BigInteger.ONE))) {
            data = 3;
        }

        if ((data == 0 || facing == 4) && level.isSolidBlockingTile(x.add(BigInteger.ONE), y, z)) {
            data = 4;
        }

        if ((data == 0 || facing == 5) && level.isSolidBlockingTile(x.subtract(BigInteger.ONE), y, z)) {
            data = 5;
        }

        level.setData(x, y, z, data);
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {
        int data = level.getData(x, y, z);
        boolean valid = false;
        if (data == 2 && level.isSolidBlockingTile(x, y, z.add(BigInteger.ONE))) {
            valid = true;
        }

        if (data == 3 && level.isSolidBlockingTile(x, y, z.subtract(BigInteger.ONE))) {
            valid = true;
        }

        if (data == 4 && level.isSolidBlockingTile(x.add(BigInteger.ONE), y, z)) {
            valid = true;
        }

        if (data == 5 && level.isSolidBlockingTile(x.subtract(BigInteger.ONE), y, z)) {
            valid = true;
        }

        if (!valid) {
            this.dropResources(level, x, y, z, data);
            level.setTile(x, y, z, 0);
        }

        super.neighborChanged(level, x, y, z, tile);
    }
}
