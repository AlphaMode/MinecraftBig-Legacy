package me.alphamode.mcbig.networking;

import java.io.DataOutputStream;
import java.io.IOException;

public interface StreamEncoder<T> {
    void encode(DataOutputStream output, T data) throws IOException;
}
