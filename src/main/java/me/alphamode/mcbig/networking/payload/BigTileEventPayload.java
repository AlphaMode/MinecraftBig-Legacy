package me.alphamode.mcbig.networking.payload;

import me.alphamode.mcbig.networking.McBigNetworking;
import net.minecraft.network.PacketListener;
import net.minecraft.network.packets.TileEventPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public record BigTileEventPayload(BigInteger x, int y, BigInteger z, int b0, int b1) implements Payload {
    public static final Type<BigTileEventPayload> TYPE = Type.create(
            PayloadIds.BIG_TILE_EVENT,
            BigTileEventPayload::encode, BigTileEventPayload::decode,
            BigTileEventPayload::toVanilla
    );

    public static void encode(DataOutputStream output, BigTileEventPayload payload) throws IOException {
        McBigNetworking.writeByteArray(output, payload.x().toByteArray());
        output.writeShort(payload.y());
        McBigNetworking.writeByteArray(output, payload.z().toByteArray());
        output.write(payload.b0());
        output.write(payload.b1());
    }

    public static BigTileEventPayload decode(DataInputStream input) throws IOException {
        BigInteger x = new BigInteger(McBigNetworking.readByteArray(input));
        int y = input.readShort();
        BigInteger z = new BigInteger(McBigNetworking.readByteArray(input));
        int b0 = input.read();
        int b1 = input.read();
        return new BigTileEventPayload(x, y, z, b0, b1);
    }

    public TileEventPacket toVanilla() {
        TileEventPacket packet = new TileEventPacket();
        packet.x = x.intValue();
        packet.y = y;
        packet.z = z.intValue();
        packet.b0 = b0;
        packet.b1 = b1;
        return packet;
    }

    @Override
    public Type<BigTileEventPayload> type() {
        return TYPE;
    }

    @Override
    public boolean handle(PacketListener listener) {
        return listener.handleBigTileEvent(this);
    }
}
