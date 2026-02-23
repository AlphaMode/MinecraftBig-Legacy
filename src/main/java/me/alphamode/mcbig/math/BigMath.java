package me.alphamode.mcbig.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

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

    public static final MathContext CONTEXT = MathContext.UNLIMITED;

    public static BigDecimal decimal(double value) {
        return new BigDecimal(value, CONTEXT);
    }

    public static BigDecimal addD(BigDecimal a, BigInteger b) {
        return a.add(new BigDecimal(b, CONTEXT));
    }

    public static BigDecimal addD(BigDecimal a, double b) {
        return a.add(new BigDecimal(b, CONTEXT));
    }

    public static BigDecimal addD(BigDecimal a, BigDecimal b) {
        return a.add(b, CONTEXT);
    }

    public static BigDecimal addD(BigInteger a, BigDecimal b) {
        return new BigDecimal(a, CONTEXT).add(b);
    }

    public static BigDecimal addD(BigInteger a, BigInteger b) {
        return new BigDecimal(a, CONTEXT).add(new BigDecimal(b, CONTEXT));
    }

    public static BigDecimal addD(BigInteger a, double b) {
        return new BigDecimal(a, CONTEXT).add(new BigDecimal(b, CONTEXT));
    }

    // Sub
    public static BigDecimal subD(BigDecimal a, BigInteger b) {
        return a.subtract(new BigDecimal(b, CONTEXT));
    }

    public static BigDecimal subD(BigDecimal a, double b) {
        return a.subtract(new BigDecimal(b, CONTEXT));
    }

    public static BigDecimal subD(BigDecimal a, BigDecimal b) {
        return a.subtract(b);
    }

    public static BigDecimal subD(BigInteger a, BigInteger b) {
        return new BigDecimal(a, CONTEXT).subtract(new BigDecimal(b, CONTEXT));
    }

    public static BigDecimal subD(BigInteger a, double b) {
        return new BigDecimal(a, CONTEXT).subtract(new BigDecimal(b, CONTEXT));
    }
}
