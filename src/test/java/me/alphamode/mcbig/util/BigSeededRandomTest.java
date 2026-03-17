package me.alphamode.mcbig.util;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BigSeededRandomTest {

    @Test
    void testBigSeededRandomAgainstJavaRandom() {
        BigInteger seed = BigInteger.valueOf(123456789L);

        Random javaRandom = new Random(seed.longValue());
        BigSeededRandom bigRandom = new BigSeededRandom(seed);

        for (int i = 0; i < 1000; i++) {
            assertEquals(javaRandom.nextInt(), bigRandom.nextInt(), "Random numbers should be equal (Attempt " + i + ")");
        }
    }
}