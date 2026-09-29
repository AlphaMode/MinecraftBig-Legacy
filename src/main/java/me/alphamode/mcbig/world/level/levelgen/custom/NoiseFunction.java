package me.alphamode.mcbig.world.level.levelgen.custom;

import java.math.BigDecimal;

@FunctionalInterface
public interface NoiseFunction {
    double evaluate(BigDecimal x, BigDecimal y, BigDecimal z);

    default double evaluate(BigDecimal x, double y, BigDecimal z) {
        return evaluate(x, BigDecimal.valueOf(y), z);
    }
}
