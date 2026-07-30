package me.alphamode.mcbig.client.gui;

import me.alphamode.mcbig.constants.McBigConstants;
import me.alphamode.mcbig.world.level.PreviewLevel;
import me.alphamode.mcbig.world.level.levelgen.WorldType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Lighting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.levelgen.RandomLevelSource;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.math.BigInteger;
import java.util.Random;

public class WorldBuilderScreen extends Screen {

    private EditBox seedEdit;
    private EditBox regionSize;
    private EditBox chunkX;
    private EditBox chunkZ;

    private WorldPreviewComponent preview;

    @Override
    public void init() {
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.seedEdit = new McBigEditBox(this, this.font, this.width / 2 - 100, 5, 200, 20, "", "Seed");
        this.regionSize = new McBigEditBox(this, this.font, this.width / 2 - 100, 35, 70, 20, "", "Region Size");
        this.chunkX = new McBigEditBox(this, this.font, this.width / 2 - 100 + 65 + 10, 35, 60, 20, "", "X");
        this.chunkZ = new McBigEditBox(this, this.font, this.width / 2 - 100 + 65 + 5 + 65 + 5, 35, 60, 20, "", "Z");
        this.buttons.add(new Button(0, this.width / 2 - 100, 95, "Generate"));
        this.buttons.add(new WorldTypeButton(1, this.width / 2 - 100, 65, 200, 20));
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.active) {
            if (button.id == 0) {
                if (preview != null) {
                    preview.destroy();
                }

                int regionSize = 1;
                String regionSizeString = this.regionSize.getValue();

                if (!Mth.isStringInvalid(regionSizeString)) {
                    try {
                        regionSize = Integer.parseInt(regionSizeString);
                    } catch (NumberFormatException _) {
                    }
                }

                BigInteger chunkX = BigInteger.ZERO;
                String chunkXString = this.chunkX.getValue();

                if (!Mth.isStringInvalid(chunkXString)) {
                    try {
                        chunkX = new BigInteger(chunkXString);
                    } catch (NumberFormatException _) {
                    }
                }

                BigInteger chunkZ = BigInteger.ZERO;
                String chunkZString = this.chunkZ.getValue();

                if (!Mth.isStringInvalid(chunkZString)) {
                    try {
                        chunkZ = new BigInteger(chunkZString);
                    } catch (NumberFormatException _) {
                    }
                }

                long seed = new Random().nextLong();
                String seedString = this.seedEdit.getValue();
                if (!Mth.isStringInvalid(seedString)) {
                    try {
                        seed = Long.parseLong(seedString);
                    } catch (NumberFormatException e) {
                        seed = seedString.hashCode();
                    }
                }

                ChunkSource chunkSource;
                if (WorldType.SELECTED == WorldType.VANILLA) {
                    chunkSource = new RandomLevelSource(new PreviewLevel(seed), seed);
                } else {
                    chunkSource = WorldType.SELECTED.getFactory().apply(new PreviewLevel(seed), seed);
                }
                preview = new WorldPreviewComponent(this, (Minecraft) FabricLoader.getInstance().getGameInstance(), chunkSource, regionSize, chunkX, chunkZ);
            }

            if (button.id == 1 && button instanceof WorldTypeButton worldTypeButton) {
                worldTypeButton.clicked();
            }
        }
    }

    @Override
    protected void keyPressed(char eventCharacter, int eventKey) {
        super.keyPressed(eventCharacter, eventKey);
        this.seedEdit.charTyped(eventCharacter, eventKey);
        this.regionSize.charTyped(eventCharacter, eventKey);
        this.chunkX.charTyped(eventCharacter, eventKey);
        this.chunkZ.charTyped(eventCharacter, eventKey);

        if (eventKey == Keyboard.KEY_F1) {
            this.minecraft.options.hideGui = !this.minecraft.options.hideGui;
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int buttonNum) {
        super.mouseClicked(x, y, buttonNum);
        this.seedEdit.clicked(x, y, buttonNum);
        this.regionSize.clicked(x, y, buttonNum);
        this.chunkX.clicked(x, y, buttonNum);
        this.chunkZ.clicked(x, y, buttonNum);
    }

    @Override
    public void render(int xm, int ym, float a) {


//        renderBackground();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        font.drawShadow("MC Big " + McBigConstants.MC_BIG_VERSION + " (" + this.minecraft.fpsString + ")", 2, 2, 16777215);
        if (preview != null) {
            Lighting.turnOff();
            preview.render(xm, ym, a);
            Lighting.turnOn();
        }
        if (!this.minecraft.options.hideGui) {
            super.render(xm, ym, a);
            this.seedEdit.render();
            this.regionSize.render();
            this.chunkX.render();
            this.chunkZ.render();
        }
    }
}
