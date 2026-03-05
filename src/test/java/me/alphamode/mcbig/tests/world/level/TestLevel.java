package me.alphamode.mcbig.tests.world.level;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.NormalDimension;
import net.minecraft.world.level.storage.MemoryLevelStorage;

public class TestLevel extends Level {
    public TestLevel() {
        super(new MemoryLevelStorage(), "Test", new NormalDimension(), 123);
    }
}
