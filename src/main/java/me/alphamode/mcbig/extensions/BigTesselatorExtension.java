package me.alphamode.mcbig.extensions;

import java.math.BigDecimal;

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
}
