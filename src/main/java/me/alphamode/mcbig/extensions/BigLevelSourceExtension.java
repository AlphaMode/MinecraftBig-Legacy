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

    //? >=1.0.0-beta.8.0.r {
    /*default int getLightColor(BigInteger x, int y, BigInteger z, int emitt) {
        throw new UnsupportedOperationException();
    }
    *///? }

    default float getBrightness(BigInteger x, int y, BigInteger z, int emitt) {
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

    default boolean isSolidRenderTile(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean isSolidBlockingTile(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    //? >=1.0.0-beta.8.0.r {
    /*default boolean isEmptyTile(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }
    *///? }
}
