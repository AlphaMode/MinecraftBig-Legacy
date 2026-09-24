package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.world.phys.BigAABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.SignTile;
import net.minecraft.world.level.tile.TileEntityTile;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(SignTile.class)
public abstract class SignTileMixin extends TileEntityTile {
    @Shadow
    private boolean isStanding;

    protected SignTileMixin(int id, Material material) {
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

    @Override
    public BigAABB getTileBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        updateShape(level, x, y, z);
        return super.getTileBigAABB(level, x, y, z);
    }

    @Override
    public AABB getTileAABB(Level level, BigInteger x, int y, BigInteger z) {
        updateShape(level, x, y, z);
        return super.getTileAABB(level, x, y, z);
    }

    @Override
    public void updateShape(LevelSource level, BigInteger x, int y, BigInteger z) {
        if (!this.isStanding) {
            int face = level.getData(x, y, z);

            float h0 = (4 + 0.5f) / 16.0f;
            float h1 = (12 + 0.5f) / 16.0f;
            float w0 = 0 / 16.0f;
            float w1 = 16 / 16.0f;

            float d0 = 2 / 16.0f;

            setShape(0, 0, 0, 1, 1, 1);
            if (face == 2) setShape(w0, h0, 1 - d0, w1, h1, 1);
            if (face == 3) setShape(w0, h0, 0, w1, h1, d0);
            if (face == 4) setShape(1 - d0, h0, w0, 1, h1, w1);
            if (face == 5) setShape(0, h0, w0, d0, h1, w1);
        }
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int type) {
        boolean remove = false;
        if (this.isStanding) {
            if (!level.getMaterial(x, y - 1, z).isSolid()) {
                remove = true;
            }
        } else {
            int face = level.getData(x, y, z);
            remove = true;
            if (face == 2 && level.getMaterial(x, y, z.add(BigInteger.ONE)).isSolid())      remove = false;
            if (face == 3 && level.getMaterial(x, y, z.subtract(BigInteger.ONE)).isSolid()) remove = false;
            if (face == 4 && level.getMaterial(x.add(BigInteger.ONE), y, z).isSolid())      remove = false;
            if (face == 5 && level.getMaterial(x.subtract(BigInteger.ONE), y, z).isSolid()) remove = false;
        }

        if (remove) {
            spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        }
        super.neighborChanged(level, x, y, z, type);
    }
}
