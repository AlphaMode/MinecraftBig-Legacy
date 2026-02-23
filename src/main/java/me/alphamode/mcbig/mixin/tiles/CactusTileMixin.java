package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.CactusTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

@Mixin(CactusTile.class)
public abstract class CactusTileMixin extends Tile implements BigTileExtension {
    protected CactusTileMixin(int id, Material material) {
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
    public AABB getAABB(Level level, BigInteger x, int y, BigInteger z) {
        float epsilon = 0.0625F;
        return AABB.newTemp(x.doubleValue() + epsilon, y, z.doubleValue() + epsilon, x.add(BigInteger.ONE).doubleValue() - epsilon, y + 1 - epsilon, z.add(BigInteger.ONE).doubleValue() - epsilon);
    }

    @Override
    public BigAABB getBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        float epsilon = 0.0625F;
        return BigAABB.create(new BigDecimal(x).add(BigConstants.EPSILON), y, new BigDecimal(z).add(BigConstants.EPSILON), new BigDecimal(x.add(BigInteger.ONE)).subtract(BigConstants.EPSILON), y + 1 - epsilon, new BigDecimal(z.add(BigInteger.ONE)).subtract(BigConstants.EPSILON));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public AABB getTileAABB(Level level, BigInteger x, int y, BigInteger z) {
        float epsilon = 0.0625F;
        return AABB.newTemp(x.doubleValue() + epsilon, y, z.doubleValue() + epsilon, x.add(BigInteger.ONE).doubleValue() - epsilon, y + 1, z.add(BigInteger.ONE).doubleValue() - epsilon);
    }

    @Override
    public BigAABB getTileBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        float epsilon = 0.0625F;
        return BigAABB.create(new BigDecimal(x).add(BigConstants.EPSILON), y, new BigDecimal(z).add(BigConstants.EPSILON), new BigDecimal(x.add(BigInteger.ONE)).subtract(BigConstants.EPSILON), y + 1, new BigDecimal(z.add(BigInteger.ONE)).subtract(BigConstants.EPSILON));
    }

    @Override
    public boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        return !super.mayPlace(level, x, y, z) ? false : this.canPlace(level, x, y, z);
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {
        if (!this.canPlace(level, x, y, z)) {
            this.dropResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        }
    }

    @Override
    public boolean canPlace(Level level, BigInteger x, int y, BigInteger z) {
        if (level.getMaterial(x.subtract(BigInteger.ONE), y, z).isSolid()) {
            return false;
        } else if (level.getMaterial(x.add(BigInteger.ONE), y, z).isSolid()) {
            return false;
        } else if (level.getMaterial(x, y, z.subtract(BigInteger.ONE)).isSolid()) {
            return false;
        } else if (level.getMaterial(x, y, z.add(BigInteger.ONE)).isSolid()) {
            return false;
        } else {
            int var5 = level.getTile(x, y - 1, z);
            return var5 == Tile.CACTUS.id || var5 == Tile.SAND.id;
        }
    }

    @Override
    public void entityInside(Level level, BigInteger x, int y, BigInteger z, Entity entity) {
        entity.hurt(null, 1);
    }
}
