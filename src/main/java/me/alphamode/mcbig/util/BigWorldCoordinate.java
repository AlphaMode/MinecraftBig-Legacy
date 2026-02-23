package me.alphamode.mcbig.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public record BigWorldCoordinate<T>(boolean relative, Value<T> value) {
    public T get(final T original) {
        return this.relative ? this.value.add(original) : this.value.value();
    }

    sealed interface Value<T> {
        T add(T other);

        T value();
    }

    public record BigIntegerValue(BigInteger value) implements Value<BigInteger> {
        @Override
        public BigInteger add(BigInteger other) {
            return this.value.add(other);
        }
    }

    public record BigDecimalValue(BigDecimal value) implements Value<BigDecimal> {
        @Override
        public BigDecimal add(BigDecimal other) {
            return this.value.add(other);
        }
    }

    public record IntValue(Integer value) implements Value<Integer> {
        @Override
        public Integer add(Integer other) {
            return this.value + other;
        }
    }

    public record DoubleValue(Double value) implements Value<Double> {
        @Override
        public Double add(Double other) {
            return this.value + other;
        }
    }
}
