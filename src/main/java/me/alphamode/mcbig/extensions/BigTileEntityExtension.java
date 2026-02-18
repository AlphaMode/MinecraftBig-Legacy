package me.alphamode.mcbig.extensions;

import java.math.BigInteger;

public interface BigTileEntityExtension {
    default BigInteger getX() {
        throw new UnsupportedOperationException();
    }

    default BigInteger getZ() {
        throw new UnsupportedOperationException();
    }

    default void setX(BigInteger x) {
        throw new UnsupportedOperationException();
    }

    default void setZ(BigInteger z) {
        throw new UnsupportedOperationException();
    }
}
