package me.alphamode.mcbig.level.cube;

import java.math.BigInteger;

public class LevelColumn {
    public BigInteger[] heightMap = new BigInteger[LevelCube.SIZE * LevelCube.SIZE];
    public BigInteger minHeight = BigInteger.ZERO;

    public boolean unsaved = false;
    public boolean dontSave;
    public long lastSaveTime = 0L;
}
