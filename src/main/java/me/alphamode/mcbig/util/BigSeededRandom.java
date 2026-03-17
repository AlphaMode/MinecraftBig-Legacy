package me.alphamode.mcbig.util;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

// Dynamically scale the seeds bits
public class BigSeededRandom extends Random {

    private final AtomicReference<BigInteger> seed;
    private final boolean fixedBits;
    private int seedBits;

    private BigInteger multiplier = BigInteger.valueOf(0x5DEECE66DL);
    private BigInteger addend = BigInteger.valueOf(0xBL);

    public BigSeededRandom(BigInteger seed) {
        this(seed, false);
    }

    public BigSeededRandom(BigInteger seed, boolean fixedBits) {
        super(0L);
        this.seed = new AtomicReference<>(initialScramble(seed));
        this.fixedBits = fixedBits;
        this.seedBits = fixedBits ? 48 : Math.max(48, seed.bitLength() + 1);
    }

    private BigInteger computeMask(BigInteger seed) {
        int bits = Math.max(48, seed.bitLength() + 1);
        return BigInteger.ONE.shiftLeft(bits).subtract(BigInteger.ONE);
    }

    private BigInteger initialScramble(BigInteger seed) {
        return (seed.xor(multiplier)).and(computeMask(seed));
    }

    public synchronized void setSeed(BigInteger seed) {
        this.seed.set(initialScramble(seed));
        this.seedBits = fixedBits ? 48 : Math.max(48, seed.bitLength() + 1);
    }

    @Override
    public synchronized void setSeed(long seed) {
        if (seed == 0L) {
            return;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    protected int next(int bits) {
        BigInteger oldseed, nextseed;
        AtomicReference<BigInteger> seed = this.seed;
        BigInteger mask = computeMask(seed.get());
        do {
            oldseed = seed.get();
            nextseed = (oldseed.multiply(multiplier).add(addend)).and(mask);
        } while (!seed.compareAndSet(oldseed, nextseed));
        return (nextseed.shiftRight(seedBits - bits)).intValue();
    }
}
