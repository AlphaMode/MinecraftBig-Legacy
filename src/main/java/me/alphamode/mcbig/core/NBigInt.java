package me.alphamode.mcbig.core;

import me.alphamode.mcbig.core.mpir.MPIR;

import java.lang.foreign.*;

/**
 * Native Big Integer
 */
public class NBigInt extends Number implements AutoCloseable, Comparable<NBigInt> {
    public static final NBigInt ZERO = new NBigInt("0", Arena.global());
    public static final NBigInt ONE = new NBigInt("1", Arena.global());
    private final Arena arena;
    final MemorySegment segment;

    public static NBigInt valueOf(long value) {
        return new NBigInt(Long.toString(value));
    }

    public NBigInt(String value) {
        this(value, Arena.ofAuto());
    }

    public NBigInt(String value, Arena arena) {
        this.arena = arena;
        this.segment = arena.allocate(MPIR.MPZ_T);
        try {
            try (Arena tmp = Arena.ofConfined()) {
                MemorySegment cStr = tmp.allocateFrom(value);
                MPIR.MPZ_INIT_SET_STR.invoke(segment, cStr, 10);
            }
        } catch (Throwable e) {
            throw new RuntimeException("mpz_init failed", e);
        }
    }

    public NBigInt(NBigDec val) {
        this(val, Arena.ofAuto());
    }

    public NBigInt(NBigDec val, Arena arena) {
        this.arena = arena;
        this.segment = arena.allocate(MPIR.MPZ_T);
        try {
            MPIR.MPZ_SET_F.invoke(segment, val.segment);  // → __gmpz_set_f
        } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public NBigInt() {
        this(Arena.ofAuto());
    }

    @Override
    public int intValue() {
        try {
            return (int) (long) MPIR.MPZ_GET_SI.invoke(this.segment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long longValue() {
        try {
            return (long) MPIR.MPZ_GET_SI.invoke(this.segment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public float floatValue() {
        try {
            return (float) (double) MPIR.MPZ_GET_D.invoke(this.segment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double doubleValue() {
        try {
            return (double) MPIR.MPZ_GET_D.invoke(this.segment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public NBigInt(Arena arena) {
        this.arena = arena;
        this.segment = arena.allocate(MPIR.MPZ_T);
        try {
            MPIR.MPZ_INIT.invoke(segment);
        } catch (Throwable e) {
            throw new RuntimeException("mpz_init failed", e);
        }
    }

    public void setValue(String value) {
        try (Arena tmp = Arena.ofConfined()) {
            MemorySegment cStr = tmp.allocateFrom(value);
            int result = (int) MPIR.MPZ_SET_STR.invoke(segment, cStr, 10);
            if (result != 0) throw new NumberFormatException("Invalid number: " + value);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public NBigInt add(NBigInt other) {
        NBigInt result = new NBigInt();
        try {
            MPIR.MPZ_ADD.invoke(result.segment, this.segment, other.segment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public NBigInt subtract(NBigInt other) {
        NBigInt result = new NBigInt();
        try {
            MPIR.MPZ_SUB.invoke(result.segment, this.segment, other.segment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public NBigInt multiply(NBigInt other) {
        NBigInt result = new NBigInt();
        try {
            MPIR.MPZ_MUL.invoke(result.segment, this.segment, other.segment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public String toString() {
        try {
            // Pass NULL as buffer — MPIR will allocate the string for us
            MemorySegment strPtr = (MemorySegment)
                    MPIR.MPZ_GET_STR.invoke(MemorySegment.NULL, 10, segment);
            return strPtr.reinterpret(Long.MAX_VALUE).getString(0);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public NBigInt pow(long exponent) {
        NBigInt result = new NBigInt();
        try {
            MPIR.MPZ_POW_UI.invoke(result.segment, this.segment, exponent);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static final NBigInt create() {
        FunctionDescriptor descriptor = FunctionDescriptor.ofVoid();
//        return new NBigInt();
        return null;
    }

    @Override
    public int compareTo(final NBigInt other) {
        try {
            return (int) MPIR.MPZ_CMP.invoke(this.segment, other.segment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public int compareTo(long other) {
        try {
            return (int) MPIR.MPZ_CMP_SI.invoke(this.segment, other);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            MPIR.MPZ_CLEAR.invoke(segment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            this.arena.close();
        }
    }

    public NBigInt and(NBigInt val) {
        NBigInt result = new NBigInt();
        try { MPIR.MPZ_AND.invoke(result.segment, segment, val.segment); }
        catch (Throwable e) { throw new RuntimeException(e); }
        return result;
    }
}
