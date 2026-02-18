package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Bush;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(Bush.class)
public abstract class BushMixin implements BigTileExtension {
    @Shadow
    protected abstract boolean mayPlaceOn(int id);

    @Override
    public boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        return BigTileExtension.super.mayPlace(level, x, y, z) && this.mayPlaceOn(level.getTile(x, y - 1, z));
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {
        BigTileExtension.super.neighborChanged(level, x, y, z, tile);
        this.checkAlive(level, x, y, z);
    }

    @Override
    public void tick(Level level, BigInteger x, int y, BigInteger z, Random random) {
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
        return (level.getRawBrightness(x, y, z) >= 8 || level.canSeeSky(x, y, z)) && this.mayPlaceOn(level.getTile(x, y - 1, z));
    }

    @Override
    public AABB getAABB(Level level, BigInteger x, int y, BigInteger z) {
        return null;
    }
}
