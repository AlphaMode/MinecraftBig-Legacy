package me.alphamode.mcbig.extensions;

import java.math.BigDecimal;

public interface CommandPlayerExtension {
    default boolean canFly() {
        throw new UnsupportedOperationException();
    }

    default void setCanFly(boolean canFly) {
        throw new UnsupportedOperationException();
    }

    default boolean isFlying() {
        throw new UnsupportedOperationException();
    }

    default void setFlying(boolean flying) {
        throw new UnsupportedOperationException();
    }

    default void setFlySpeed(float speed) {
        throw new UnsupportedOperationException();
    }

    default float getFlySpeed() {
        throw new UnsupportedOperationException();
    }

    default void setNoclip(boolean noclip) {
        throw new UnsupportedOperationException();
    }

    default boolean canNoclip() {
        throw new UnsupportedOperationException();
    }

    default void teleport(BigDecimal x, double y, BigDecimal z) {
        throw new UnsupportedOperationException();
    }
}
