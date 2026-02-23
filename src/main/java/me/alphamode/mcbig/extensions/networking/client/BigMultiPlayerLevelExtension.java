package me.alphamode.mcbig.extensions.networking.client;

import me.alphamode.mcbig.extensions.BigLevelExtension;
import net.minecraft.client.multiplayer.MultiPlayerLevel;

import java.math.BigInteger;

public interface BigMultiPlayerLevelExtension extends BigLevelExtension {
    default void clearResetRegion(BigInteger x0, int y0, BigInteger z0, BigInteger x1, int y1, BigInteger z1) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void addToTickNextTick(BigInteger x, int y, BigInteger z, int tileId, int delay) {
    }

    default void setChunkVisible(BigInteger x, BigInteger z, boolean visible) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean setDataNoUpdate(BigInteger x, int y, BigInteger z, int meta) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean setTileAndDataNoUpdate(BigInteger x, int y, BigInteger z, int tile, int meta) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean setTileNoUpdate(BigInteger x, int y, BigInteger z, int tile) {
        throw new UnsupportedOperationException();
    }

    default boolean doSetTileAndData(BigInteger x, int y, BigInteger z, int tile, int data) {
        throw new UnsupportedOperationException();
    }
}
