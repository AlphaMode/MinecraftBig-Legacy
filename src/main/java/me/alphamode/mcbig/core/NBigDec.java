package me.alphamode.mcbig.core;

import me.alphamode.mcbig.core.mpir.MPIR;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.math.BigDecimal;
import java.math.BigInteger;

public class NBigDec extends Number implements AutoCloseable, Comparable<NBigDec> {
    public static final NBigDec ZERO = new NBigDec("0");
    // Default precision — 256 bits is roughly 77 decimal digits
    public static final long DEFAULT_PRECISION = 256;

    private final Arena arena;
    final MemorySegment segment;

    public NBigDec() {
        this(DEFAULT_PRECISION);
    }

    public NBigDec(long precisionBits) {
        this.arena = Arena.ofConfined();
        this.segment = arena.allocate(MPIR.MPF_T_LAYOUT);
        try {
            MPIR.MPF_INIT.invoke(segment);
        } catch (Throwable e) {
            throw new RuntimeException("mpf_init2 failed", e);
        }
    }

    public NBigDec(String value) {
        this();
        setValue(value);
    }

    public NBigDec(String value, long precisionBits) {
        this(precisionBits);
        setValue(value);
    }

    public NBigDec(NBigInt value) {
        this(value.toString());
    }

    public NBigDec(BigDecimal value) {
        this(value.toPlainString());
    }

    public NBigDec(BigDecimal value, long precisionBits) {
        this(value.toPlainString(), precisionBits);
    }

    public NBigDec(double val) {
        this();
        try {
            MPIR.MPF_SET_D.invoke(segment, val);
        } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public void setValue(String value) {
        try (Arena tmp = Arena.ofConfined()) {
            MemorySegment cStr = tmp.allocateFrom(value);
            int result = (int) MPIR.MPF_SET_STR.invoke(segment, cStr, 0);
            if (result != 0) throw new NumberFormatException("Invalid number: " + value);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public long getPrecision() {
        try {
            return (long) MPIR.MPF_GET_PREC.invoke(segment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void setPrecision(long precisionBits) {
        try {
            MPIR.MPF_SET_PREC.invoke(segment, precisionBits);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public NBigDec add(NBigDec other) {
        NBigDec result = new NBigDec(Math.max(getPrecision(), other.getPrecision()));
        try {
            MPIR.MPF_ADD.invoke(result.segment, this.segment, other.segment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public NBigDec subtract(NBigDec other) {
        NBigDec result = new NBigDec(Math.max(getPrecision(), other.getPrecision()));
        try {
            MPIR.MPF_SUB.invoke(result.segment, this.segment, other.segment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public NBigDec multiply(NBigDec other) {
        NBigDec result = new NBigDec(Math.max(getPrecision(), other.getPrecision()));
        try {
            MPIR.MPF_MUL.invoke(result.segment, this.segment, other.segment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public NBigDec divide(NBigDec other) {
        NBigDec result = new NBigDec(Math.max(getPrecision(), other.getPrecision()));
        try {
            MPIR.MPF_DIV.invoke(result.segment, this.segment, other.segment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public int compareTo(NBigDec other) {
        try {
            return (int) MPIR.MPF_CMP.invoke(this.segment, other.segment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public int compareTo(double other) {
        if (!Double.isFinite(other)) {
            throw new ArithmeticException("Cannot compare NBigDec to non-finite double: " + other);
        }
        try {
            return (int) MPIR.MPF_CMP_D.invoke(this.segment, other);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public int compareTo(long other) {
        try {
            return (int) MPIR.MPF_CMP_SI.invoke(this.segment, other);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NBigDec other)) return false;
        return compareTo(other) == 0;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /** Convert to BigDecimal. Note: may lose precision beyond double's range. */
    public BigDecimal toBigDecimal() {
        return new BigDecimal(toString());
    }

    public NBigInt toBigInteger() {
        return new NBigInt(this);
    }

    @Override
    public String toString() {
        return toString(10, 0);
    }

    /**
     * @param base     numeric base (2–62)
     * @param numDigits number of significant digits (0 = all available)
     */
    public String toString(int base, int numDigits) {
        try (Arena tmp = Arena.ofConfined()) {
            MemorySegment expSegment = tmp.allocate(ValueLayout.JAVA_LONG);
            MemorySegment strPtr = (MemorySegment) MPIR.MPF_GET_STR.invoke(
                    MemorySegment.NULL, expSegment, base, numDigits, segment);

            long exp = expSegment.get(ValueLayout.JAVA_LONG, 0);
            String digits = strPtr.reinterpret(Long.MAX_VALUE).getString(0);

            return formatWithExponent(digits, exp);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    // mpf_get_str returns raw digits + a separate exponent, so we reconstruct
    // the decimal string here (e.g. digits="314159", exp=1 → "3.14159")
    private String formatWithExponent(String digits, long exp) {
        if (digits.isEmpty() || digits.equals("0")) return "0";

        boolean negative = digits.startsWith("-");
        String raw = negative ? digits.substring(1) : digits;
        String sign = negative ? "-" : "";

        if (exp <= 0) {
            // e.g. 0.00314...
            return sign + "0." + "0".repeat((int) -exp) + raw;
        } else if (exp >= raw.length()) {
            // e.g. 31400000
            return sign + raw + "0".repeat((int) (exp - raw.length()));
        } else {
            // e.g. 3.14159
            return sign + raw.substring(0, (int) exp) + "." + raw.substring((int) exp);
        }
    }

    @Override
    public void close() {
        try {
            MPIR.MPF_CLEAR.invoke(segment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            arena.close();
        }
    }

    @Override
    public int intValue() {
        return (int) longValue();
    }

    @Override
    public long longValue() {
        return 0;
    }

    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    @Override
    public double doubleValue() {
        try {
            return (double) MPIR.MPF_GET_D.invoke(segment);
        } catch (Throwable e) { throw new RuntimeException(e); }
    }
}
