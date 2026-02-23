package me.alphamode.mcbig.networking.payload;

import me.alphamode.mcbig.networking.McBigNetworking;
import net.minecraft.network.PacketListener;
import net.minecraft.network.packets.ChunkVisibilityPacket;

import java.math.BigInteger;

public record BigChunkVisibilityPayload(BigInteger x, BigInteger z, boolean visible) implements Payload {
    public static final Type<BigChunkVisibilityPayload> TYPE = Type.create(PayloadIds.BIG_CHUNK_VISIBILITY, (output, payload) -> {
        McBigNetworking.writeByteArray(output, payload.x().toByteArray());
        McBigNetworking.writeByteArray(output, payload.z().toByteArray());
        output.writeBoolean(payload.visible());
    }, input -> new BigChunkVisibilityPayload(new BigInteger(McBigNetworking.readByteArray(input)), new BigInteger(McBigNetworking.readByteArray(input)), input.readBoolean()), BigChunkVisibilityPayload::toVanilla);

    public ChunkVisibilityPacket toVanilla() {
        return new ChunkVisibilityPacket(x.intValue(), z.intValue(), visible);
    }

    @Override
    public Type<BigChunkVisibilityPayload> type() {
        return TYPE;
    }

    @Override
    public boolean handle(PacketListener listener) {
        return listener.handleBigChunkVisibility(this);
    }
}
