package me.alphamode.mcbig.extensions;

import net.minecraft.world.level.tile.Tile;

import java.math.BigInteger;

public interface BigTileRendererExtension {
    default boolean tesselateInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean tesselateBlockInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean tesselateBlockInWorldWithAmbienceOcclusion(Tile tile, BigInteger x, int y, BigInteger z, float r, float g, float b) {
        throw new UnsupportedOperationException();
    }

    default boolean tesselateBlockInWorld(Tile tile, BigInteger x, int y, BigInteger z, float r, float g, float b) {
        throw new UnsupportedOperationException();
    }

    default boolean tesselateWaterInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }
}
