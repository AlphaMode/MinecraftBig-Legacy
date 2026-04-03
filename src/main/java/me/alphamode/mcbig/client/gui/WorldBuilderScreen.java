package me.alphamode.mcbig.client.gui;

import me.alphamode.mcbig.constants.McBigConstants;
import me.alphamode.mcbig.world.level.PreviewLevel;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Lighting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;
import net.minecraft.world.level.levelgen.RandomLevelSource;
import org.lwjgl.opengl.GL11;

import java.math.BigInteger;

public class WorldBuilderScreen extends Screen {

    private final WorldPreviewComponent preview = new WorldPreviewComponent(this, (Minecraft) FabricLoader.getInstance().getGameInstance(), new RandomLevelSource(new PreviewLevel(2), 2), 8, BigInteger.ZERO, BigInteger.ZERO);

    @Override
    public void render(int xm, int ym, float a) {

//        super.render(xm, ym, a);
//        blitOffset = -90;
//        renderBackground();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        font.drawShadow("MC Big " + McBigConstants.MC_BIG_VERSION + " (" + this.minecraft.fpsString + ")", 2, 2, 16777215);
        Lighting.turnOff();
        preview.render(xm, ym, a);
        Lighting.turnOn();
    }
}
