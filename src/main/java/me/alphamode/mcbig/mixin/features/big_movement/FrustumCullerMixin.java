package me.alphamode.mcbig.mixin.features.big_movement;

import me.alphamode.mcbig.extensions.features.big_movement.BigCullerExtension;
import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.minecraft.client.renderer.culling.FrustumCuller;
import net.minecraft.client.renderer.culling.FrustumData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;

@Mixin(FrustumCuller.class)
public class FrustumCullerMixin implements BigCullerExtension {
    @Shadow
    private double yOff;
    @Shadow
    private FrustumData frustum;

    private BigDecimal xOffBig;
    private BigDecimal zOffBig;

    /**
     * @author AlphaMode
     * @reason Temporary solution till I fully fix big movement
     */
    @Overwrite
    public boolean cubeInFrustum(double x0, double y0, double z0, double x1, double y1, double z1) {
        return this.frustum.cubeInFrustum(BigMath.decimal(x0).subtract(this.xOffBig).doubleValue(), y0 - this.yOff, BigMath.decimal(z0).subtract(this.zOffBig).doubleValue(), BigMath.decimal(x1).subtract(this.xOffBig).doubleValue(), y1 - this.yOff, BigMath.decimal(z1).subtract(this.zOffBig).doubleValue());
    }

    public boolean cubeInFrustum(BigDecimal x0, double y0, BigDecimal z0, BigDecimal x1, double y1, BigDecimal z1) {
        return this.frustum.cubeInFrustum(x0.subtract(this.xOffBig).doubleValue(), y0 - this.yOff, z0.subtract(this.zOffBig).doubleValue(), x1.subtract(this.xOffBig).doubleValue(), y1 - this.yOff, z1.subtract(this.zOffBig).doubleValue());
    }

    @Override
    public boolean isVisible(BigAABB bb) {
        return cubeInFrustum(bb.x0(), bb.y0(), bb.z0(), bb.x1(), bb.y1(), bb.z1());
    }

    @Override
    public void prepare(BigDecimal xOff, double yOff, BigDecimal zOff) {
        this.xOffBig = xOff;
        this.yOff = yOff;
        this.zOffBig = zOff;
    }
}
