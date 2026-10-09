package me.alphamode.mcbig.extensions.features.big_movement;

import me.alphamode.mcbig.world.phys.BigAABB;

import java.math.BigDecimal;

public interface BigEntityExtension {
    void setPos(BigDecimal x, double y, BigDecimal z);

    void bigMove(double x, double y, double z);

    void absMoveTo(BigDecimal x, double y, BigDecimal z, float yRot, float xRot);

    void moveTo(BigDecimal x, double y, BigDecimal z, float yRot, float xRot);

    default BigAABB getBigBB() {
        throw new UnsupportedOperationException();
    }

    BigDecimal getX();
    BigDecimal getY();
    BigDecimal getZ();

    void setX(BigDecimal x);
    void setY(BigDecimal y);
    void setZ(BigDecimal z);

    BigDecimal getXO();
    BigDecimal getYO();
    BigDecimal getZO();

    void setXO(BigDecimal x);
    void setYO(BigDecimal y);
    void setZO(BigDecimal z);

    BigDecimal getXOld();
    BigDecimal getYOld();
    BigDecimal getZOld();

    void setXOld(BigDecimal x);
    void setYOld(BigDecimal x);
    void setZOld(BigDecimal z);

    double distanceToSqr(BigDecimal x, double y, BigDecimal z);

    boolean checkInBlock(BigDecimal x, double y, BigDecimal z);

    double distanceToSqr(BigDecimal x, BigDecimal y, BigDecimal z);

    boolean checkInBlock(BigDecimal x, BigDecimal y, BigDecimal z);
}
