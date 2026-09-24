package me.alphamode.mcbig.networking.payload;

import net.minecraft.network.PacketListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record AbilitiesPayload(boolean canFly, boolean canNoclip) implements Payload {
    public static final Type<AbilitiesPayload> TYPE = Type.create(PayloadIds.ABILITIES, AbilitiesPayload::encode, AbilitiesPayload::decode);

    public static void encode(DataOutputStream output, AbilitiesPayload payload) throws IOException {
        output.writeBoolean(payload.canFly());
        output.writeBoolean(payload.canNoclip());
    }

    public static AbilitiesPayload decode(DataInputStream input) throws IOException {
        boolean canFly = input.readBoolean();
        boolean canNoclip = input.readBoolean();
        return new AbilitiesPayload(canFly, canNoclip);
    }

    @Override
    public Type<AbilitiesPayload> type() {
        return TYPE;
    }

    @Override
    public boolean handle(PacketListener listener) {
        return listener.handleAbilities(this);
    }
}
