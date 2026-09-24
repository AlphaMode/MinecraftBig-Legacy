package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.math.BigMath;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Random;

@Mixin(OreFeature.class)
public abstract class OreFeatureMixin implements BigFeatureExtension {
    @Shadow
    private int count;

    @Shadow
    private int tile;

    @Shadow
    public abstract boolean place(Level level, Random random, int x, int y, int z);

    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        float dir = random.nextFloat() * (float) Math.PI;

        BigDecimal _x = new BigDecimal(x);
        BigDecimal _z = new BigDecimal(z);

        BigDecimal x0 = _x.add(new BigDecimal(8 + Mth.sin(dir) * this.count / 8.0F));
        BigDecimal x1 = _x.add(new BigDecimal(8 - Mth.sin(dir) * this.count / 8.0F));
        BigDecimal z0 = _z.add(new BigDecimal(8 + Mth.cos(dir) * this.count / 8.0F));
        BigDecimal z1 = _z.add(new BigDecimal(8 - Mth.cos(dir) * this.count / 8.0F));

        //~ if >=1.0.0-beta.8.0.r '+ 2' -> '- 2' {
        double y0 = y + random.nextInt(3) + 2;
        double y1 = y + random.nextInt(3) + 2;
        //~ }

        for (int d = 0; d <= this.count; d++) {
            BigDecimal xx = x0.add(new BigDecimal((x1.subtract(x0).doubleValue()) * d / this.count));
            double yy = y0 + (y1 - y0) * d / this.count;
            BigDecimal zz = z0.add(new BigDecimal((z1.subtract(z0).doubleValue()) * d / this.count));

            double ss = random.nextDouble() * this.count / 16.0;
            double r = (Mth.sin(d * (float) Math.PI / this.count) + 1.0F) * ss + 1.0;
            double hr = (Mth.sin(d * (float) Math.PI / this.count) + 1.0F) * ss + 1.0;
            BigDecimal _r = new BigDecimal(r / 2);

            var xt0 = BigMath.floor(xx.subtract(_r));
            int yt0 = Mth.floor(yy - hr / 2.0);
            var zt0 = BigMath.floor(zz.subtract(_r));

            var xt1 = BigMath.floor(xx.add(_r));
            int yt1 = Mth.floor(yy + hr / 2.0);
            var zt1 = BigMath.floor(zz.add(_r));

            for (var x2 = xt0; x2.compareTo(xt1) <= 0; x2 = x2.add(BigInteger.ONE)) {
                double xd = (new BigDecimal(x2).add(BigConstants.POINT_FIVE).subtract(xx)).doubleValue() / (r / 2);
                if (xd * xd < 1.0) {
                    for (int y2 = yt0; y2 <= yt1; y2++) {
                        double yd = (y2 + 0.5 - yy) / (hr / 2.0);
                        if (xd * xd + yd * yd < 1.0) {
                            for (var z2 = zt0; z2.compareTo(zt1) <= 0; z2 = z2.add(BigInteger.ONE)) {
                                double zd = (new BigDecimal(z2).add(BigConstants.POINT_FIVE).subtract(zz)).doubleValue() / (r / 2.0);
                                if (xd * xd + yd * yd + zd * zd < 1.0 && level.getTile(x2, y2, z2) == Tile.stone.id) {
                                    level.setTileNoUpdate(x2, y2, z2, this.tile);
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
