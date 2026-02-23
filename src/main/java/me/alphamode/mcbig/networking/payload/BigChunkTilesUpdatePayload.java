package me.alphamode.mcbig.networking.payload;

import me.alphamode.mcbig.networking.McBigNetworking;
import net.minecraft.network.PacketListener;
import net.minecraft.network.packets.ChunkTilesUpdatePacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public record BigChunkTilesUpdatePayload(BigInteger xc, BigInteger zc, short[] positions, byte[] blocks, byte[] data,
                                         int changes) implements Payload {
    public static final Type<BigChunkTilesUpdatePayload> TYPE = Type.create(PayloadIds.BIG_CHUNK_TILES_UPDATE, BigChunkTilesUpdatePayload::encode, BigChunkTilesUpdatePayload::decode, BigChunkTilesUpdatePayload::toVanilla);

    public BigChunkTilesUpdatePayload(BigInteger xc, BigInteger zc, short[] blockData, int changes, Level level) {
        short[] positions = new short[changes];
        byte[] blocks = new byte[changes];
        byte[] data = new byte[changes];
        LevelChunk chunk = level.getChunk(xc, zc);

        for (int i = 0; i < changes; i++) {
            int x = blockData[i] >> 12 & 15;
            int z = blockData[i] >> 8 & 15;
            int y = blockData[i] & 255;
            positions[i] = blockData[i];
            blocks[i] = (byte) chunk.getTile(x, y, z);
            data[i] = (byte) chunk.getData(x, y, z);
        }

        this(xc, zc, positions, blocks, data, changes);
    }

    public static void encode(DataOutputStream output, BigChunkTilesUpdatePayload payload) throws IOException {
        McBigNetworking.writeByteArray(output, payload.xc().toByteArray());
        McBigNetworking.writeByteArray(output, payload.zc().toByteArray());
        output.writeShort((short) payload.changes());

        for (int i = 0; i < payload.changes(); i++) {
            output.writeShort(payload.positions()[i]);
        }

        output.write(payload.blocks());
        output.write(payload.data());
    }

    public static BigChunkTilesUpdatePayload decode(DataInputStream input) throws IOException {
        BigInteger xc = new BigInteger(McBigNetworking.readByteArray(input));
        BigInteger zc = new BigInteger(McBigNetworking.readByteArray(input));
        int changes = input.readShort() & '\uffff';
        short[] positions = new short[changes];
        byte[] blocks = new byte[changes];
        byte[] data = new byte[changes];

        for (int i = 0; i < changes; i++) {
            positions[i] = input.readShort();
        }

        input.readFully(blocks);
        input.readFully(data);

        return new BigChunkTilesUpdatePayload(xc, zc, positions, blocks, data, changes);
    }

    public ChunkTilesUpdatePacket toVanilla() {
        ChunkTilesUpdatePacket packet = new ChunkTilesUpdatePacket();
        packet.zc = zc.intValue();
        packet.xc = xc.intValue();
        packet.positions = positions;
        packet.blocks = blocks;
        packet.data = data;
        packet.changes = changes;
        return packet;
    }

    @Override
    public boolean shouldDelay() {
        return true;
    }

    @Override
    public Type<BigChunkTilesUpdatePayload> type() {
        return TYPE;
    }

    @Override
    public boolean handle(PacketListener listener) {
        return listener.handleBigChunkTilesUpdate(this);
    }
}
