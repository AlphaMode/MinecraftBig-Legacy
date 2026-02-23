package me.alphamode.mcbig.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class VarInt {
    public static final int MAX_VARINT_SIZE = 5;
    private static final int DATA_BITS_MASK = 127;
    private static final int CONTINUATION_BIT_MASK = 128;
    private static final int DATA_BITS_PER_BYTE = 7;

    public static int getByteSize(final int value) {
        for (int i = 1; i < MAX_VARINT_SIZE; i++) {
            if ((value & -1 << i * DATA_BITS_PER_BYTE) == 0) {
                return i;
            }
        }

        return MAX_VARINT_SIZE;
    }

    public static boolean hasContinuationBit(final byte in) {
        return (in & CONTINUATION_BIT_MASK) == CONTINUATION_BIT_MASK;
    }

    public static int read(final DataInputStream input) throws IOException {
        int out = 0;
        int bytes = 0;

        byte in;
        do {
            in = input.readByte();
            out |= (in & DATA_BITS_MASK) << bytes++ * DATA_BITS_PER_BYTE;
            if (bytes > MAX_VARINT_SIZE) {
                throw new RuntimeException("VarInt too big");
            }
        } while (hasContinuationBit(in));

        return out;
    }

    public static void write(final DataOutputStream output, int value) throws IOException {
        while ((value & -CONTINUATION_BIT_MASK) != 0) {
            output.writeByte(value & DATA_BITS_MASK | CONTINUATION_BIT_MASK);
            value >>>= DATA_BITS_PER_BYTE;
        }

        output.writeByte(value);
    }
}
