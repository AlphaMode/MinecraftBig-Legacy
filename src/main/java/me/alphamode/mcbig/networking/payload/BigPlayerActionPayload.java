package me.alphamode.mcbig.networking.payload;

import me.alphamode.mcbig.networking.McBigNetworking;
import net.minecraft.network.PacketListener;
import net.minecraft.network.packets.PlayerActionPacket;

import java.math.BigInteger;

public record BigPlayerActionPayload(BigInteger x, int y, BigInteger z, int face, int action) implements Payload {
    public static final int START_DESTROY_BLOCK = 0;
    public static final int CONTINUE_DESTROY_BLOCK = 1;
    public static final int STOP_DESTROY_BLOCK = 2;
    public static final int GET_UPDATED_BLOCK = 3;
    public static final int DROP_ITEM = 4;

    public static final Type<BigPlayerActionPayload> TYPE = Type.create(PayloadIds.BIG_PLAYER_ACTION, (output, payload) -> {
        output.write(payload.action());
        McBigNetworking.writeByteArray(output, payload.x().toByteArray());
        output.write(payload.y());
        McBigNetworking.writeByteArray(output, payload.z().toByteArray());
        output.write(payload.face());
    }, input -> {
        int action = input.read();
        BigInteger x = new BigInteger(McBigNetworking.readByteArray(input));
        int y = input.read();
        BigInteger z = new BigInteger(McBigNetworking.readByteArray(input));
        int face = input.read();
        return new BigPlayerActionPayload(x, y, z, face, action);
    }, BigPlayerActionPayload::toVanilla);

    public PlayerActionPacket toVanilla() {
        PlayerActionPacket packet = new PlayerActionPacket();
        packet.action = action;
        packet.x = x.intValue();
        packet.y = y;
        packet.z = z.intValue();
        packet.face = face;
        return packet;
    }

    @Override
    public Type<BigPlayerActionPayload> type() {
        return TYPE;
    }

    @Override
    public boolean handle(PacketListener listener) {
        return listener.handleBigPlayerAction(this);
    }
}
