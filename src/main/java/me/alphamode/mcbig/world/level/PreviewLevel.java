package me.alphamode.mcbig.world.level;

import net.minecraft.world.level.Level;
//? >=1.0.0-beta.8.0.r
//import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.NormalDimension;
import net.minecraft.world.level.storage.MemoryLevelStorage;

public class PreviewLevel extends Level {
    public PreviewLevel(long seed) {
        //? >=1.0.0-beta.8.0.r {
        /*super(new MemoryLevelStorage(), "Preview", new LevelSettings(seed, 0, false), new NormalDimension());
        *///? } else {
        super(new MemoryLevelStorage(), "Preview", new NormalDimension(), seed);
        //? }
    }
}
