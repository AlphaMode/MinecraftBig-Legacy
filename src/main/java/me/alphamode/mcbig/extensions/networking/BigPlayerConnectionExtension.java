package me.alphamode.mcbig.extensions.networking;

import me.alphamode.mcbig.networking.payload.Payload;

import java.math.BigDecimal;

public interface BigPlayerConnectionExtension {
    default void sendPayload(Payload payload) {
        throw new UnsupportedOperationException();
    }

    default void teleport(BigDecimal x, double y, BigDecimal z, float yRot, float xRot) {
        throw new UnsupportedOperationException();
    }
}
