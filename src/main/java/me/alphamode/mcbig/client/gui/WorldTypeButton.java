package me.alphamode.mcbig.client.gui;

import me.alphamode.mcbig.world.level.levelgen.WorldType;
import net.minecraft.client.gui.Button;

public class WorldTypeButton extends Button {
    private WorldType selected;

    public WorldTypeButton(int id, int x, int y, WorldType selected) {
        super(id, x, y, 150, 20, selected.getMessage());
        this.selected = selected;
    }

    public WorldTypeButton(int id, int x, int y, int width, int height, WorldType selected) {
        super(id, x, y, width, height, selected.getMessage());
        this.selected = selected;
    }

    public WorldType clicked() {
        this.selected = WorldType.values()[(this.selected.ordinal() + 1) % WorldType.values().length];
        this.message = this.selected.getMessage();
        return this.selected;
    }
}
