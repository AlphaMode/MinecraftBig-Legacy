package me.alphamode.mcbig.extensions;

import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.entity.TileEntity;

import java.math.BigInteger;

public interface BigLevelSourceExtension {
    default int getTile(BigInteger x, int y, BigInteger z) {
        return ((LevelSource) this).getTile(x.intValue(), y, z.intValue());//throw new UnsupportedOperationException();
    }

    default TileEntity getTileEntity(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default float getBrightness(BigInteger x, int y, BigInteger z, int max) {
        throw new UnsupportedOperationException();
    }

    default float getBrightness(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default int getData(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default Material getMaterial(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean isSolidTile(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean isSolidBlockingTile(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }
}
