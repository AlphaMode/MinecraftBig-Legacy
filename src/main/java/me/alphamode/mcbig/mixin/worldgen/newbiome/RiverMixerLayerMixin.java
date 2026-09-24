//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.worldgen.newbiome;

import net.minecraft.util.IntCache;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.layer.Layer;
import net.minecraft.world.level.newbiome.layer.RiverMixerLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(RiverMixerLayer.class)
public abstract class RiverMixerLayerMixin extends Layer {
    @Shadow
    private Layer biomes;

    @Shadow
    private Layer rivers;

    public RiverMixerLayerMixin(long seedMixup) {
        super(seedMixup);
    }

    @Override
    public int[] getArea(BigInteger xo, BigInteger yo, int w, int h) {
        int[] b = this.biomes.getArea(xo, yo, w, h);
        int[] r = this.rivers.getArea(xo, yo, w, h);

        int[] result = IntCache.allocate(w * h);
        for (int i = 0; i < w * h; i++) {
            if (b[i] == Biome.ocean.id) {
                result[i] = b[i];
            } else {
                result[i] = r[i] >= 0 ? r[i] : b[i];
            }
        }

        return result;
    }
}
*///? }