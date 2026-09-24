package me.alphamode.mcbig.networking.payload;

import net.minecraft.network.PacketListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record FlySpeedPayload(float speed) implements Payload {

    public static final Type<FlySpeedPayload> TYPE = Type.create(PayloadIds.FLY_SPEED, FlySpeedPayload::encode, FlySpeedPayload::decode);

    public static void encode(DataOutputStream output, FlySpeedPayload payload) throws IOException {
        output.writeFloat(payload.speed());
    }

    public static FlySpeedPayload decode(DataInputStream input) throws IOException {
        return new FlySpeedPayload(input.readFloat());
    }

    @Override
    public Type<FlySpeedPayload> type() {
        return TYPE;
    }

    @Override
    public boolean handle(PacketListener listener) {
        return listener.handleSetFlySpeed(this);
    }
}
