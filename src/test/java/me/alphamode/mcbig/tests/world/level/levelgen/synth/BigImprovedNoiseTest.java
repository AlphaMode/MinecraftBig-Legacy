package me.alphamode.mcbig.tests.world.level.levelgen.synth;

import me.alphamode.mcbig.world.level.levelgen.synth.BigPerlinNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BigImprovedNoiseTest {
    @Test
    void testImprovedNoise() {
        long seed = 123456789L;

        PerlinNoise normalNoise = new PerlinNoise(new Random(seed), 4);
        BigPerlinNoise bigNoise = new BigPerlinNoise(new Random(seed), 4);

        assertEquals(normalNoise.getValue(1, 1), bigNoise.getValue(BigDecimal.ONE, BigDecimal.ONE));
    }
}