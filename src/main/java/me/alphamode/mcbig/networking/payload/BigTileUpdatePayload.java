package me.alphamode.mcbig.networking.payload;

import me.alphamode.mcbig.networking.McBigNetworking;
import net.minecraft.network.PacketListener;
import net.minecraft.network.packets.TileUpdatePacket;
import net.minecraft.world.level.Level;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public record BigTileUpdatePayload(BigInteger x, int y, BigInteger z, int block, int data) implements Payload {
    public static final Type<BigTileUpdatePayload> TYPE = Type.create(
            PayloadIds.BIG_TILE_UPDATE,
            BigTileUpdatePayload::encode, BigTileUpdatePayload::decode,
            BigTileUpdatePayload::toVanilla
    );

    public static void encode(DataOutputStream output, BigTileUpdatePayload payload) throws IOException {
        McBigNetworking.writeByteArray(output, payload.x().toByteArray());
        output.write(payload.y());
        McBigNetworking.writeByteArray(output, payload.z().toByteArray());
        output.write(payload.block());
        output.write(payload.data());
    }

    public static BigTileUpdatePayload decode(DataInputStream input) throws IOException {
        BigInteger x = new BigInteger(McBigNetworking.readByteArray(input));
        int y = input.read();
        BigInteger z = new BigInteger(McBigNetworking.readByteArray(input));
        int block = input.read();
        int data = input.read();
        return new BigTileUpdatePayload(x, y, z, block, data);
    }

    public BigTileUpdatePayload(BigInteger x, int y, BigInteger z, Level level) {
        this(x, y, z, level.getTile(x, y, z), level.getData(x, y, z));
    }

    public TileUpdatePacket toVanilla() {
        TileUpdatePacket packet = new TileUpdatePacket();
        packet.x = x.intValue();
        packet.y = y;
        packet.z = z.intValue();
        packet.block = block;
        packet.data = data;
        return packet;
    }

    @Override
    public boolean shouldDelay() {
        return true;
    }

    @Override
    public Type<BigTileUpdatePayload> type() {
        return TYPE;
    }

    @Override
    public boolean handle(PacketListener listener) {
        return listener.handleBigTileUpdate(this);
    }
}
