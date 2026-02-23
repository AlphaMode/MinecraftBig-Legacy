package me.alphamode.mcbig.mixin.networking;

import me.alphamode.mcbig.extensions.networking.PayloadPacketListenerExtension;
import net.minecraft.network.PacketListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PacketListener.class)
public class PacketListenerMixin implements PayloadPacketListenerExtension {
}
