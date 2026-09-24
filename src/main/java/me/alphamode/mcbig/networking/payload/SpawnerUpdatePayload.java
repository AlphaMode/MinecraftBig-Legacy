package me.alphamode.mcbig.networking.payload;

import me.alphamode.mcbig.networking.McBigNetworking;
import net.minecraft.network.PacketListener;
import net.minecraft.network.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public record SpawnerUpdatePayload(BigInteger x, int y, BigInteger z, String entityId) implements Payload {
    public static final Type<SpawnerUpdatePayload> TYPE = Type.create(PayloadIds.SPAWNER_UPDATE, SpawnerUpdatePayload::encode, SpawnerUpdatePayload::decode);
    public static void encode(DataOutputStream output, SpawnerUpdatePayload payload) throws IOException {
        McBigNetworking.writeByteArray(output, payload.x().toByteArray());
        output.write(payload.y());
        McBigNetworking.writeByteArray(output, payload.z().toByteArray());
        Packet.writeString(payload.entityId(), output);
    }

    public static SpawnerUpdatePayload decode(DataInputStream input) throws IOException {
        BigInteger x = new BigInteger(McBigNetworking.readByteArray(input));
        int y = input.read();
        BigInteger z = new BigInteger(McBigNetworking.readByteArray(input));
        String entityId = Packet.readString(input, 32767);
        return new SpawnerUpdatePayload(x, y, z, entityId);
    }

    @Override
    public Type<SpawnerUpdatePayload> type() {
        return TYPE;
    }

    @Override
    public boolean shouldDelay() {
        return true;
    }

    @Override
    public boolean handle(PacketListener listener) {
        return listener.handleSpawnerUpdate(this);
    }
}
