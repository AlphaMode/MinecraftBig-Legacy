package me.alphamode.mcbig.networking.payload;

import net.minecraft.network.PacketListener;
import net.minecraft.network.packets.Packet;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.BiConsumer;
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
    }

    default boolean shouldDelay() {
        return false;
    }

    Type<? extends Payload> type();

    boolean handle(PacketListener listener);

    record Type<P extends Payload>(int id, PayloadCodec<P> codec, @Nullable Function<P, Packet> vanillaConverter) {
        public static <P extends Payload> Type<P> create(int id, PayloadEncoder<P> encoder, PayloadDecoder<P> decoder) {
            return create(id, encoder, decoder, null);
        }

        public static <P extends Payload> Type<P> create(int id, PayloadEncoder<P> encoder, PayloadDecoder<P> decoder, @Nullable Function<P, Packet> vanillaConverter) {
            return new Type<>(id, new PayloadCodec<>() {
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

    interface PayloadCodec<P extends Payload> extends PayloadEncoder<P>, PayloadDecoder<P> {}

    interface PayloadEncoder<P extends Payload> {
        void encode(DataOutputStream output, P payload) throws IOException;
    }

    interface PayloadDecoder<P extends Payload> {
        P decode(DataInputStream input) throws IOException;
    }
}
