package me.alphamode.mcbig.mixin;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.Region;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Region.class)
public class RegionMixin {
//    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getChunk(II)Lnet/minecraft/world/level/chunk/LevelChunk;"))
//    private LevelChunk fakeChunk(Level instance, int z, int i) {
//        return null;
//    }
}
