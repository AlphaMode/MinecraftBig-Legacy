package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.ReedTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;
import java.util.Random;

@Mixin(ReedTile.class)
public abstract class ReedTileMixin extends Tile implements BigTileExtension {
    protected ReedTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public void tick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        if (level.isEmptyTile(x, y + 1, z)) {
            int height = 1;

            while (level.getTile(x, y - height, z) == this.id) {
                height++;
            }

            if (height < 3) {
                int data = level.getData(x, y, z);
                if (data == 15) {
                    level.setTile(x, y + 1, z, this.id);
                    level.setData(x, y, z, 0);
                } else {
                    level.setData(x, y, z, data + 1);
                }
            }
        }
    }

    @Override
    public boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        int tt = level.getTile(x, y - 1, z);
        if (tt == this.id) {
            return true;
        } else if (tt != Tile.GRASS.id && tt != Tile.DIRT.id) {
            return false;
        } else if (level.getMaterial(x.subtract(BigInteger.ONE), y - 1, z) == Material.WATER) {
            return true;
        } else if (level.getMaterial(x.add(BigInteger.ONE), y - 1, z) == Material.WATER) {
            return true;
        } else {
            return level.getMaterial(x, y - 1, z.subtract(BigInteger.ONE)) == Material.WATER ? true : level.getMaterial(x, y - 1, z.add(BigInteger.ONE)) == Material.WATER;
        }
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {
        this.checkAlive(level, x, y, z);
    }

    protected final void checkAlive(Level level, BigInteger x, int y, BigInteger z) {
        if (!this.canPlace(level, x, y, z)) {
            this.dropResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        }
    }

    @Override
    public boolean canPlace(Level level, BigInteger x, int y, BigInteger z) {
        return this.mayPlace(level, x, y, z);
    }

    @Override
    public AABB getAABB(Level level, BigInteger x, int y, BigInteger z) {
        return null;
    }

    @Override
    public BigAABB getBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        return null;
    }
}
