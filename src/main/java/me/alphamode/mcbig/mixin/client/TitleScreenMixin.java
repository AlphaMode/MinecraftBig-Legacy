package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.constants.McBigConstants;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    private static int heightOffset = 20;

    @Inject(method = "render", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/client/gui/TitleScreen;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V", ordinal = 0
    ))
    private void addMcBigVersion(int mouseY, int a, float par3, CallbackInfo ci) {
        this.drawString(this.font, "McBig Legacy (" + McBigConstants.MC_BIG_VERSION + ")", 2, this.height - heightOffset(), 5263440);
    }

    private static int heightOffset() {
        //? >=1.0.0-beta.8.0.r {
        /*return 20;
        *///? } else
        return 10;
    }
}
