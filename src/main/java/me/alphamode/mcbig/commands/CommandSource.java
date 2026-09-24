package me.alphamode.mcbig.commands;

import net.minecraft.world.entity.Entity;

import java.math.BigDecimal;

public interface CommandSource {

    String getName();

    void sendMessage(String message);

    Entity getEntity();

    BigDecimal getX();

    BigDecimal getZ();
}
