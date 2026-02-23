package me.alphamode.mcbig.mixin.networking;

import me.alphamode.mcbig.extensions.networking.BigConnectionExtension;
import me.alphamode.mcbig.networking.packets.McBigPayloadPacket;
import me.alphamode.mcbig.networking.payload.Payload;
import me.alphamode.mcbig.prelaunch.Features;
import net.minecraft.network.Connection;
import net.minecraft.network.packets.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Function;

@Mixin(Connection.class)
public abstract class ConnectionMixin implements BigConnectionExtension {

    private boolean isBigConnection = false;

    private List<Features> features;

    @Shadow
    public abstract void send(Packet packet);

    @Inject(method = "send", at = @At("HEAD"), cancellable = true)
    private void convertPayloads(Packet packet, CallbackInfo ci) {
        if (packet instanceof McBigPayloadPacket p) {
            Function<Payload, Packet> converter = (Function<Payload, Packet>) p.payload().type().vanillaConverter();
            if (!isBigConnection && converter != null) {
                send(converter.apply(p.payload()));
                ci.cancel();
            }
        }
    }

    @Override
    public void sendPayload(Payload payload) {
        this.send(new McBigPayloadPacket(payload));
    }

    @Override
    public List<Features> getFeatures() {
        return this.features;
    }

    @Override
    public void setFeatures(List<Features> features) {
        this.features = features;
    }

    @Override
    public void setBigConnection(boolean value) {
        this.isBigConnection = value;
    }

    @Override
    public boolean isBigConnection() {
        return this.isBigConnection;
    }
}
