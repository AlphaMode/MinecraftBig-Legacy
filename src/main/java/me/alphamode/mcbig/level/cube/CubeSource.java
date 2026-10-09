package me.alphamode.mcbig.level.cube;

import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.chunk.ChunkSource;

import java.math.BigInteger;

public interface CubeSource {
    boolean hasCube(BigInteger x, BigInteger y, BigInteger z);

    LevelCube getCube(BigInteger x, BigInteger y, BigInteger z);

    LevelCube create(BigInteger x, BigInteger y, BigInteger z);

    void postProcess(ChunkSource generator, BigInteger x, BigInteger y, BigInteger z);

    boolean save(boolean force, ProgressListener listener);

    boolean tick();

    boolean shouldSave();

    String gatherStats();
}
