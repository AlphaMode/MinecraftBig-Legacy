package me.alphamode.mcbig.world.phys;

import me.alphamode.mcbig.math.BigMath;
import net.minecraft.world.phys.AABB;

import java.math.BigDecimal;

public class BigAABB {
    public BigDecimal x0;
    public double y0;
    public BigDecimal z0;
    public BigDecimal x1;
    public double y1;
    public BigDecimal z1;

    public static BigAABB create(BigDecimal x0, double y0, BigDecimal z0, BigDecimal x1, double y1, BigDecimal z1) {
        return new BigAABB(x0, y0, z0, x1, y1, z1);
    }

    public BigAABB(BigDecimal minX, double minY, BigDecimal minZ, BigDecimal maxX, double maxY, BigDecimal maxZ) {
        this.x0 = minX;
        this.y0 = minY;
        this.z0 = minZ;
        this.x1 = maxX;
        this.y1 = maxY;
        this.z1 = maxZ;
    }

    public BigAABB copy() {
        return new BigAABB(this.x0, this.y0, this.z0, this.x1, this.y1, this.z1);
    }

    public BigAABB set(BigDecimal x0, double y0, BigDecimal z0, BigDecimal x1, double y1, BigDecimal z1) {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        return this;
    }

    public static boolean USE_VANILLA = false;

    public double clipXCollide(BigAABB c, double xa) {
        if (USE_VANILLA) {
            return toVanilla().clipXCollide(c.toVanilla(), xa);
        }
        if (c.y1 <= this.y0 || c.y0 >= this.y1) {
            return xa;
        } else if (!(c.z1.compareTo(this.z0) <= 0) && !(c.z0.compareTo(this.z1) >= 0)) {
            if (xa > 0.0 && c.x1.compareTo(this.x0) <= 0) {
                double var4 = BigMath.subD(this.x0, c.x1).doubleValue();
                if (var4 < xa) {
                    xa = var4;
                }
            }

            if (xa < 0.0 && c.x0.compareTo(this.x1) >= 0) {
                double var6 = BigMath.subD(this.x1, c.x0).doubleValue();
                if (var6 > xa) {
                    xa = var6;
                }
            }

            return xa;
        } else {
            return xa;
        }
    }

    public double clipYCollide(BigAABB c, double ya) {
        if (USE_VANILLA) {
            return toVanilla().clipYCollide(c.toVanilla(), ya);
        }
        if (c.x1.compareTo(this.x0) <= 0 || c.x0.compareTo(this.x1) >= 0) {
            return ya;
        } else if (!(c.z1.compareTo(this.z0) <= 0) && !(c.z0.compareTo(this.z1) >= 0)) {
            if (ya > 0.0 && c.y1 <= this.y0) {
                double var4 = this.y0 - c.y1;
                if (var4 < ya) {
                    ya = var4;
                }
            }

            if (ya < 0.0 && c.y0 >= this.y1) {
                double var6 = this.y1 - c.y0;
                if (var6 > ya) {
                    ya = var6;
                }
            }

            return ya;
        } else {
            return ya;
        }
    }

    public double clipZCollide(BigAABB c, double za) {
        if (USE_VANILLA) {
            return toVanilla().clipZCollide(c.toVanilla(), za);
        }
        if (c.x1.compareTo(this.x0) <= 0 || c.x0.compareTo(this.x1) >= 0) {
            return za;
        } else if (!(c.y1 <= this.y0) && !(c.y0 >= this.y1)) {
            if (za > 0.0 && c.z1.compareTo(this.z0) <= 0) {
                double var4 = BigMath.subD(this.z0, c.z1).doubleValue();
                if (var4 < za) {
                    za = var4;
                }
            }

            if (za < 0.0 && c.z0.compareTo(this.z1) >= 0) {
                double var6 = BigMath.subD(this.z1, c.z0).doubleValue();
                if (var6 > za) {
                    za = var6;
                }
            }

            return za;
        } else {
            return za;
        }
    }

    public BigAABB expand(double x, double y, double z) {
        BigDecimal bigX = BigMath.decimal(x);
        BigDecimal bigZ = BigMath.decimal(z);
        BigDecimal var7 = this.x0;
        double var9 = this.y0;
        BigDecimal var11 = this.z0;
        BigDecimal var13 = this.x1;
        double var15 = this.y1;
        BigDecimal var17 = this.z1;
        if (x < 0.0) {
            var7 = BigMath.addD(var7, bigX);
        }

        if (x > 0.0) {
            var13 = BigMath.addD(var13, bigX);
        }

        if (y < 0.0) {
            var9 += y;
        }

        if (y > 0.0) {
            var15 += y;
        }

        if (z < 0.0) {
            var11 = BigMath.addD(var11, bigZ);
        }

        if (z > 0.0) {
            var17 = BigMath.addD(var17, bigZ);
        }

        return create(var7, var9, var11, var13, var15, var17);
    }

    public BigAABB inflate(double x, double y, double z) {
        BigDecimal bigX = BigMath.decimal(x);
        BigDecimal bigZ = BigMath.decimal(z);
        BigDecimal x0 = this.x0.subtract(bigX, BigMath.CONTEXT);
        double y0 = this.y0 - y;
        BigDecimal z0 = this.z0.subtract(bigZ, BigMath.CONTEXT);
        BigDecimal x1 = this.x1.add(bigX, BigMath.CONTEXT);
        double y1 = this.y1 + y;
        BigDecimal z1 = this.z1.add(bigZ, BigMath.CONTEXT);
        return create(x0, y0, z0, x1, y1, z1);
    }

    public BigAABB offset(BigDecimal x, double y, BigDecimal z) {
        return create(BigMath.addD(this.x0, x), this.y0 + y, BigMath.addD(this.z0, z), BigMath.addD(this.x1, x), this.y1 + y, BigMath.addD(this.z1, z));
    }

    public BigAABB offset(double x, double y, double z) {
        return create(BigMath.addD(this.x0, x), this.y0 + y, BigMath.addD(this.z0, z), BigMath.addD(this.x1, x), this.y1 + y, BigMath.addD(this.z1, z));
    }

    public boolean intersects(BigAABB c) {
        if (c.x1.compareTo(this.x0) <= 0 || c.x0.compareTo(this.x1) >= 0) {
            return false;
        } else if (c.y1 <= this.y0 || c.y0 >= this.y1) {
            return false;
        } else {
            return !(c.z1.compareTo(this.z0) <= 0) && !(c.z0.compareTo(this.z1) >= 0);
        }
    }

    public BigAABB grow(double x, double y, double z) {
        return grow(BigMath.decimal(x), y, BigMath.decimal(z));
    }

    public BigAABB grow(BigDecimal x, double y, BigDecimal z) {
        this.x0 = this.x0.add(x);
        this.y0 += y;
        this.z0 = this.z0.add(z);
        this.x1 = this.x1.add(x);
        this.y1 += y;
        this.z1 = this.z1.add(z);
        return this;
    }

    public static BigAABB from(AABB bb) {
        return create(BigMath.decimal(bb.x0), bb.y0, BigMath.decimal(bb.z0), BigMath.decimal(bb.x1), bb.y1, BigMath.decimal(bb.z1));
    }

    public AABB toVanilla() {
        return AABB.create(x0.doubleValue(), y0, z0.doubleValue(), x1.doubleValue(), y1, z1.doubleValue());
    }

    public void copyFrom(BigAABB c) {
        this.x0 = c.x0;
        this.y0 = c.y0;
        this.z0 = c.z0;
        this.x1 = c.x1;
        this.y1 = c.y1;
        this.z1 = c.z1;
    }

    public void copyFrom(AABB c) {
        this.x0 = BigMath.decimal(c.x0);
        this.y0 = c.y0;
        this.z0 = BigMath.decimal(c.z0);
        this.x1 = BigMath.decimal(c.x1);
        this.y1 = c.y1;
        this.z1 = BigMath.decimal(c.z1);
    }

    @Override
    public String toString() {
        return "big_box[" + this.x0 + ", " + this.y0 + ", " + this.z0 + " -> " + this.x1 + ", " + this.y1 + ", " + this.z1 + "]";
    }
}
