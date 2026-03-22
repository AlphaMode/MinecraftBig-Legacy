package me.alphamode.mcbig.extensions.features.big_movement;

import me.alphamode.mcbig.world.phys.BigAABB;

import java.math.BigDecimal;

public interface BigCullerExtension {
    boolean isVisible(BigAABB bb);

    void prepare(BigDecimal xOff, double yOff, BigDecimal zOff);
}
