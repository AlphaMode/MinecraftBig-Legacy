package me.alphamode.mcbig.level;

import me.alphamode.mcbig.world.level.levelgen.WorldType;

public record BigWorldSettings(WorldType type, RandomSettings randomSettings) {

    public record RandomSettings(boolean useBigRandom, boolean useFixedBits) {}
}
