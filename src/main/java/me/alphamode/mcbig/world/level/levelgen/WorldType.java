package me.alphamode.mcbig.world.level.levelgen;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.levelgen.RandomLevelSource;

import java.util.function.BiFunction;

public enum WorldType {
    VANILLA(RandomLevelSource::new);
//    DEBUG()
//    FLAT(FlatLevelSource::new);

    private BiFunction<Level, Long, ChunkSource> factory;

    WorldType(BiFunction<Level, Long, ChunkSource> factory) {
        this.factory = factory;
    }
}
