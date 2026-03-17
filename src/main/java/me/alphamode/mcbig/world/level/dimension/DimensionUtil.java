package me.alphamode.mcbig.world.level.dimension;

import net.minecraft.world.level.dimension.Dimension;

import java.nio.file.Path;

public class DimensionUtil {
    public static Path getStorageFolder(final Dimension dimension, final Path baseDir) {
        if (dimension.id == 0) {
            return baseDir;
        } else {
            return baseDir.resolve("DIM" + dimension.id);
        }
    }
}
