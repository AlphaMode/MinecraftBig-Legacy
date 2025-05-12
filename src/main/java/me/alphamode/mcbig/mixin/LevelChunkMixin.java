package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.BigLevelChunkExtension;
import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin implements BigLevelChunkExtension {
    @Shadow public abstract int getHeightmap(int x, int z);

    @Inject(method = {"<init>(Lnet/minecraft/world/level/Level;II)V", "<init>(Lnet/minecraft/world/level/Level;[BII)V"}, at = @At("TAIL"))
    private void throwOnVanillaInit(CallbackInfo ci) {
        if (!((Object) this instanceof BigLevelChunk) && !((Object) this instanceof EmptyLevelChunk)) {
            throw new RuntimeException("Level chunk is not an instance of BigLevelChunk");
        }
    }
}
