package me.alphamode.mcbig.networking.payload;

import me.alphamode.mcbig.networking.McBigNetworking;
import net.minecraft.network.PacketListener;
import net.minecraft.network.packets.BlockRegionUpdatePacket;
import net.minecraft.world.level.Level;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public record BigBlockRegionUpdatePayload(BigInteger x, int y, BigInteger z, int xs, int ys, int zs, byte[] buffer, int size) implements Payload {
    public static final Type<BigBlockRegionUpdatePayload> TYPE = Type.create(PayloadIds.BIG_BLOCK_REGION_UPDATE, BigBlockRegionUpdatePayload::encode, BigBlockRegionUpdatePayload::decode, BigBlockRegionUpdatePayload::toVanilla);

    public BigBlockRegionUpdatePayload(BigInteger x, int y, BigInteger z, int xs, int ys, int zs, Level level) {
        byte[] input = level.getBlocksAndData(x, y, z, xs, ys, zs);
        Deflater deflater = new Deflater(-1);

        byte[] buffer = new byte[xs * ys * zs * 5 / 2];
        int size;

        try {
            deflater.setInput(input);
            deflater.finish();
            size = deflater.deflate(buffer);
        } finally {
            deflater.end();
        }

        this(x, y, z, xs, ys, zs, buffer, size);
    }

    public static void encode(DataOutputStream output, BigBlockRegionUpdatePayload payload) throws IOException {
        McBigNetworking.writeByteArray(output, payload.x().toByteArray());
        output.writeShort(payload.y());
        McBigNetworking.writeByteArray(output, payload.z().toByteArray());
        output.write(payload.xs() - 1);
        output.write(payload.ys() - 1);
        output.write(payload.zs() - 1);
        output.writeInt(payload.size());
        output.write(payload.buffer(), 0, payload.size());
    }

    public static BigBlockRegionUpdatePayload decode(DataInputStream input) throws IOException {
        BigInteger x = new BigInteger(McBigNetworking.readByteArray(input));
        int y = input.readShort();
        BigInteger z = new BigInteger(McBigNetworking.readByteArray(input));
        int xs = input.read() + 1;
        int ys = input.read() + 1;
        int zs = input.read() + 1;
        int size = input.readInt();
        byte[] data = new byte[size];
        input.readFully(data);
        byte[] buffer = new byte[xs * ys * zs * 5 / 2];
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        try {
            inflater.inflate(buffer);
        } catch (DataFormatException var8) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }

        return new BigBlockRegionUpdatePayload(x, y, z, xs, ys, zs, buffer, size);
    }


    public BlockRegionUpdatePacket toVanilla() {
        BlockRegionUpdatePacket packet = new BlockRegionUpdatePacket();
        packet.x = x.intValue();
        packet.y = y;
        packet.z = z.intValue();
        packet.xs = xs;
        packet.ys = ys;
        packet.zs = zs;
        packet.buffer = buffer;
        packet.size = size;
        return packet;
    }

    @Override
    public boolean shouldDelay() {
        return true;
    }

    @Override
    public Type<BigBlockRegionUpdatePayload> type() {
        return TYPE;
    }

    @Override
    public boolean handle(PacketListener listener) {
        return listener.handleBigBlockRegionUpdate(this);
    }
}
