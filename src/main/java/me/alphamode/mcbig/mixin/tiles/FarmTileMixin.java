package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.FarmTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

@Mixin(FarmTile.class)
public abstract class FarmTileMixin extends Tile {
    protected FarmTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public AABB getAABB(Level level, BigInteger x, int y, BigInteger z) {
        return AABB.newTemp(x.doubleValue(), y + 0, z.doubleValue(), x.add(BigInteger.ONE).doubleValue(), y + 1, z.add(BigInteger.ONE).doubleValue());
    }

    @Override
    public BigAABB getBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        return BigAABB.create(new BigDecimal(x), y + 0, new BigDecimal(z), new BigDecimal(x.add(BigInteger.ONE)), y + 1, new BigDecimal(z.add(BigInteger.ONE)));
    }

    @Override
    public void tick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        if (random.nextInt(5) == 0) {
            if (!isNearWater(level, x, y, z) && !level.isRainingAt(x, y + 1, z)) {
                int moisture = level.getData(x, y, z);
                if (moisture > 0) {
                    level.setData(x, y, z, moisture - 1);
                } else if (!isUnderCrops(level, x, y, z)) {
                    level.setTile(x, y, z, Tile.dirt.id);
                }
            } else {
                level.setData(x, y, z, 7);
            }
        }
    }

    @Override
    public void stepOn(Level level, BigInteger x, int y, BigInteger z, Entity entity) {
        if (level.random.nextInt(4) == 0) {
            level.setTile(x, y, z, Tile.dirt.id);
        }
    }

    private boolean isUnderCrops(Level level, BigInteger x, int y, BigInteger z) {
        BigInteger r = BigInteger.ZERO;
        for (var xx = x.subtract(r); xx.compareTo(x.add(r)) <= 0; xx = xx.add(BigInteger.ONE)) {
            for (var zz = z.subtract(r); zz.compareTo(z.add(r)) <= 0; zz = zz.add(BigInteger.ONE)) {
                //? >=1.0.0-beta.8.0.r {
                /*int tile = level.getTile(xx, y + 1, zz);
                if (tile == Tile.wheat.id || tile == Tile.melonStem.id || tile == Tile.pumpkinStem.id) {
                *///? } else
                if (level.getTile(xx, y + 1, zz) == Tile.wheat.id) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNearWater(Level level, BigInteger x, int y, BigInteger z) {
        var xc = x.add(BigConstants.FOUR);
        var zc = z.add(BigConstants.FOUR);
        for (var xx = x.subtract(BigConstants.FOUR); xx.compareTo(xc) <= 0; xx = xx.add(BigInteger.ONE)) {
            for (int yy = y; yy <= y + 1; yy++) {
                for (var zz = z.subtract(BigConstants.FOUR); zz.compareTo(zc) <= 0; zz = zz.add(BigInteger.ONE)) {
                    if (level.getMaterial(xx, yy, zz) == Material.water) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int type) {
        super.neighborChanged(level, x, y, z, type);
        Material above = level.getMaterial(x, y + 1, z);
        if (above.isSolid()) {
            level.setTile(x, y, z, Tile.dirt.id);
        }
    }
}
