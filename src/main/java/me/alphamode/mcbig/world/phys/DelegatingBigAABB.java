package me.alphamode.mcbig.world.phys;

import net.minecraft.world.phys.AABB;

import java.math.BigDecimal;

public class DelegatingBigAABB extends BigAABB {
    private AABB delegate;

    public DelegatingBigAABB(BigDecimal minX, double minY, BigDecimal minZ, BigDecimal maxX, double maxY, BigDecimal maxZ) {
        super(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public void setDelegate(AABB delegate) {
        this.delegate = delegate;
    }

    @Override
    public BigAABB set(BigDecimal x0, double y0, BigDecimal z0, BigDecimal x1, double y1, BigDecimal z1) {
        BigAABB result = super.set(x0, y0, z0, x1, y1, z1);
        delegate.set(result.x0.doubleValue(), result.y0, result.z0.doubleValue(), result.x1.doubleValue(), result.y1, result.z1.doubleValue());
        return result;
    }

    @Override
    public BigAABB grow(BigDecimal x, double y, BigDecimal z) {
        BigAABB result = super.grow(x, y, z);
        delegate.set(result.x0.doubleValue(), result.y0, result.z0.doubleValue(), result.x1.doubleValue(), result.y1, result.z1.doubleValue());
        return result;
    }

    @Override
    public void copyFrom(BigAABB c) {
        super.copyFrom(c);
        delegate.set(c.x0.doubleValue(), c.y0, c.z0.doubleValue(), c.x1.doubleValue(), c.y1, c.z1.doubleValue());
    }

    @Override
    public void copyFrom(AABB c) {
        super.copyFrom(c);
        delegate.copyFrom(c);
    }
}
