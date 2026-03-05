package me.alphamode.mcbig.world.level.levelgen;

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
    FLAT("Flat", FlatLevelSource::new),
    DEBUG("Debug", DebugLevelSource::new);

    public static WorldType SELECTED = VANILLA;

    private final String type;
    @Nullable
    private final BiFunction<Level, Long, ChunkSource> factory;

    WorldType(String type, @Nullable BiFunction<Level, Long, ChunkSource> factory) {
        this.type = type;
        this.factory = factory;
    }

    @Nullable
    public BiFunction<Level, Long, ChunkSource> getFactory() {
        return this.factory;
    }

    public String getMessage() {
        return "Type: " + this.type;
    }

    public static WorldType parse(String type) {
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "vanilla" -> VANILLA;
            case "debug" -> DEBUG;
            case "flat" -> FLAT;
            default -> VANILLA;
        };
    }

    public String getType() {
        return type.toLowerCase(Locale.ROOT);
    }
}
