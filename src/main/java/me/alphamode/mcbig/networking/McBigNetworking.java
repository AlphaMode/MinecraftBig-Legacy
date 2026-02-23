package me.alphamode.mcbig.networking;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.alphamode.mcbig.networking.payload.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class McBigNetworking {
    // TODO: possible bit packing so we can encode the protocolVersion we are using
    public static final long MC_BIG_VERSION_MAGIC = 17;

    public static final int PROTOCOL_VERSION = 1;

    public static final Int2ObjectMap<Payload.Type<?>> PAYLOADS = new Int2ObjectOpenHashMap<>();

    public static void registerPayload(Payload.Type<?> type) {
        PAYLOADS.put(type.id(), type);
    }

    public static byte[] readByteArray(DataInputStream input) throws IOException {
        int size = VarInt.read(input);
        byte[] bytes = new byte[size];
        input.readFully(bytes);
        return bytes;
    }

    public static void writeByteArray(DataOutputStream output, byte[] bytes) throws IOException {
        VarInt.write(output, bytes.length);
        output.write(bytes);
    }

    static {
        registerPayload(ConfigurePayload.TYPE);
        registerPayload(BigSetSpawnPositionPayload.TYPE);
        registerPayload(BigMovePlayerPayload.StatusOnly.TYPE);
        registerPayload(BigMovePlayerPayload.Pos.TYPE);
        registerPayload(BigMovePlayerPayload.PosRot.TYPE);
        registerPayload(BigMovePlayerPayload.Rot.TYPE);
        registerPayload(BigChunkVisibilityPayload.TYPE);
        registerPayload(BigChunkTilesUpdatePayload.TYPE);
        registerPayload(BigTileUpdatePayload.TYPE);
        registerPayload(BigBlockRegionUpdatePayload.TYPE);
        registerPayload(BigTileEventPayload.TYPE);
        registerPayload(BigPlayerActionPayload.TYPE);
    }
}
