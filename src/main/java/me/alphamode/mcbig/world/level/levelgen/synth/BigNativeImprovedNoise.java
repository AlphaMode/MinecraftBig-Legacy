package me.alphamode.mcbig.world.level.levelgen.synth;

import me.alphamode.mcbig.core.NBigDec;
import me.alphamode.mcbig.core.NBigInt;
import me.alphamode.mcbig.math.BigNConstants;
import me.alphamode.mcbig.math.BigNMath;

import java.util.Random;

public class BigNativeImprovedNoise {
    private int[] p = new int[512];
    public final NBigDec xo;
    public final NBigDec yo;
    public final NBigDec zo;

    public BigNativeImprovedNoise() {
        this(new Random());
    }

    public BigNativeImprovedNoise(Random random) {
        this.xo = BigNMath.decimal(random.nextDouble() * 256.0);
        this.yo = BigNMath.decimal(random.nextDouble() * 256.0);
        this.zo = BigNMath.decimal(random.nextDouble() * 256.0);

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

    public double noise(NBigDec _x, NBigDec _y, NBigDec _z) {
        NBigDec x = _x.add(this.xo);
        NBigDec y = _y.add(this.yo);
        NBigDec z = _z.add(this.zo);

        NBigInt xf = x.toBigInteger();
        NBigInt yf = y.toBigInteger();
        NBigInt zf = z.toBigInteger();

        if (x.compareTo(new NBigDec(xf)) < 0) xf = xf.subtract(NBigInt.ONE) ;

        if (y.compareTo(new NBigDec(yf)) < 0) yf = yf.subtract(NBigInt.ONE);

        if (z.compareTo(new NBigDec(zf)) < 0) zf = zf.subtract(NBigInt.ONE);

        int X = xf.and(BigNConstants.NOISE_MASK).intValue(); // FIND UNIT CUBE THAT
        int Y = yf.and(BigNConstants.NOISE_MASK).intValue(); // CONTAINS POINT.
        int Z = zf.and(BigNConstants.NOISE_MASK).intValue();

        double xR = x.subtract(BigNMath.decimalW(xf)).doubleValue(); // FIND RELATIVE X,Y,Z
        double yR = y.subtract(BigNMath.decimalW(yf)).doubleValue(); // OF POINT IN CUBE.
        double zR = z.subtract(BigNMath.decimalW(zf)).doubleValue();

        double u = xR * xR * xR * (xR * (xR * 6.0 - 15.0) + 10.0); // COMPUTE FADE CURVES
        double v = yR * yR * yR * (yR * (yR * 6.0 - 15.0) + 10.0); // FOR EACH OF X,Y,Z.
        double w = zR * zR * zR * (zR * (zR * 6.0 - 15.0) + 10.0);

        int A = this.p[X] + Y, AA = this.p[A] + Z, AB = this.p[A + 1] + Z, // HASH COORDINATES OF
                B = this.p[X + 1] + Y, BA = this.p[B] + Z, BB = this.p[B + 1] + Z; // THE 8 CUBE CORNERS,

        return lerp(w, lerp(v, lerp(u, grad(this.p[AA], xR, yR, zR), // AND ADD
                                grad(this.p[BA], xR - 1.0, yR, zR)), // BLENDED
                        lerp(u, grad(this.p[AB], xR, yR - 1.0, zR), // RESULTS
                                grad(this.p[BB], xR - 1.0, yR - 1.0, zR))), // FROM  8
                lerp(v, lerp(u, grad(this.p[AA + 1], xR, yR, zR - 1.0), // CORNERS
                                grad(this.p[BA + 1], xR - 1.0, yR, zR - 1.0)), // OF CUBE
                        lerp(u, grad(this.p[AB + 1], xR, yR - 1.0, zR - 1.0), grad(this.p[BB + 1], xR - 1.0, yR - 1.0, zR - 1.0))
                )
        );
    }

    public final double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    public final double grad2(int hash, double x, double z) {
        int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE

        double u = (1 - ((h & 8) >> 3)) * x, // INTO 12 GRADIENT DIRECTIONS.
                v = h < 4 ? 0.0 : (h != 12 && h != 14 ? z : x);

        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    public final double grad(int hash, double x, double y, double z) {
        int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE

        double u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
                v = h < 4 ? y : (h != 12 && h != 14 ? z : x);

        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    public double getValue(NBigDec x, NBigDec y) {
        return this.noise(x, y, NBigDec.ZERO);
    }

    public double getValue(NBigDec x, NBigDec y, NBigDec z) {
        return noise(x, y, z);
    }

    public void add(double[] buffer, final NBigDec _x, final NBigDec _y, final NBigDec _z, int xSize, int ySize, int zSize, final double _xs, final double _ys, final double _zs, double pow) {
        final NBigDec xs = BigNMath.decimal(_xs);
        final NBigDec ys = BigNMath.decimal(_ys);
        final NBigDec zs = BigNMath.decimal(_zs);
        if (ySize == 1) {
            int A = 0, AA = 0, B = 0, BA = 0;
            double vv0 = 0.0, vv2 = 0.0;
            int pp = 0;
            double scale = 1.0 / pow;

            for (int xx = 0; xx < xSize; xx++) {
                NBigDec x = (_x.add(BigNMath.decimal(xx))).multiply(xs).add(this.xo);
                NBigInt xf = x.toBigInteger();
                if (x.compareTo(new NBigDec(xf)) < 0) xf = xf.subtract(NBigInt.ONE);

                int X = xf.and(BigNConstants.NOISE_MASK).intValue();
                double xR = x.subtract(BigNMath.decimalW(xf)).doubleValue();
                double u = xR * xR * xR * (xR * (xR * 6.0 - 15.0) + 10.0);

                for (int zz = 0; zz < zSize; zz++) {
                    NBigDec z = (_z.add(BigNMath.decimal(zz))).multiply(zs).add(this.zo);
                    NBigInt zf = z.toBigInteger();
                    if (z.compareTo(new NBigDec(zf)) < 0) zf = zf.subtract(NBigInt.ONE);

                    int Z = zf.and(BigNConstants.NOISE_MASK).intValue();
                    double zR = z.subtract(BigNMath.decimalW(zf)).doubleValue();
                    double w = zR * zR * zR * (zR * (zR * 6.0 - 15.0) + 10.0);
                    A = this.p[X] + 0;
                    AA = this.p[A] + Z;
                    B = this.p[X + 1] + 0;
                    BA = this.p[B] + Z;
                    vv0 = this.lerp(u, this.grad2(this.p[AA], xR, zR), this.grad(this.p[BA], xR - 1.0, 0.0, zR));
                    vv2 = this.lerp(u, this.grad(this.p[AA + 1], xR, 0.0, zR - 1.0), this.grad(this.p[BA + 1], xR - 1.0, 0.0, zR - 1.0));
                    double val = this.lerp(w, vv0, vv2);
                    buffer[pp++] += val * scale;
                }
            }
        } else {
            int pp = 0;
            double scale = 1.0 / pow;
            int yOld = -1;
            int A = 0;
            int AA = 0;
            int AB = 0;
            int B = 0;
            int BA = 0;
            int BB = 0;
            double vv0 = 0.0;
            double vv1 = 0.0;
            double vv2 = 0.0;
            double vv3 = 0.0;

            for (int xx = 0; xx < xSize; xx++) {
                NBigDec x = (_x.add(BigNMath.decimal(xx))).multiply(xs).add(this.xo);
                NBigInt xf = x.toBigInteger();
                if (x.compareTo(new NBigDec(xf)) < 0) xf = xf.subtract(NBigInt.ONE);

                int X = xf.and(BigNConstants.NOISE_MASK).intValue();
                double xR = x.subtract(BigNMath.decimalW(xf)).doubleValue();
                double u = xR * xR * xR * (xR * (xR * 6.0 - 15.0) + 10.0);

                for (int zz = 0; zz < zSize; zz++) {
                    NBigDec z = (_z.add(BigNMath.decimal(zz))).multiply(zs).add(this.zo);
                    NBigInt zf = z.toBigInteger();
                    if (z.compareTo(new NBigDec(zf)) < 0) zf = zf.subtract(NBigInt.ONE);

                    int Z = zf.and(BigNConstants.NOISE_MASK).intValue();
                    double zR = z.subtract(BigNMath.decimalW(zf)).doubleValue();
                    double w = zR * zR * zR * (zR * (zR * 6.0 - 15.0) + 10.0);

                    for (int yy = 0; yy < ySize; yy++) {
                        NBigDec y = (_y.add(BigNMath.decimal(yy))).multiply(ys).add(this.yo);
                        NBigInt yf = y.toBigInteger();
                        if (y.compareTo(new NBigDec(yf)) < 0) yf = yf.subtract(NBigInt.ONE);

                        int Y = yf.and(BigNConstants.NOISE_MASK).intValue();
                        double yR = y.subtract(BigNMath.decimalW(yf)).doubleValue();
                        double v = yR * yR * yR * (yR * (yR * 6.0 - 15.0) + 10.0);
                        if (yy == 0 || Y != yOld) {
                            yOld = Y;
                            A = this.p[X] + Y;
                            AA = this.p[A] + Z;
                            AB = this.p[A + 1] + Z;
                            B = this.p[X + 1] + Y;
                            BA = this.p[B] + Z;
                            BB = this.p[B + 1] + Z;
                            vv0 = lerp(u, grad(this.p[AA], xR, yR, zR), grad(this.p[BA], xR - 1.0, yR, zR));
                            vv1 = lerp(u, grad(this.p[AB], xR, yR - 1.0, zR), grad(this.p[BB], xR - 1.0, yR - 1.0, zR));
                            vv2 = lerp(u, grad(this.p[AA + 1], xR, yR, zR - 1.0), grad(this.p[BA + 1], xR - 1.0, yR, zR - 1.0));
                            vv3 = this.lerp(
                                    u, this.grad(this.p[AB + 1], xR, yR - 1.0, zR - 1.0), grad(this.p[BB + 1], xR - 1.0, yR - 1.0, zR - 1.0)
                            );
                        }

                        double v0 = lerp(v, vv0, vv1);
                        double v1 = lerp(v, vv2, vv3);
                        double val = lerp(w, v0, v1);

                        buffer[pp++] += val * scale;
                    }
                }
            }
        }
    }
}
