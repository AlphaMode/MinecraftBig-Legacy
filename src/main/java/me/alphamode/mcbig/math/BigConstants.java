package me.alphamode.mcbig.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class BigConstants {
    public static final BigInteger FOUR = BigInteger.valueOf(4);
    public static final BigInteger EIGHT = BigInteger.valueOf(8);
    public static final BigInteger FIFTEEN = BigInteger.valueOf(15);
    public static final BigInteger SIXTEEN = BigInteger.valueOf(16);
    public static final BigDecimal SIXTEEN_F = BigDecimal.valueOf(16.0);
    public static final BigInteger THIRTY_ONE = BigInteger.valueOf(31);
    public static final BigInteger REGION_MASK = BigInteger.valueOf(63);
    public static final BigInteger CHUNK_OFFSET = BigInteger.valueOf(1023);

    public static final BigDecimal TWO = BigDecimal.valueOf(2);
    public static final BigDecimal POINT_TWO = BigDecimal.valueOf(0.2);
    public static final BigDecimal POINT_THREE = BigDecimal.valueOf(0.3);
    public static final BigDecimal POINT_FOUR = BigDecimal.valueOf(0.4);
    public static final BigDecimal POINT_FIVE = BigDecimal.valueOf(0.5);
    public static final BigDecimal EPSILON = new BigDecimal(0.0625F, MathContext.DECIMAL32);
    public static final BigDecimal NEGATIVE_EPSILON = EPSILON.negate();
    public static final BigDecimal CLIP = BigMath.decimal(999.0);
}
