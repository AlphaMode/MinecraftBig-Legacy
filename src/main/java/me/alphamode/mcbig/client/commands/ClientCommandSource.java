package me.alphamode.mcbig.client.commands;

import me.alphamode.mcbig.commands.CommandSource;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

import java.math.BigDecimal;

public class ClientCommandSource implements CommandSource {
    private final Minecraft mc;

    public ClientCommandSource(Minecraft minecraft) {
        this.mc = minecraft;
    }

    @Override
    public String getName() {
        return mc.player.name;
    }

    @Override
    public void sendMessage(String message) {
        this.mc.gui.addMessage(message);
    }

    @Override
    public Entity getEntity() {
        return this.mc.player;
    }

    @Override
    public BigDecimal getX() {
        return ((BigEntityExtension) getEntity()).getX();
    }

    @Override
    public BigDecimal getZ() {
        return ((BigEntityExtension) getEntity()).getZ();
    }
}
