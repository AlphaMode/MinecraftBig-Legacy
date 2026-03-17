package me.alphamode.mcbig.tests.world.level.levelgen.synth;

import me.alphamode.mcbig.world.level.levelgen.vanilla.BigRandomLevelSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Random;

public class ImprovedNoiseTest {
    @Test
    void testNoiseOverflow() {
        long seed = 123456789L;
        Random random = new Random(seed);

        // Scale
        double s = 684.412;
        double hs = 684.412;

        int xChunks = 16 / BigRandomLevelSource.CHUNK_WIDTH;
        int waterHeight = 64;

        int xSize = xChunks + 1;
        int ySize = 17;
        int zSize = xChunks + 1;

        double[] br = null;
        PerlinNoise noise = new PerlinNoise(random, 16);
        br = noise.getRegion(br, new BigDecimal("2E154").doubleValue(), 0, 0, xSize, ySize, zSize, s, hs, s);

        IO.println(br);
    }
}
