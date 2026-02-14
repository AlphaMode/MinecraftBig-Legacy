package me.alphamode.mcbig.world.phys;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;

public class BigVec3 {
    public static final BigVec3 ZERO = newTemp(BigDecimal.ZERO, 0, BigDecimal.ZERO);
    private static final List<BigVec3> pool = new ArrayList<>();
    private static int poolPointer = 0;
    public BigDecimal x;
    public double y;
    public BigDecimal z;

    public static BigVec3 create(BigDecimal x, double y, BigDecimal z) {
        return new BigVec3(x, y, z);
    }

    public static void clearCache() {
        pool.clear();
        poolPointer = 0;
    }

    public static void resetPool() {
        poolPointer = 0;
    }

    public static BigVec3 newTemp(BigDecimal x, double y, BigDecimal z) {
        if (poolPointer >= pool.size()) {
            pool.add(create(BigDecimal.ZERO, 0.0, BigDecimal.ZERO));
        }

        return pool.get(poolPointer++).set(x, y, z);
    }

    private BigVec3(BigDecimal x, double y, BigDecimal z) {
        if (y == -0.0) {
            y = 0.0;
        }

        this.x = x;
        this.y = y;
        this.z = z;
    }

    private BigVec3 set(BigDecimal x, double y, BigDecimal z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public BigVec3 vectorTo(BigVec3 vec) {
        return newTemp(vec.x.subtract(this.x), vec.y - this.y, vec.z.subtract(this.z));
    }
//
//    public Vec3 normalize() {
//        double d = Mth.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
//        return d < 1.0E-4 ? ZERO : newTemp(this.x / d, this.y / d, this.z / d);
//    }

    public BigVec3 cross(BigVec3 b) {
        BigDecimal bigY = BigDecimal.valueOf(this.y);
        BigDecimal bBigY = BigDecimal.valueOf(b.y);
        return newTemp(bigY.multiply(b.z).subtract(this.z.multiply(bBigY)), this.z.multiply(b.x).subtract(this.x.multiply(b.z)).doubleValue(), this.x.multiply(bBigY).subtract(bigY.multiply(b.x)));
    }

    public BigVec3 add(double xOff, double yOff, double zOff) {
        return newTemp(this.x.add(BigDecimal.valueOf(xOff)), this.y + yOff, this.z.add(BigDecimal.valueOf(zOff)));
    }

    public double distanceTo(BigVec3 to) {
        double d = to.x.subtract(this.x).doubleValue();
        double e = to.y - this.y;
        double f = to.z.subtract(this.z).doubleValue();
        return Mth.sqrt(d * d + e * e + f * f);
    }

    public double distanceToSqr(BigVec3 to) {
        double d = to.x.subtract(this.x).doubleValue();
        double e = to.y - this.y;
        double f = to.z.subtract(this.z).doubleValue();
        return d * d + e * e + f * f;
    }

    public double distanceToSqr(BigDecimal x, double y, BigDecimal z) {
        double d = x.subtract(this.x).doubleValue();
        double e = y - this.y;
        double f = z.subtract(this.z).doubleValue();
        return d * d + e * e + f * f;
    }

    public double length() {
        return Mth.sqrt(this.x.doubleValue() * this.x.doubleValue() + this.y * this.y + this.z.doubleValue() * this.z.doubleValue());
    }

    public BigVec3 clipX(BigVec3 other, double x) {
        double d = other.x.subtract(this.x).doubleValue();
        double e = other.y - this.y;
        double f = other.z.subtract(this.z).doubleValue();
        if (d * d < 1.0E-7F) {
            return null;
        } else {
            double g = (x - this.x.doubleValue()) / d;
            return !(g < 0.0) && !(g > 1.0) ? newTemp(this.x.add(BigDecimal.valueOf(d * g)), this.y + e * g, this.z.add(BigDecimal.valueOf(f * g))) : null;
        }
    }

    public BigVec3 clipY(BigVec3 other, double y) {
        double d = other.x.subtract(this.x).doubleValue();
        double e = other.y - this.y;
        double f = other.z.subtract(this.z).doubleValue();
        if (e * e < 1.0E-7F) {
            return null;
        } else {
            double g = (y - this.y) / e;
            return !(g < 0.0) && !(g > 1.0) ? newTemp(this.x.add(BigDecimal.valueOf(d * g)), this.y + e * g, this.z.add(BigDecimal.valueOf(f * g))) : null;
        }
    }

    public BigVec3 clipZ(BigVec3 other, double z) {
        double d = other.x.subtract(this.x).doubleValue();
        double e = other.y - this.y;
        double f = other.z.subtract(this.z).doubleValue();
        if (f * f < 1.0E-7F) {
            return null;
        } else {
            double g = (z - this.z.doubleValue()) / f;
            return !(g < 0.0) && !(g > 1.0) ? newTemp(this.x.add(BigDecimal.valueOf(d * g)), this.y + e * g, this.z.add(BigDecimal.valueOf(f * g))) : null;
        }
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
}
