package me.alphamode.mcbig.extensions;

public interface PlayerExtension {
    boolean canFly();

    void setCanFly(boolean canFly);

    boolean isFlying();

    void setFlying(boolean flying);

    void setFlySpeed(float speed);

    float getFlySpeed();

    void setNoclip(boolean noclip);

    boolean canNoclip();
}
