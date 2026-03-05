package me.alphamode.mcbig.client.gui;

import me.alphamode.mcbig.world.level.levelgen.WorldType;
import net.minecraft.client.gui.components.Button;

public class WorldTypeButton extends Button {
    private WorldType selected;

    public WorldTypeButton(int id, int x, int y) {
        super(id, x, y, 150, 20, WorldType.SELECTED.getMessage());
        this.selected = WorldType.SELECTED;
    }

    public void clicked() {
        this.selected = WorldType.values()[(this.selected.ordinal() + 1) % WorldType.values().length];
        this.message = this.selected.getMessage();
        WorldType.SELECTED = this.selected;
    }
}
