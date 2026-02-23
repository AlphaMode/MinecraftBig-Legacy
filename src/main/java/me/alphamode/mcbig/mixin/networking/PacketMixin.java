package me.alphamode.mcbig.mixin.networking;

import me.alphamode.mcbig.networking.packets.McBigPayloadPacket;
import net.minecraft.network.packets.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Packet.class)
public abstract class PacketMixin {
    @Shadow
    static void registerPacket(int id, boolean clientbound, boolean serverbound, Class<? extends Packet> packetClass) {
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void registerMcBigPackets(CallbackInfo ci) {
        registerPacket(McBigPayloadPacket.ID, true, true, McBigPayloadPacket.class);
    }
}
