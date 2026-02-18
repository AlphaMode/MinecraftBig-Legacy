package me.alphamode.mcbig.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

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
}
