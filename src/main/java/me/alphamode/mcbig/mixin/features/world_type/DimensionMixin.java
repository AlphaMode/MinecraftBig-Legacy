package me.alphamode.mcbig.mixin.features.world_type;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.Dimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Dimension.class)
public class DimensionMixin {
    @Shadow
    public Level level;

    @Inject(method = "createRandomLevelSource", at = @At("HEAD"), cancellable = true)
    private void createWorldTypeSource(CallbackInfoReturnable<ChunkSource> cir) {
        if (this.level.getLevelData().getWorldType().getFactory() != null) {
            cir.setReturnValue(this.level.getLevelData().getWorldType().getFactory().apply(this.level, this.level.getSeed()));
        }
    }
}
