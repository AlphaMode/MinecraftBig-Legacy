package me.alphamode.mcbig.mixin.commands.client;

import me.alphamode.mcbig.commands.CommandSource;
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
    public CommandSource getCommandSource() {
        return new CommandSource(this.minecraft);
    }
}
