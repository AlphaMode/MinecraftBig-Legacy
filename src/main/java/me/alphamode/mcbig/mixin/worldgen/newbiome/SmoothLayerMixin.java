//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.worldgen.newbiome;

import net.minecraft.util.IntCache;
import net.minecraft.world.level.newbiome.layer.Layer;
import net.minecraft.world.level.newbiome.layer.SmoothLayer;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(SmoothLayer.class)
public abstract class SmoothLayerMixin extends Layer {
    public SmoothLayerMixin(long seedMixup) {
        super(seedMixup);
    }

    @Override
    public int[] getArea(BigInteger xo, BigInteger yo, int w, int h) {
        var px = xo.subtract(BigInteger.ONE);
        var py = yo.subtract(BigInteger.ONE);
        int pw = w + 2;
        int ph = h + 2;
        int[] p = this.parent.getArea(px, py, pw, ph);

        int[] result = IntCache.allocate(w * h);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int l = p[x + 0 + (y + 1) * pw];
                int r = p[x + 2 + (y + 1) * pw];
                int u = p[x + 1 + (y + 0) * pw];
                int d = p[x + 1 + (y + 2) * pw];
                int c = p[x + 1 + (y + 1) * pw];
                if (l == r && u == d) {
                    initRandom(x + xo.longValue(), y + yo.longValue());
                    if (nextRandom(2) == 0) c = l;
                    else c = u;
                } else {
                    if (l == r) c = l;
                    if (u == d) c = u;
                }
                result[x + y * w] = c;
            }
        }

        return result;
    }
}
*///? }