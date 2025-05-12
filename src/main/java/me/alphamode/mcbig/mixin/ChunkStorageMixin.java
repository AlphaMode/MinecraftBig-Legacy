package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.BigChunkStorageExtension;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkStorage.class)
public interface ChunkStorageMixin extends BigChunkStorageExtension {
}
