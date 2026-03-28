package me.alphamode.mcbig.world.level;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.NormalDimension;
import net.minecraft.world.level.storage.MemoryLevelStorage;

public class PreviewLevel extends Level {
    public PreviewLevel(long seed) {
        super(new MemoryLevelStorage(), "Preview", new NormalDimension(), seed);
    }
}
