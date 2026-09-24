//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.worldgen.newbiome;

import net.minecraft.util.IntCache;
import net.minecraft.world.level.newbiome.layer.Layer;
import net.minecraft.world.level.newbiome.layer.RiverInitLayer;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(RiverInitLayer.class)
public abstract class RiverInitLayerMixin extends Layer {
    public RiverInitLayerMixin(long seedMixup) {
        super(seedMixup);
    }

    @Override
    public int[] getArea(BigInteger xo, BigInteger yo, int w, int h) {
        int[] b = this.parent.getArea(xo, yo, w, h);

        int[] result = IntCache.allocate(w * h);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                initRandom(x + xo.longValue(), y + yo.longValue());
                result[x + y * w] = b[x + y * w] > 0 ? nextRandom(2) + 2 : 0;
            }
        }

        return result;
    }
}
*///? }