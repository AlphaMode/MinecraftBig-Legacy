package me.alphamode.mcbig.extensions;

import net.minecraft.world.level.tile.Tile;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface BigTileRendererExtension {

    default void renderFaceDown(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        throw new UnsupportedOperationException();
    }

    default void renderFaceUp(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        throw new UnsupportedOperationException();
    }

    default void renderNorth(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        throw new UnsupportedOperationException();
    }

    default void renderSouth(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        throw new UnsupportedOperationException();
    }

    default void renderWest(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        throw new UnsupportedOperationException();
    }

    default void renderEast(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        throw new UnsupportedOperationException();
    }

    default void tesselateInWorld(Tile tile, BigInteger x, int y, BigInteger z, int destroyProgress) {
        throw new UnsupportedOperationException();
    }

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
