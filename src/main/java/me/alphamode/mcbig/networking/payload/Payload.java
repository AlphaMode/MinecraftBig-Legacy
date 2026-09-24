package me.alphamode.mcbig.networking.payload;

import me.alphamode.mcbig.networking.StreamCodec;
import me.alphamode.mcbig.networking.StreamDecoder;
import me.alphamode.mcbig.networking.StreamEncoder;
import me.alphamode.mcbig.networking.packets.McBigPayloadPacket;
import net.minecraft.network.PacketListener;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.Function;

public interface Payload {
    interface PayloadIds {
        int CONFIGURE = 0;
        int SET_SPAWN = 1;

        // Player movement
        int MOVE_PLAYER_STATUS = 2;
        int MOVE_PLAYER_POS = 3;
        int MOVE_PLAYER_POS_ROT = 4;
        int MOVE_PLAYER_ROT = 5;
        int BIG_CHUNK_VISIBILITY = 6;
        int BIG_TILE_UPDATE = 7;
        int BIG_CHUNK_TILES_UPDATE = 8;
        int BIG_BLOCK_REGION_UPDATE = 9;
        int BIG_TILE_EVENT = 10;
        int BIG_PLAYER_ACTION = 11;
        int ABILITIES = 12;
        int FLY_SPEED = 13;
        int SPAWNER_UPDATE = 14;
        int COMMANDS = 15;
    }

    default boolean shouldDelay() {
        return false;
    }

    Type<? extends Payload> type();

    boolean handle(PacketListener listener);

    default Packet createPacket() {
        return new McBigPayloadPacket(this);
    }

    record Type<P extends Payload>(int id, StreamCodec<P> codec, @Nullable Function<P, Packet> vanillaConverter) {
        public static <P extends Payload> Type<P> create(int id, StreamEncoder<P> encoder, StreamDecoder<P> decoder) {
            return create(id, encoder, decoder, null);
        }

        public static <P extends Payload> Type<P> create(int id, StreamEncoder<P> encoder, StreamDecoder<P> decoder, @Nullable Function<P, Packet> vanillaConverter) {
            return new Type<>(id, new StreamCodec<>() {
                @Override
                public void encode(DataOutputStream output, P payload) throws IOException {
                    encoder.encode(output, payload);
                }

                @Override
                public P decode(DataInputStream input) throws IOException {
                    return decoder.decode(input);
                }
            }, vanillaConverter);
        }
    }
}
