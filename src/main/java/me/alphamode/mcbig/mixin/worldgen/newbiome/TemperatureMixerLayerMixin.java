//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.worldgen.newbiome;

import net.minecraft.util.IntCache;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.layer.Layer;
import net.minecraft.world.level.newbiome.layer.TemperatureMixerLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(TemperatureMixerLayer.class)
public abstract class TemperatureMixerLayerMixin extends Layer {
    @Shadow
    private Layer temp;

    @Shadow
    private int layer;

    public TemperatureMixerLayerMixin(long seedMixup) {
        super(seedMixup);
    }

    @Override
    public int[] getArea(BigInteger xo, BigInteger yo, int w, int h) {
        int[] b = this.parent.getArea(xo, yo, w, h);
        int[] t = this.temp.getArea(xo, yo, w, h);

        int[] result = IntCache.allocate(w * h);
        for (int i = 0; i < w * h; i++) {
            result[i] = t[i] + (Biome.biomes[b[i]].getTemperatureInt() - t[i]) / (this.layer * 2 + 1);
        }

        return result;
    }
}
*///? }