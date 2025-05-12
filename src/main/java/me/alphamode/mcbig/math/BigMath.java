package me.alphamode.mcbig.math;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigMath {
    public static BigInteger floor(double value) {
        BigInteger i = BigInteger.valueOf((long) value);
        return value < i.doubleValue() ? i.subtract(BigInteger.ONE) : i;
    }

    public static BigInteger floor(BigDecimal value) {
        BigInteger i = value.toBigInteger();
        return value.compareTo(new BigDecimal(i)) < 0 ? i.subtract(BigInteger.ONE) : i;
    }

    public static BigInteger intFloorDiv(BigInteger x, BigInteger y) {
        return x.compareTo(BigInteger.ZERO) < 0 ? ((x.negate().subtract(BigInteger.ONE)).divide(y)).negate().subtract(BigInteger.ONE) : x.divide(y);
    }
}
