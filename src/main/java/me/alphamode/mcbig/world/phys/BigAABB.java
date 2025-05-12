package me.alphamode.mcbig.world.phys;

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

    private BigAABB(BigDecimal minX, double minY, BigDecimal minZ, BigDecimal maxX, double maxY, BigDecimal maxZ) {
        this.x0 = minX;
        this.y0 = minY;
        this.z0 = minZ;
        this.x1 = maxX;
        this.y1 = maxY;
        this.z1 = maxZ;
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

    public BigAABB grow(double x, double y, double z) {
        return grow(BigDecimal.valueOf(x), y, BigDecimal.valueOf(z));
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
}
