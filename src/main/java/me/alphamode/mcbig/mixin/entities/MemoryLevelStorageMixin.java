package me.alphamode.mcbig.mixin.entities;

import me.alphamode.mcbig.extensions.entities.BigLevelStorageExtension;
import me.alphamode.mcbig.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.storage.MemoryLevelStorage;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MemoryLevelStorage.class)
public class MemoryLevelStorageMixin implements BigLevelStorageExtension {
    @Override
    public EntityStorage createEntityStorage(Dimension dimension) {
        return null;
    }
}
