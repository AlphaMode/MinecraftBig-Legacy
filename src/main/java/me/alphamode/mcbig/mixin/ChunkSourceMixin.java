package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.BigChunkSourceExtension;
import net.minecraft.world.level.chunk.ChunkSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkSource.class)
public interface ChunkSourceMixin extends BigChunkSourceExtension {
}
