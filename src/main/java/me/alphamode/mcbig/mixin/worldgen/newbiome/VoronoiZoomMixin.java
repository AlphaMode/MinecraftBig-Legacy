//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.worldgen.newbiome;

import me.alphamode.mcbig.math.BigMath;
import net.minecraft.util.IntCache;
import net.minecraft.world.level.newbiome.layer.Layer;
import net.minecraft.world.level.newbiome.layer.VoronoiZoom;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(VoronoiZoom.class)
public abstract class VoronoiZoomMixin extends Layer {
    public VoronoiZoomMixin(long seedMixup) {
        super(seedMixup);
    }

    @Override
    public int[] getArea(BigInteger xo, BigInteger yo, int w, int h) {
        xo = xo.subtract(BigInteger.TWO);
        yo = yo.subtract(BigInteger.TWO);
        int bits = 2;
        int ss = 1 << bits;
        var px = xo.shiftRight(bits);
        var py = yo.shiftRight(bits);
        int pw = (w >> bits) + 3;
        int ph = (h >> bits) + 3;
        int[] p = this.parent.getArea(px, py, pw, ph);
        
        int ww = pw << bits;
        int hh = ph << bits;
        int[] tmp = IntCache.allocate(ww * hh);
        for (int y = 0; y < ph - 1; y++) {
            int ul = p[0 + (y + 0) * pw];
            int dl = p[0 + (y + 1) * pw];
            for (int x = 0; x < pw - 1; x++) {
                double s = ss * 0.9;
                initRandom(x + px.longValue() << bits, y + py.longValue() << bits);
                double x0 = (nextRandom(1024) / 1024.0 - 0.5) * s;
                double y0 = (nextRandom(1024) / 1024.0 - 0.5) * s;
                initRandom(x + px.longValue() + 1 << bits, y + py.longValue() << bits);
                double x1 = (nextRandom(1024) / 1024.0 - 0.5) * s + ss;
                double y1 = (nextRandom(1024) / 1024.0 - 0.5) * s;
                this.initRandom(x + px.longValue() << bits, y + py.longValue() + 1 << bits);
                double x2 = (nextRandom(1024) / 1024.0 - 0.5) * s;
                double y2 = (nextRandom(1024) / 1024.0 - 0.5) * s + ss;
                this.initRandom(x + px.longValue() + 1 << bits, y + py.longValue() + 1 << bits);
                double x3 = (nextRandom(1024) / 1024.0 - 0.5) * s + ss;
                double y3 = (nextRandom(1024) / 1024.0 - 0.5) * s + ss;

                int ur = p[x + 1 + (y + 0) * pw];
                int dr = p[x + 1 + (y + 1) * pw];

                for (int yy = 0; yy < ss; yy++) {
                    int pp = ((y << bits) + yy) * ww + (x << bits);
                    for (int xx = 0; xx < ss; xx++) {
                        double d0 = (yy - y0) * (yy - y0) + (xx - x0) * (xx - x0);
                        double d1 = (yy - y1) * (yy - y1) + (xx - x1) * (xx - x1);
                        double d2 = (yy - y2) * (yy - y2) + (xx - x2) * (xx - x2);
                        double d3 = (yy - y3) * (yy - y3) + (xx - x3) * (xx - x3);
                        if (d0 < d1 && d0 < d2 && d0 < d3) {
                            tmp[pp++] = ul;
                        } else if (d1 < d0 && d1 < d2 && d1 < d3) {
                            tmp[pp++] = ur;
                        } else if (d2 < d0 && d2 < d1 && d2 < d3) {
                            tmp[pp++] = dl;
                        } else {
                            tmp[pp++] = dr;
                        }
                    }
                }

                ul = ur;
                dl = dr;
            }
        }
        int[] result = IntCache.allocate(w * h);
        for (int y = 0; y < h; y++) {
            System.arraycopy(tmp, (y + (BigMath.fastAnd(yo, ss - 1))) * (pw << bits) + (BigMath.fastAnd(xo, ss - 1)), result, y * w, w);
        }

        return result;
    }
}
*///? }