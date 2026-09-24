package me.alphamode.mcbig.networking;

import java.io.DataInputStream;
import java.io.IOException;

public interface StreamDecoder<T> {
    T decode(DataInputStream input) throws IOException;
}
