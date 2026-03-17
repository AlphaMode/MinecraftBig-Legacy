package me.alphamode.mcbig.extensions.entities;

import me.alphamode.mcbig.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.dimension.Dimension;

public interface BigLevelStorageExtension {
    default EntityStorage createEntityStorage(Dimension dimension) {
        throw new UnsupportedOperationException();
    }
}
