package me.alphamode.mcbig.world.level.region;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.entity.TileEntity;

import java.math.BigInteger;

public class LocalLevelRegion {
    private final Level level;
    private final BigInteger xRegion;
    private final BigInteger zRegion;

    public LocalLevelRegion(Level level, BigInteger xRegion, BigInteger zRegion) {
        this.level = level;
        this.xRegion = xRegion;
        this.zRegion = zRegion;
    }

    public IntResult getTile(int x, int y, int z) {
        return ResultOutOfRegion.INSTANCE;
    }

    public Result<TileEntity> getTileEntity(int x, int y, int z) {
        return null;
    }

    public FloatResult getBrightness(int x, int y, int z, int emitt) {
        return ResultOutOfRegion.INSTANCE;
    }

    public FloatResult getBrightness(int x, int y, int z) {
        return ResultOutOfRegion.INSTANCE;
    }

    public IntResult getData(int x, int y, int z) {
        return ResultOutOfRegion.INSTANCE;
    }

    public Result<Material> getMaterial(int x, int y, int z) {
        return null;
    }

    public BoolResult isSolidRenderTile(int x, int y, int z) {
        return ResultOutOfRegion.INSTANCE;
    }

    public BoolResult isSolidBlockingTile(int x, int y, int z) {
        return ResultOutOfRegion.INSTANCE;
    }

    public BiomeSource getBiomeSource() {
        return this.level.getBiomeSource();
    }

    public sealed interface BoolResult {}

    public sealed interface IntResult {}

    public sealed interface FloatResult {}

    public sealed interface Result<T> {}

    public record BoolResultOk(boolean value) implements BoolResult {}

    public record IntResultOk(int value) implements IntResult {}

    public record FloatResultOk(float value) implements FloatResult {}

    public record ResultOk<T>(T value) implements Result<T> {}

    public enum ResultOutOfRegion implements BoolResult, IntResult, FloatResult, Result<Void> {
        INSTANCE;
    }
}
