package me.alphamode.mcbig.mixin.networking.server;

import me.alphamode.mcbig.networking.McBigNetworking;
import me.alphamode.mcbig.networking.packets.McBigPayloadPacket;
import me.alphamode.mcbig.networking.payload.ConfigurePayload;
import me.alphamode.mcbig.networking.payload.Payload;
import me.alphamode.mcbig.networking.payload.BigSetSpawnPositionPayload;
import me.alphamode.mcbig.prelaunch.Features;
import me.alphamode.mcbig.world.phys.BigVec3i;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.packets.ChatPacket;
import net.minecraft.network.packets.LoginPacket;
import net.minecraft.network.packets.SetTimePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListener;
import net.minecraft.server.network.ServerLoginPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Mixin(ServerLoginPacketListener.class)
public abstract class ServerLoginPacketListenerMixin extends PacketListener {
    @Shadow
    public Connection connection;

    @Shadow
    public abstract void disconnect(String string);

    @Shadow
    private MinecraftServer server;
    @Shadow
    public static Logger LOGGER;

    @Shadow
    public abstract String m_53213659();

    @Shadow
    public boolean disconnected;
    private boolean isBigClient = false;

    private LoginPacket pendingLoginPacket;

    @Override
    public boolean handleConfigure(ConfigurePayload payload) {
        if (payload.protocolVersion() != McBigNetworking.PROTOCOL_VERSION) {
            disconnect("Unsupported McBig protocol version: " + payload.protocolVersion() + "\n Server is on: " + McBigNetworking.PROTOCOL_VERSION);
        }
        this.connection.setFeatures(payload.features());
        this.handleAcceptedBigLogin(this.pendingLoginPacket);
        this.pendingLoginPacket = null;

        return true;
    }

    public void handleAcceptedBigLogin(LoginPacket packet) {
        ServerPlayer player = this.server.playerList.getPlayerForLogin((ServerLoginPacketListener) (Object) this, packet.userName);
        if (player != null) {
            this.server.playerList.loadPlayer(player);
            player.setLevel(this.server.getLevel(player.dimension));
            LOGGER.info(this.m_53213659() + " logged in with entity id " + player.id + " at (" + player.x + ", " + player.y + ", " + player.z + ")");
            ServerLevel level = this.server.getLevel(player.dimension);
            BigVec3i spawnPos = level.getBigSpawnPos();
            ServerGamePacketListener gamePacketListener = new ServerGamePacketListener(this.server, this.connection, player);
            gamePacketListener.send(new LoginPacket("", player.id, level.getSeed(), (byte)level.dimension.id));
            gamePacketListener.sendPayload(new BigSetSpawnPositionPayload(spawnPos.x(), spawnPos.y(), spawnPos.z()));
            this.server.playerList.sendLevelInfo(player, level);
            this.server.playerList.broadcastAll(new ChatPacket("§e" + player.name + " joined the game."));
            this.server.playerList.addPlayer(player);
            gamePacketListener.teleport(player.x, player.y, player.z, player.yRot, player.xRot);
            this.server.connection.addConnection(gamePacketListener);
            gamePacketListener.send(new SetTimePacket(level.getTime()));
            player.initMenu();
        }

        this.disconnected = true;
    }

    @Inject(method = "handleLogin", at = @At("HEAD"), cancellable = true)
    private void checkMcBigClient(LoginPacket packet, CallbackInfo ci) {
        this.isBigClient = packet.seed == McBigNetworking.MC_BIG_VERSION_MAGIC;

        if (this.isBigClient) {
            this.pendingLoginPacket = packet;
            handleMcBigClient();
            ci.cancel();
        }
    }

    private void handleMcBigClient() {
        List<Features> features = new ArrayList<>();
        for (Features feature : Features.values()) {
            if (feature.isEnabled()) {
                features.add(feature);
            }
        }
        ConfigurePayload payload = new ConfigurePayload(McBigNetworking.PROTOCOL_VERSION, features);
        this.connection.sendPayload(payload);
    }
}
