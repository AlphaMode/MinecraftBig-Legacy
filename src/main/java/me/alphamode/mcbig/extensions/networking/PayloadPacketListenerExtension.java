package me.alphamode.mcbig.extensions.networking;

import me.alphamode.mcbig.networking.payload.*;

public interface PayloadPacketListenerExtension {
    default void sendPayload(Payload payload) {
        throw new UnsupportedOperationException();
    }

    default boolean handleConfigure(ConfigurePayload payload) {
        return false;
    }

    default boolean handleBigSetSpawn(BigSetSpawnPositionPayload payload) {
        return false;
    }

    default boolean handleBigMovePlayer(BigMovePlayerPayload payload) {
        return false;
    }

    default boolean handleBigChunkVisibility(BigChunkVisibilityPayload payload) {
        return false;
    }

    default boolean handleBigChunkTilesUpdate(BigChunkTilesUpdatePayload payload) {
        return false;
    }

    default boolean handleBigBlockRegionUpdate(BigBlockRegionUpdatePayload payload) {
        return false;
    }

    default boolean handleBigTileUpdate(BigTileUpdatePayload payload) {
        return false;
    }

    default boolean handleBigTileEvent(BigTileEventPayload payload) {
        return false;
    }

    default boolean handleBigPlayerAction(BigPlayerActionPayload payload) {
        return false;
    }
}
