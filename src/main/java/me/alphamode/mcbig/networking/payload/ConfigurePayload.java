package me.alphamode.mcbig.networking.payload;

import me.alphamode.mcbig.prelaunch.Features;
import net.minecraft.network.PacketListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record ConfigurePayload(int protocolVersion, List<Features> features) implements Payload {
    public static final Type<ConfigurePayload> TYPE = Type.create(PayloadIds.CONFIGURE, ConfigurePayload::encode, ConfigurePayload::decode);

    public static void encode(DataOutputStream output, ConfigurePayload payload) throws IOException {
        output.writeInt(payload.protocolVersion());
        List<Features> features = payload.features();
        int size = features.size();
        output.writeInt(size);
        for (int i = 0; i < size; i++) {
            output.writeByte(features.get(i).ordinal());
        }
    }

    public static ConfigurePayload decode(DataInputStream input) throws IOException {
        int version = input.readInt();
        int size = input.readInt();
        List<Features> features = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            features.add(Features.values()[input.readByte()]);
        }
        return new ConfigurePayload(version, List.copyOf(features));
    }

    @Override
    public Type<ConfigurePayload> type() {
        return TYPE;
    }

    @Override
    public boolean handle(PacketListener listener) {
        return listener.handleConfigure(this);
    }
}
