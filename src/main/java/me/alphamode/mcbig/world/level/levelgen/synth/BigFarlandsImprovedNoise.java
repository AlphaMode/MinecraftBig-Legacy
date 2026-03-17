package me.alphamode.mcbig.world.level.levelgen.synth;

import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.math.BigMath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

public class BigFarlandsImprovedNoise {
    private int[] p = new int[512];
    public final BigDecimal xo;
    public final BigDecimal yo;
    public final BigDecimal zo;

    public BigFarlandsImprovedNoise() {
        this(new Random());
    }

    public BigFarlandsImprovedNoise(Random random) {
        this.xo = BigMath.decimal(random.nextDouble() * 256.0);
        this.yo = BigMath.decimal(random.nextDouble() * 256.0);
        this.zo = BigMath.decimal(random.nextDouble() * 256.0);

        for (int i = 0; i < 256; i++) {
            p[i] = i;
        }

        for (int i = 0; i < 256; i++) {
            int j = random.nextInt(256 - i) + i;
            int tmp = this.p[i];
            this.p[i] = this.p[j];
            this.p[j] = tmp;
            this.p[i + 256] = this.p[i];
        }
    }

    public BigDecimal noise(BigDecimal _x, BigDecimal _y, BigDecimal _z) {
        BigDecimal x = _x.add(this.xo);
        BigDecimal y = _y.add(this.yo);
        BigDecimal z = _z.add(this.zo);

        BigInteger xf = BigInteger.valueOf(x.intValue());
        BigInteger yf = BigInteger.valueOf(y.intValue());
        BigInteger zf = BigInteger.valueOf(z.intValue());

        if (x.compareTo(new BigDecimal(xf)) < 0) xf = xf.subtract(BigInteger.ONE) ;

        if (y.compareTo(new BigDecimal(yf)) < 0) yf = yf.subtract(BigInteger.ONE);

        if (z.compareTo(new BigDecimal(zf)) < 0) zf = zf.subtract(BigInteger.ONE);

        int X = xf.and(BigConstants.NOISE_MASK).intValue(); // FIND UNIT CUBE THAT
        int Y = yf.and(BigConstants.NOISE_MASK).intValue(); // CONTAINS POINT.
        int Z = zf.and(BigConstants.NOISE_MASK).intValue();

        BigDecimal xR = x.subtract(BigMath.decimalW(xf)); // FIND RELATIVE X,Y,Z
        BigDecimal yR = y.subtract(BigMath.decimalW(yf)); // OF POINT IN CUBE.
        BigDecimal zR = z.subtract(BigMath.decimalW(zf));

        BigDecimal u = xR.multiply(xR).multiply(xR).multiply((xR.multiply((xR.multiply(BigConstants.SIX_F).subtract(BigConstants.FIFTEEN_F))).add(BigDecimal.TEN))); // COMPUTE FADE CURVES
        BigDecimal v = yR.multiply(yR).multiply(yR).multiply((yR.multiply((yR.multiply(BigConstants.SIX_F).subtract(BigConstants.FIFTEEN_F))).add(BigDecimal.TEN))); // FOR EACH OF X,Y,Z.
        BigDecimal w = zR.multiply(zR).multiply(zR).multiply((zR.multiply((zR.multiply(BigConstants.SIX_F).subtract(BigConstants.FIFTEEN_F))).add(BigDecimal.TEN)));

        int A = this.p[X] + Y, AA = this.p[A] + Z, AB = this.p[A + 1] + Z, // HASH COORDINATES OF
        B = this.p[X + 1] + Y, BA = this.p[B] + Z, BB = this.p[B + 1] + Z; // THE 8 CUBE CORNERS,

        return lerp(w, lerp(v, lerp(u, grad(this.p[AA], xR, yR, zR), // AND ADD
                    grad(this.p[BA], xR.subtract(BigDecimal.ONE), yR, zR)), // BLENDED
                    lerp(u, grad(this.p[AB], xR, yR.subtract(BigDecimal.ONE), zR), // RESULTS
                            grad(this.p[BB], xR.subtract(BigDecimal.ONE), yR.subtract(BigDecimal.ONE), zR))), // FROM  8
                    lerp(v, lerp(u, grad(this.p[AA + 1], xR, yR, zR.subtract(BigDecimal.ONE)), // CORNERS
                            grad(this.p[BA + 1], xR.subtract(BigDecimal.ONE), yR, zR.subtract(BigDecimal.ONE))), // OF CUBE
                            lerp(u, grad(this.p[AB + 1], xR, yR.subtract(BigDecimal.ONE), zR.subtract(BigDecimal.ONE)), grad(this.p[BB + 1], xR.subtract(BigDecimal.ONE), yR.subtract(BigDecimal.ONE), zR.subtract(BigDecimal.ONE)))
                )
        );
    }

    public final BigDecimal lerp(BigDecimal t, BigDecimal a, BigDecimal b) {
        return a.add(t.multiply(b.subtract(a)));
    }

    public final BigDecimal grad2(int hash, BigDecimal x, BigDecimal z) {
        int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE

        BigDecimal u = new BigDecimal((1 - ((h & 8) >> 3))).multiply(x), // INTO 12 GRADIENT DIRECTIONS.
        v = h < 4 ? BigDecimal.ZERO : (h != 12 && h != 14 ? z : x);

        return ((h & 1) == 0 ? u : u.negate()).add(((h & 2) == 0 ? v : v.negate()));
    }

    public final BigDecimal grad(int hash, BigDecimal x, BigDecimal y, BigDecimal z) {
        int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE

        BigDecimal u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
        v = h < 4 ? y : (h != 12 && h != 14 ? z : x);

        return ((h & 1) == 0 ? u : u.negate()).add(((h & 2) == 0 ? v : v.negate()));
    }

    public BigDecimal getValue(BigDecimal x, BigDecimal y) {
        return this.noise(x, y, BigDecimal.ZERO);
    }

    public BigDecimal getValue(BigDecimal x, BigDecimal y, BigDecimal z) {
        return noise(x, y, z);
    }

    public void add(BigDecimal[] buffer, final BigDecimal _x, final BigDecimal _y, final BigDecimal _z, int xSize, int ySize, int zSize, final double _xs, final double _ys, final double _zs, double pow) {
        final BigDecimal xs = BigMath.decimal(_xs);
        final BigDecimal ys = BigMath.decimal(_ys);
        final BigDecimal zs = BigMath.decimal(_zs);
        if (ySize == 1) {
            int A = 0, AA = 0, B = 0, BA = 0;
            BigDecimal vv0 = BigDecimal.ZERO, vv2 = BigDecimal.ZERO;
            int pp = 0;
            BigDecimal scale = new BigDecimal(1.0 / pow);

            for (int xx = 0; xx < xSize; xx++) {
                BigDecimal x = (_x.add(BigMath.decimal(xx))).multiply(xs).add(this.xo);
                BigInteger xf = BigInteger.valueOf((int) x.doubleValue());
                if (x.compareTo(new BigDecimal(xf)) < 0) xf = xf.subtract(BigInteger.ONE);

                int X = xf.and(BigConstants.NOISE_MASK).intValue();
                BigDecimal xR = x.subtract(BigMath.decimalW(xf));
                BigDecimal u = xR.multiply(xR).multiply(xR).multiply((xR.multiply((xR.multiply(BigConstants.SIX_F).subtract(BigConstants.FIFTEEN_F))).add(BigDecimal.TEN)));

                for (int zz = 0; zz < zSize; zz++) {
                    BigDecimal z = (_z.add(BigMath.decimal(zz))).multiply(zs).add(this.zo);
                    BigInteger zf = BigInteger.valueOf((int) z.doubleValue());
                    if (z.compareTo(new BigDecimal(zf)) < 0) zf = zf.subtract(BigInteger.ONE);

                    int Z = zf.and(BigConstants.NOISE_MASK).intValue();
                    BigDecimal zR = z.subtract(BigMath.decimalW(zf));
                    BigDecimal w = zR.multiply(zR).multiply(zR).multiply((zR.multiply((zR.multiply(BigConstants.SIX_F).subtract(BigConstants.FIFTEEN_F))).add(BigDecimal.TEN)));
                    A = this.p[X] + 0;
                    AA = this.p[A] + Z;
                    B = this.p[X + 1] + 0;
                    BA = this.p[B] + Z;
                    vv0 = this.lerp(u, this.grad2(this.p[AA], xR, zR), this.grad(this.p[BA], xR.subtract(BigDecimal.ONE), BigDecimal.ZERO, zR));
                    vv2 = this.lerp(u, this.grad(this.p[AA + 1], xR, BigDecimal.ZERO, zR.subtract(BigDecimal.ONE)), this.grad(this.p[BA + 1], xR.subtract(BigDecimal.ONE), BigDecimal.ZERO, zR.subtract(BigDecimal.ONE)));
                    BigDecimal val = this.lerp(w, vv0, vv2);
                    buffer[pp] = buffer[pp].add(val.multiply(scale));
                    pp++;
                }
            }
        } else {
            int pp = 0;
            BigDecimal scale = new BigDecimal(1.0 / pow);
            int yOld = -1;
            int A = 0;
            int AA = 0;
            int AB = 0;
            int B = 0;
            int BA = 0;
            int BB = 0;
            BigDecimal vv0 = BigDecimal.ZERO;
            BigDecimal vv1 = BigDecimal.ZERO;
            BigDecimal vv2 = BigDecimal.ZERO;
            BigDecimal vv3 = BigDecimal.ZERO;

            for (int xx = 0; xx < xSize; xx++) {
                BigDecimal x = (_x.add(BigMath.decimal(xx))).multiply(xs).add(this.xo);
                BigInteger xf = BigInteger.valueOf((int) x.doubleValue());
                if (x.compareTo(new BigDecimal(xf)) < 0) xf = xf.subtract(BigInteger.ONE);

                int X = xf.and(BigConstants.NOISE_MASK).intValue();
                BigDecimal xR = x.subtract(BigMath.decimalW(xf));
                BigDecimal u = xR.multiply(xR).multiply(xR).multiply((xR.multiply((xR.multiply(BigConstants.SIX_F).subtract(BigConstants.FIFTEEN_F))).add(BigDecimal.TEN)));

                for (int zz = 0; zz < zSize; zz++) {
                    BigDecimal z = (_z.add(BigMath.decimal(zz))).multiply(zs).add(this.zo);
                    BigInteger zf = BigInteger.valueOf((int) z.doubleValue());
                    if (z.compareTo(new BigDecimal(zf)) < 0) zf = zf.subtract(BigInteger.ONE);

                    int Z = zf.and(BigConstants.NOISE_MASK).intValue();
                    BigDecimal zR = z.subtract(BigMath.decimalW(zf));
                    BigDecimal w = zR.multiply(zR).multiply(zR).multiply((zR.multiply((zR.multiply(BigConstants.SIX_F).subtract(BigConstants.FIFTEEN_F))).add(BigDecimal.TEN)));

                    for (int yy = 0; yy < ySize; yy++) {
                        BigDecimal y = (_y.add(BigMath.decimal(yy))).multiply(ys).add(this.yo);
                        BigInteger yf = BigInteger.valueOf((int) y.doubleValue());
                        if (y.compareTo(new BigDecimal(yf)) < 0) yf = yf.subtract(BigInteger.ONE);

                        int Y = yf.and(BigConstants.NOISE_MASK).intValue();
                        BigDecimal yR = y.subtract(BigMath.decimalW(yf));
                        BigDecimal v = yR.multiply(yR).multiply(yR).multiply((yR.multiply((yR.multiply(BigConstants.SIX_F).subtract(BigConstants.FIFTEEN_F))).add(BigDecimal.TEN)));
                        if (yy == 0 || Y != yOld) {
                            yOld = Y;
                            A = this.p[X] + Y;
                            AA = this.p[A] + Z;
                            AB = this.p[A + 1] + Z;
                            B = this.p[X + 1] + Y;
                            BA = this.p[B] + Z;
                            BB = this.p[B + 1] + Z;
                            vv0 = lerp(u, grad(this.p[AA], xR, yR, zR), grad(this.p[BA], xR.subtract(BigDecimal.ONE), yR, zR));
                            vv1 = lerp(u, grad(this.p[AB], xR, yR.subtract(BigDecimal.ONE), zR), grad(this.p[BB], xR.subtract(BigDecimal.ONE), yR.subtract(BigDecimal.ONE), zR));
                            vv2 = lerp(u, grad(this.p[AA + 1], xR, yR, zR.subtract(BigDecimal.ONE)), grad(this.p[BA + 1], xR.subtract(BigDecimal.ONE), yR, zR.subtract(BigDecimal.ONE)));
                            vv3 = this.lerp(
                                    u, this.grad(this.p[AB + 1], xR, yR.subtract(BigDecimal.ONE), zR.subtract(BigDecimal.ONE)), grad(this.p[BB + 1], xR.subtract(BigDecimal.ONE), yR.subtract(BigDecimal.ONE), zR.subtract(BigDecimal.ONE))
                            );
                        }

                        BigDecimal v0 = lerp(v, vv0, vv1);
                        BigDecimal v1 = lerp(v, vv2, vv3);
                        BigDecimal val = lerp(w, v0, v1);

                        buffer[pp] = buffer[pp].add(val.multiply(scale));
                        pp++;
                    }
                }
            }
        }
    }
}
