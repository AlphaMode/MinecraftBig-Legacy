package me.alphamode.mcbig.extensions.features.big_movement;

import java.math.BigDecimal;

public interface BigEntityExtension {
    void setPos(BigDecimal x, double y, BigDecimal z);

    void bigMove(double x, double y, double z);

    BigDecimal getX();
    BigDecimal getZ();

    void setX(BigDecimal x);
    void setZ(BigDecimal z);

    BigDecimal getXOld();
    BigDecimal getZOld();

    void setXOld(BigDecimal x);
    void setZOld(BigDecimal z);
}
