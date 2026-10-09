package me.alphamode.mcbig.level.cube.storage;

import me.alphamode.mcbig.level.cube.LevelCube;
import net.minecraft.world.level.Level;

import java.io.IOException;
import java.math.BigInteger;

public interface CubeStorage {
    LevelCube load(Level level, BigInteger x, BigInteger y, BigInteger z) throws IOException;

    void save(Level level, LevelCube chunk) throws IOException;

    void saveEntities(Level level, LevelCube chunk);

    void tick();

    void flush();
}
