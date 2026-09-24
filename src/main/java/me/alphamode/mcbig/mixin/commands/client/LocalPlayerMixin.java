package me.alphamode.mcbig.mixin.commands.client;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alphamode.mcbig.client.commands.ClientCommandSource;
import me.alphamode.mcbig.extensions.features.commands.LocalPlayerExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin implements LocalPlayerExtension {

    @Shadow
    protected Minecraft minecraft;

    @Override
    public ClientCommandSource getCommandSource() {
        return new ClientCommandSource(this.minecraft);
    }

    @Override
    public void command(String command) {
        try {
            this.minecraft.getCommands().getDispatcher().execute(command, this.minecraft.player.getCommandSource());
        } catch (CommandSyntaxException e) {
            this.minecraft.gui.addMessage("Failed to execute command: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
