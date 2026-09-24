//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.worldgen.newbiome;

import net.minecraft.util.IntCache;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.layer.Layer;
import net.minecraft.world.level.newbiome.layer.TemperatureLayer;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(TemperatureLayer.class)
public abstract class TemperatureLayerMixin extends Layer {
    public TemperatureLayerMixin(long seedMixup) {
        super(seedMixup);
    }

    @Override
    public int[] getArea(BigInteger xo, BigInteger yo, int w, int h) {
        int[] b = this.parent.getArea(xo, yo, w, h);

        int[] result = IntCache.allocate(w * h);
        for (int i = 0; i < w * h; i++) {
            result[i] = Biome.biomes[b[i]].getTemperatureInt();
        }
        return result;
    }
}
*///? }