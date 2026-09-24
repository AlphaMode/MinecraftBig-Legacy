//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.worldgen.newbiome;

import net.minecraft.util.IntCache;
import net.minecraft.world.level.newbiome.layer.AddIslandLayer;
import net.minecraft.world.level.newbiome.layer.Layer;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(AddIslandLayer.class)
public abstract class AddIslandLayerMixin extends Layer {
    public AddIslandLayerMixin(long seedMixup) {
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
                int n1 = p[x + 0 + (y + 0) * pw];
                int n2 = p[x + 2 + (y + 0) * pw];
                int n3 = p[x + 0 + (y + 2) * pw];
                int n4 = p[x + 2 + (y + 2) * pw];
                int c = p[x + 1 + (y + 1) * pw];
                initRandom(x + xo.longValue(), y + yo.longValue());
                if (c != 0 || n1 == 0 && n2 == 0 && n3 == 0 && n4 == 0) {
                    if (c != 1 || n1 == 1 && n2 == 1 && n3 == 1 && n4 == 1) {
                        result[x + y * w] = c;
                    } else {
                        result[x + y * w] = 1 - nextRandom(5) / 4;
                    }
                } else {
                    result[x + y * w] = 0 + nextRandom(3) / 2;
                }
            }
        }

        return result;
    }
}
*///? }