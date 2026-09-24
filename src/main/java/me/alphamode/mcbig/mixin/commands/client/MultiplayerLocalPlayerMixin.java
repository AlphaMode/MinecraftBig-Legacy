package me.alphamode.mcbig.mixin.commands.client;

import me.alphamode.mcbig.extensions.features.commands.LocalPlayerExtension;
import net.minecraft.client.multiplayer.ClientConnection;
import net.minecraft.client.multiplayer.MultiplayerLocalPlayer;
import net.minecraft.network.packet.ChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MultiplayerLocalPlayer.class)
public class MultiplayerLocalPlayerMixin implements LocalPlayerExtension {
    @Shadow
    public ClientConnection connection;

    @Override
    public void command(String command) {
        this.connection.send(new ChatPacket("/" + command));
    }
}
