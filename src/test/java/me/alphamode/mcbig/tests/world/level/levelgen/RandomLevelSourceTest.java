package me.alphamode.mcbig.tests.world.level.levelgen;

import me.alphamode.mcbig.tests.world.level.TestLevel;
import me.alphamode.mcbig.world.level.levelgen.vanilla.BigRandomLevelSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.RandomLevelSource;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class RandomLevelSourceTest {
    @Test
    void testRandomLevelSource() {
        TestLevel level = new TestLevel();
        RandomLevelSource levelSource = new RandomLevelSource(level, level.getSeed());
        BigRandomLevelSource bigLevelSource = new BigRandomLevelSource(level, level.getSeed());

        // Test a 10x10 chunk area
        for (int xc = 0; xc < 10; xc++) {
            for (int zc = 0; zc < 10; zc++) {
                LevelChunk vanillaChunk = levelSource.getChunk(BigInteger.valueOf(xc), BigInteger.valueOf(zc));
                LevelChunk bigChunk = bigLevelSource.getChunk(BigInteger.valueOf(xc), BigInteger.valueOf(zc));
                assertArrayEquals(vanillaChunk.blocks, bigChunk.blocks, "Chunks at (" + xc + ", " + zc + ") should be equal");
            }
        }
    }
}
