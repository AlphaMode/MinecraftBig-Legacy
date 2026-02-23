package me.alphamode.mcbig.extensions.networking;

import me.alphamode.mcbig.networking.payload.Payload;
import me.alphamode.mcbig.prelaunch.Features;

import java.util.List;

public interface BigConnectionExtension {
    default void sendPayload(Payload payload) {
        throw new UnsupportedOperationException();
    }

    default List<Features> getFeatures() {
        throw new UnsupportedOperationException();
    }

    default void setFeatures(List<Features> features) {
        throw new UnsupportedOperationException();
    }

    default void setBigConnection(boolean value) {
        throw new UnsupportedOperationException();
    }

    default boolean isBigConnection() {
        throw new UnsupportedOperationException();
    }
}
