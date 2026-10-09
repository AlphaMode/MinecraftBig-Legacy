package me.alphamode.mcbig.tests.world.level;

import net.minecraft.world.level.Level;
//? >=1.0.0-beta.8.0.r
//import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.NormalDimension;
import net.minecraft.world.level.storage.MemoryLevelStorage;

public class TestLevel extends Level {
    public TestLevel() {
        //? >=1.0.0-beta.8.0.r {
        /*super(new MemoryLevelStorage(), "Test", new NormalDimension(), new LevelSettings(123, 0, true));
        *///? } else
        super(new MemoryLevelStorage(), "Test", new NormalDimension(), 123);
    }
}
