//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.worldgen.newbiome;

import me.alphamode.mcbig.extensions.biome.newbiome.BigLayerExtension;
import net.minecraft.world.level.newbiome.layer.Layer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(Layer.class)
public abstract class LayerMixin implements BigLayerExtension {
    @Shadow
    public abstract int[] getArea(int xo, int yo, int w, int h);

    @Override
    public int[] getArea(BigInteger xo, BigInteger yo, int w, int h) {
        return getArea(xo.intValue(), yo.intValue(), w, h);
    }
}
*///? }
