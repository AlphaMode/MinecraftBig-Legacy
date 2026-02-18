package me.alphamode.mcbig.mixin.commands.client;

import me.alphamode.mcbig.client.commands.ChatComponent;
import me.alphamode.mcbig.extensions.features.commands.GuiExtension;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Gui.class)
public class GuiMixin implements GuiExtension {

    private ChatComponent guiComponent = new ChatComponent();

    @Override
    public ChatComponent getChat() {
        return guiComponent;
    }
}
