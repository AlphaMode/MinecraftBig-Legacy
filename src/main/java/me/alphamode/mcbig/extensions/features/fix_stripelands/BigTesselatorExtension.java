package me.alphamode.mcbig.extensions.features.fix_stripelands;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface BigTesselatorExtension {

    default void vertexUV(BigDecimal x, double y, BigDecimal z, double u, double v) {
        throw new UnsupportedOperationException();
    }

    default void vertex(BigDecimal x, double y, BigDecimal z) {
        throw new UnsupportedOperationException();
    }

    default void offset(BigDecimal x, double y, BigDecimal z) {
        throw new UnsupportedOperationException();
    }

    default void addOffset(BigDecimal xo, float yo, BigDecimal zo) {
        throw new UnsupportedOperationException();
    }

    default void setTesselatorOffset(BigInteger xo, BigInteger zo) {
        throw new UnsupportedOperationException();
    }

    default BigInteger getOffsetX() {
        throw new UnsupportedOperationException();
    }

    default BigInteger getOffsetZ() {
        throw new UnsupportedOperationException();
    }
}
