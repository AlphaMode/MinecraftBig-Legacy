package me.alphamode.mcbig.networking.packets;

import me.alphamode.mcbig.networking.McBigNetworking;
import me.alphamode.mcbig.networking.VarInt;
import me.alphamode.mcbig.networking.payload.Payload;
import net.minecraft.network.PacketListener;
import net.minecraft.network.packets.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.BiConsumer;

public final class McBigPayloadPacket extends Packet {
    public static final int ID = 199;

    private Payload payload;

    public McBigPayloadPacket(Payload payload) {
        this.payload = payload;
        this.shouldDelay = payload.shouldDelay();
    }

    public McBigPayloadPacket() {}

    public Payload payload() {
        return this.payload;
    }

    @Override
    public void read(DataInputStream data) throws IOException {
        int id = VarInt.read(data);
        if (!McBigNetworking.PAYLOADS.containsKey(id)) {
            System.out.println("Unknown payload id: " + id);
        }

        Payload.Type<?> type = McBigNetworking.PAYLOADS.get(id);
        this.payload = type.codec().decode(data);
        this.shouldDelay = this.payload.shouldDelay();
    }

    @Override
    public void write(DataOutputStream data) throws IOException {
        VarInt.write(data, this.payload.type().id());
        ((Payload.PayloadCodec) this.payload.type().codec()).encode(data, this.payload);
    }

    @Override
    public void handle(PacketListener handler) {
        if (!this.payload.handle(handler)) {
            handler.onUnhandledPacket(this);
        }
    }

    @Override
    public int getEstimatedSize() {
        return 4; // 4 for the id the rest we have no idea
    }
}
