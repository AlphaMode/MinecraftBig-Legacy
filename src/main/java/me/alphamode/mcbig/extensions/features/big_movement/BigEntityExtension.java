package me.alphamode.mcbig.extensions.features.big_movement;

import me.alphamode.mcbig.world.phys.BigAABB;

import java.math.BigDecimal;

public interface BigEntityExtension {
    void setPos(BigDecimal x, double y, BigDecimal z);

    void bigMove(double x, double y, double z);

    void absMoveTo(BigDecimal x, double y, BigDecimal z, float yRot, float xRot);

    default BigAABB getBigBB() {
        throw new UnsupportedOperationException();
    }

    BigDecimal getX();
    BigDecimal getZ();

    void setX(BigDecimal x);
    void setZ(BigDecimal z);

    BigDecimal getXO();
    BigDecimal getZO();

    void setXO(BigDecimal x);
    void setZO(BigDecimal z);

    BigDecimal getXOld();
    BigDecimal getZOld();

    void setXOld(BigDecimal x);
    void setZOld(BigDecimal z);
}
