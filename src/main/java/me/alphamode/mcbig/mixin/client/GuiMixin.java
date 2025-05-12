package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow
    private Minecraft minecraft;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V", ordinal = 2))
    private void fixXCoord(Gui instance, Font font, String s, int x, int y, int color) {
        instance.drawString(font, String.format("x: %.3f", ((BigEntityExtension) this.minecraft.player).getX()), 2, 64, 14737632);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V", ordinal = 3))
    private void fixYCoord(Gui instance, Font font, String s, int x, int y, int color) {
        instance.drawString(font, String.format("y: %.3f", this.minecraft.player.y), 2, 72, 14737632);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V", ordinal = 4))
    private void fixZCoord(Gui instance, Font font, String s, int x, int y, int color) {
        instance.drawString(font, String.format("z: %.3f", ((BigEntityExtension) this.minecraft.player).getZ()), 2, 80, 14737632);
    }
}
