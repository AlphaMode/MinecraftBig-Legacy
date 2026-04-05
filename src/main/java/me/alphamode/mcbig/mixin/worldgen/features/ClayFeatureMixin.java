package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.math.BigMath;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ClayFeature;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Random;

@Mixin(ClayFeature.class)
public class ClayFeatureMixin implements BigFeatureExtension {
    @Shadow
    private int radius;

    @Shadow
    private int tile;

    private BigDecimal bigRadius;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void makeBigRadius(int par1, CallbackInfo ci) {
        this.bigRadius = BigDecimal.valueOf(this.radius);
    }

    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        if (level.getMaterial(x, y, z) != Material.water) return false;

        float dir = random.nextFloat() * (float) Math.PI;

        BigDecimal x0 = new BigDecimal(x.add(BigConstants.EIGHT)).add(BigDecimal.valueOf(Mth.sin(dir) * this.radius / 8.0F));
        BigDecimal x1 = new BigDecimal(x.add(BigConstants.EIGHT)).subtract(BigDecimal.valueOf(Mth.sin(dir) * this.radius / 8.0F));
        BigDecimal z0 = new BigDecimal(z.add(BigConstants.EIGHT)).add(BigDecimal.valueOf(Mth.cos(dir) * this.radius / 8.0F));
        BigDecimal z1 = new BigDecimal(z.add(BigConstants.EIGHT)).subtract(BigDecimal.valueOf(Mth.cos(dir) * this.radius / 8.0F));

        double y0 = y + random.nextInt(3) + 2;
        double y1 = y + random.nextInt(3) + 2;

        for (int d = 0; d <= this.radius; d++) {
            BigDecimal dBig = BigDecimal.valueOf(d);
            BigDecimal xx = x0.add((x1.subtract(x0)).multiply(dBig).divide(this.bigRadius, MathContext.DECIMAL64));
            double yy = y0 + (y1 - y0) * d / this.radius;
            BigDecimal zz = z0.add((z1.subtract(z0)).multiply(dBig).divide(this.bigRadius, MathContext.DECIMAL64));

            double ss = random.nextDouble() * this.radius / 16.0;
            double r = (Mth.sin(d * (float) Math.PI / this.radius) + 1.0F) * ss + 1.0;
            double hr = (Mth.sin(d * (float) Math.PI / this.radius) + 1.0F) * ss + 1.0;

            BigInteger xt0 = BigMath.floor(xx.subtract(BigDecimal.valueOf(r / 2.0)));
            BigInteger xt1 = BigMath.floor(xx.add(BigDecimal.valueOf(r / 2.0)));
            int yt0 = Mth.floor(yy - hr / 2.0);
            int yt1 = Mth.floor(yy + hr / 2.0);
            BigInteger zt0 = BigMath.floor(zz.subtract(BigDecimal.valueOf(r / 2.0)));
            BigInteger zt1 = BigMath.floor(zz.add(BigDecimal.valueOf(r / 2.0)));

            for (BigInteger x2 = xt0; x2.compareTo(xt1) <= 0; x2 = x2.add(BigInteger.ONE)) {
                for (int yt = yt0; yt <= yt1; yt++) {
                    for (BigInteger zt = zt0; zt.compareTo(zt1) <= 0; zt = zt.add(BigInteger.ONE)) {
                        double xd = (new BigDecimal(x2).add(BigConstants.POINT_FIVE).subtract(xx)).divide(BigDecimal.valueOf(r / 2.0), MathContext.DECIMAL64).doubleValue();
                        double yd = (yt + 0.5 - yy) / (hr / 2.0);
                        double zd = (new BigDecimal(zt).add(BigConstants.POINT_FIVE).subtract(zz)).divide(BigDecimal.valueOf(r / 2.0), MathContext.DECIMAL64).doubleValue();
                        if (xd * xd + yd * yd + zd * zd < 1) {
                            int t = level.getTile(x2, yt, zt);
                            if (t == Tile.sand.id) level.setTileNoUpdate(x2, yt, zt, this.tile);
                        }
                    }
                }
            }
        }

        return true;
    }
}
