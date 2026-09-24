//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.worldgen.newbiome;

import net.minecraft.util.IntCache;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.layer.BiomeInitLayer;
import net.minecraft.world.level.newbiome.layer.Layer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(BiomeInitLayer.class)
public abstract class BiomeInitLayerMixin extends Layer {
    @Shadow
    private Biome[] startBiomes;

    public BiomeInitLayerMixin(long seedMixup) {
        super(seedMixup);
    }

    @Override
    public int[] getArea(BigInteger xo, BigInteger yo, int w, int h) {
        int[] b = this.parent.getArea(xo, yo, w, h);

        int[] result = IntCache.allocate(w * h);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                initRandom(x + xo.longValue(), y + yo.longValue());
                result[x + y * w] = b[x + y * w] > 0 ? this.startBiomes[nextRandom(this.startBiomes.length)].id : 0;
            }
        }

        return result;
    }
}
*///? }