package me.alphamode.mcbig.commands;

import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

import java.math.BigDecimal;

public class CommandSource {

    private final Minecraft mc;

    public CommandSource(Minecraft minecraft) {
        this.mc = minecraft;
    }

    public void sendMessage(String message) {
        this.mc.gui.addMessage(message);
    }

    public Entity getEntity() {
        return this.mc.player;
    }

    public BigDecimal getX() {
        return ((BigEntityExtension) getEntity()).getX();
    }

    public BigDecimal getZ() {
        return ((BigEntityExtension) getEntity()).getZ();
    }
}
