package me.alphamode.mcbig.extensions;

import java.math.BigInteger;

public interface BigEntityExtension {
    default void setXChunk(BigInteger x) {
        throw new UnsupportedOperationException();
    }

    default void setZChunk(BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default BigInteger getXChunk() {
        throw new UnsupportedOperationException();
    }

    default BigInteger getZChunk() {
        throw new UnsupportedOperationException();
    }
}
