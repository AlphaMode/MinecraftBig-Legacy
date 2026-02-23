package me.alphamode.mcbig.networking.payload;

import me.alphamode.mcbig.networking.McBigNetworking;
import net.minecraft.network.PacketListener;
import net.minecraft.network.packets.SetSpawnPositionPacket;

import java.math.BigInteger;

public record BigSetSpawnPositionPayload(BigInteger x, int y, BigInteger z) implements Payload {
    public static final Type<BigSetSpawnPositionPayload> TYPE = Type.create(PayloadIds.SET_SPAWN, (output, payload) -> {
        McBigNetworking.writeByteArray(output, payload.x().toByteArray());
        output.writeInt(payload.y());
        McBigNetworking.writeByteArray(output, payload.z().toByteArray());
    }, input -> {
        return new BigSetSpawnPositionPayload(new BigInteger(McBigNetworking.readByteArray(input)), input.readInt(), new BigInteger(McBigNetworking.readByteArray(input)));
    }, BigSetSpawnPositionPayload::toVanilla);

    public SetSpawnPositionPacket toVanilla() {
        SetSpawnPositionPacket packet = new SetSpawnPositionPacket();
        packet.x = x.intValue();
        packet.y = y;
        packet.z = z.intValue();
        return packet;
    }

    @Override
    public Type<BigSetSpawnPositionPayload> type() {
        return TYPE;
    }

    @Override
    public boolean handle(PacketListener listener) {
        return listener.handleBigSetSpawn(this);
    }
}
