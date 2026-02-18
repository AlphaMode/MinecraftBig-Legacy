package me.alphamode.mcbig.util;

import java.math.BigDecimal;

public record BigWorldCoordinate(boolean relative, BigDecimal value) {
    public BigDecimal get(final BigDecimal original) {
        return this.relative ? this.value.add(original) : this.value;
    }
}
