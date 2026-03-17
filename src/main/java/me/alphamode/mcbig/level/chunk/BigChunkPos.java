package me.alphamode.mcbig.level.chunk;

import java.math.BigInteger;

public record BigChunkPos(BigInteger x, BigInteger z) {
    private static final BigInteger HASH_A = BigInteger.valueOf(32767);

    public static int hash(BigInteger x, BigInteger z) {
        return (x.compareTo(BigInteger.ZERO) < 0 ? Integer.MIN_VALUE : 0) | (x.and(HASH_A).intValue()) << 16 | (z.compareTo(BigInteger.ZERO) < 0 ? 32768 : 0) | z.and(HASH_A).intValue();
    }
}
