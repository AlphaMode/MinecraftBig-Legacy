package me.alphamode.mcbig.mixin.commands.client;

import me.alphamode.mcbig.client.commands.ChatComponent;
import me.alphamode.mcbig.extensions.features.commands.GuiExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin implements GuiExtension {

    private ChatComponent guiComponent;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(Minecraft mc, CallbackInfo ci) {
        this.guiComponent = new ChatComponent(mc);
    }

    @Override
    public ChatComponent getChat() {
        return guiComponent;
    }
}
