package me.alphamode.mcbig.mixin.features.big_movement;

import net.minecraft.client.renderer.culling.FrustumCuller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(FrustumCuller.class)
public class FrustumCullerMixin {
    /**
     * @author AlphaMode
     * @reason Temporary solution till I fully fix big movement
     */
    @Overwrite
    public boolean cubeInFrustum(double x0, double y0, double z0, double x1, double y1, double z1) {
        return true;
    }
}
