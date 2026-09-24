package me.alphamode.mcbig.world.level.levelgen;

//import me.alphamode.mcbig.world.level.levelgen.vanilla.BigFarlandsRandomLevelSource;
import me.alphamode.mcbig.world.level.levelgen.vanilla.BigRandomLevelSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.levelgen.RandomLevelSource;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.BiFunction;

public enum WorldType {
    VANILLA("Vanilla", null),
    BIG_VANILLA("Big Vanilla", BigRandomLevelSource::new),
//    BIG_FARLANDS("Big Vanilla With Farlands", BigFarlandsRandomLevelSource::new),
    FLAT("Flat", FlatLevelSource::new),
    DEBUG("Debug", DebugLevelSource::new);

    public static WorldType SELECTED = VANILLA;

    private final String type;
    @Nullable
    private final ChunkSourceFactory factory;

    WorldType(String type, @Nullable ChunkSourceFactory factory) {
        this.type = type;
        this.factory = factory;
    }

    @Nullable
    public ChunkSourceFactory getFactory() {
        return this.factory;
    }

    public String getMessage() {
        return "Type: " + this.type;
    }

    public static WorldType parse(String type) {
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "vanilla" -> VANILLA;
            case "big_vanilla" -> BIG_VANILLA;
//            case "big_vanilla_with_farlands" -> BIG_FARLANDS;
            case "debug" -> DEBUG;
            case "flat" -> FLAT;
            default -> VANILLA;
        };
    }

    public String getType() {
        return type.toLowerCase(Locale.ROOT).replace(" ", "_");
    }

    @FunctionalInterface
    public interface ChunkSourceFactory {
        //~ if >=1.0.0-beta.8.0.r ', long seed)' -> ', long seed, boolean generateStructures)'
        ChunkSource create(Level level, long seed);
    }
}
