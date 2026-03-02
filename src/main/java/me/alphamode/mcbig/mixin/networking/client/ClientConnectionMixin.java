package me.alphamode.mcbig.mixin.networking.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.extensions.networking.PayloadPacketListenerExtension;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.networking.McBigNetworking;
import me.alphamode.mcbig.networking.packets.McBigPayloadPacket;
import me.alphamode.mcbig.networking.payload.*;
import me.alphamode.mcbig.prelaunch.Features;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConnection;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.packets.*;
import net.minecraft.util.Vec3i;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin extends PacketListener implements PayloadPacketListenerExtension {
    @Shadow
    public abstract void onDisconnect(String reason, Object[] args);

    @Shadow
    private Connection connection;

    @Shadow
    private Minecraft minecraft;

    @Shadow
    private boolean started;

    @Shadow
    private MultiPlayerLevel level;

    @Shadow
    public abstract void send(Packet packet);

    @Shadow
    public abstract void handleMovePlayer(MovePlayerPacket packet);

    @Override
    public void sendPayload(Payload payload) {
        send(new McBigPayloadPacket(payload));
    }

    @WrapOperation(method = "handlePreLogin", at = @At(value = "NEW", target = "(Ljava/lang/String;I)Lnet/minecraft/network/packets/LoginPacket;"))
    private LoginPacket addMcBigMagic(String username, int protocol, Operation<LoginPacket> original) {
        LoginPacket packet = original.call(username, protocol);
        packet.seed = McBigNetworking.MC_BIG_VERSION_MAGIC;
        return packet;
    }

    @Override
    public boolean handleConfigure(ConfigurePayload payload) {
        if (payload.protocolVersion() != McBigNetworking.PROTOCOL_VERSION) {
            onDisconnect("Unsupported McBig protocol version: " + payload.protocolVersion() + "\n Server is on: " + McBigNetworking.PROTOCOL_VERSION, new Object[0]);
        }
        this.connection.setFeatures(payload.features());
        this.connection.setBigConnection(true);

        List<Features> features = new ArrayList<>();
        for (Features feature : Features.values()) {
            if (feature.isEnabled()) {
                features.add(feature);
            }
        }
        this.connection.sendPayload(new ConfigurePayload(McBigNetworking.PROTOCOL_VERSION, features));

        return true;
    }

    @Override
    public boolean handleBigSetSpawn(BigSetSpawnPositionPayload payload) {
        this.minecraft.player.setRespawnPosition(new Vec3i(payload.x().intValue(), payload.y(), payload.z().intValue()));
        this.minecraft.level.getLevelData().setBigSpawnXYZ(payload.x(), payload.y(), payload.z());
        return true;
    }

    @Override
    public boolean handleBigMovePlayer(BigMovePlayerPayload payload) {
        LocalPlayer player = this.minecraft.player;
        BigEntityExtension bigPlayer = (BigEntityExtension) player;
        BigDecimal x = bigPlayer.getX();
        double y = player.y;
        BigDecimal z = bigPlayer.getZ();
        float yRot = player.yRot;
        float xRot = player.xRot;
        if (payload.hasPos()) {
            x = payload.x();
            y = payload.y();
            z = payload.z();
        }

        if (payload.hasRot()) {
            yRot = payload.yRot();
            xRot = payload.xRot();
        }

        player.ySlideOffset = 0.0F;
        player.xd = player.yd = player.zd = 0.0;
        bigPlayer.absMoveTo(x, y, z, yRot, xRot);
        switch (payload) {
            case BigMovePlayerPayload.Pos pos -> {
                this.connection.sendPayload(new BigMovePlayerPayload.Pos(bigPlayer.getX(), bigPlayer.getBigBB().y0(), player.y, bigPlayer.getZ(), pos.onGround()));
            }
            case BigMovePlayerPayload.PosRot posRot -> {
                this.connection.sendPayload(new BigMovePlayerPayload.PosRot(bigPlayer.getX(), bigPlayer.getBigBB().y0(), player.y, bigPlayer.getZ(), posRot.yRot(), posRot.xRot(), posRot.onGround()));
            }
            default -> this.connection.sendPayload(payload);
        }
        if (!this.started) {
            bigPlayer.setXO(bigPlayer.getX());
            this.minecraft.player.yo = this.minecraft.player.y;
            bigPlayer.setZO(bigPlayer.getZ());
            this.started = true;
            this.minecraft.setScreen(null);
        }
        return true;
    }

    @Override
    public boolean handleBigChunkVisibility(BigChunkVisibilityPayload payload) {
        this.level.setChunkVisible(payload.x(), payload.z(), payload.visible());
        return true;
    }

    @Override
    public boolean handleBigChunkTilesUpdate(BigChunkTilesUpdatePayload payload) {
        LevelChunk lc = this.level.getChunk(payload.xc(), payload.zc());
        BigInteger xo = payload.xc().multiply(BigConstants.SIXTEEN);
        BigInteger zo = payload.zc().multiply(BigConstants.SIXTEEN);

        for (int i = 0; i < payload.changes(); i++) {
            int pos = payload.positions()[i];
            int tile = payload.blocks()[i] & 255;
            int data = payload.data()[i];
            int x = pos >> 12 & 15;
            int z = pos >> 8 & 15;
            int y = pos & 255;
            lc.setTileAndData(x, y, z, tile, data);
            BigInteger bigX = BigInteger.valueOf(x).add(xo);
            BigInteger bigZ = BigInteger.valueOf(z).add(zo);
            this.level.clearResetRegion(bigX, y, bigZ, bigX, y, bigZ);
            this.level.setTilesDirty(bigX, y, bigZ, bigX, y, bigZ);
        }

        return true;
    }

    @Override
    public boolean handleBigBlockRegionUpdate(BigBlockRegionUpdatePayload payload) {
        this.level.clearResetRegion(payload.x(), payload.y(), payload.z(), payload.x().add(BigInteger.valueOf(payload.xs() - 1)), payload.y() + payload.ys() - 1, payload.z().add(BigInteger.valueOf(payload.zs() - 1)));
        this.level.setBlocksAndData(payload.x(), payload.y(), payload.z(), payload.xs(), payload.ys(), payload.zs(), payload.buffer());

        return true;
    }

    @Override
    public boolean handleBigTileUpdate(BigTileUpdatePayload payload) {
        this.level.doSetTileAndData(payload.x(), payload.y(), payload.z(), payload.block(), payload.data());
        return true;
    }

    @Override
    public boolean handleBigTileEvent(BigTileEventPayload payload) {
        this.minecraft.level.tileEvent(payload.x(), payload.y(), payload.z(), payload.b0(), payload.b1());
        return true;
    }
}
