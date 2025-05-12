package me.alphamode.mcbig.extensions;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface BigOffsettedRenderListExtension {
    default void init(BigInteger x, int y, BigInteger z, double cameraX, double cameraY, double cameraZ) {
        throw new UnsupportedOperationException();
    }

    default void init(BigInteger x, int y, BigInteger z, BigDecimal cameraX, double cameraY, BigDecimal cameraZ) {
        throw new UnsupportedOperationException();
    }

    default boolean isAt(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }
}
