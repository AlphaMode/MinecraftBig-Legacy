package me.alphamode.mcbig.mixin.networking.server;

import me.alphamode.mcbig.extensions.networking.BigServerGamePacketListenerExtension;
import me.alphamode.mcbig.extensions.networking.PayloadPacketListenerExtension;
import me.alphamode.mcbig.networking.packets.McBigPayloadPacket;
import me.alphamode.mcbig.networking.payload.BigMovePlayerPayload;
import me.alphamode.mcbig.networking.payload.BigPlayerActionPayload;
import me.alphamode.mcbig.networking.payload.BigTileUpdatePayload;
import me.alphamode.mcbig.networking.payload.Payload;
import net.minecraft.network.packets.Packet;
import net.minecraft.network.packets.PlayerActionPacket;
import net.minecraft.network.packets.TileUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListener;
import net.minecraft.util.Facing;
import net.minecraft.util.Mth;
import net.minecraft.util.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;
import java.math.BigInteger;

@Mixin(ServerGamePacketListener.class)
public abstract class ServerGamePacketListenerMixin implements BigServerGamePacketListenerExtension, PayloadPacketListenerExtension {
    @Shadow
    public abstract void send(Packet packet);

    @Shadow
    private boolean awaitingPositionFromClient;

    @Shadow
    private ServerPlayer player;

    private BigDecimal lastGoodBigX = BigDecimal.ZERO;
    private BigDecimal lastGoodBigZ = BigDecimal.ZERO;

    @Shadow
    private double lastGoodX;

    @Shadow
    private double lastGoodY;

    @Shadow
    private double lastGoodZ;

    @Shadow
    private MinecraftServer server;

    @Override
    public void sendPayload(Payload payload) {
        send(new McBigPayloadPacket(payload));
    }

    @Override
    public void teleport(BigDecimal bx, double y, BigDecimal bz, float yRot, float xRot) {
        this.awaitingPositionFromClient = false;
        double x = bx.doubleValue();
        double z = bz.doubleValue();
        this.lastGoodX = x;
        this.lastGoodZ = z;
        this.lastGoodBigX = bx;
        this.lastGoodY = y;
        this.lastGoodBigZ = bz;
        this.player.absMoveTo(x, y, z, yRot, xRot);
        this.player.connection.sendPayload(new BigMovePlayerPayload.PosRot(bx, y, y + 1.62F, bz, yRot, xRot, false));
    }

    @Override
    public boolean handleBigPlayerAction(BigPlayerActionPayload payload) {
        ServerLevel level = this.server.getLevel(this.player.dimension);
        if (payload.action() == BigPlayerActionPayload.DROP_ITEM) {
            this.player.drop();
        } else {
            boolean canUpdate = level.f_55807453 = level.dimension.id != 0 || this.server.playerList.isOp(this.player.name);
            boolean var4 = false;
            if (payload.action() == BigPlayerActionPayload.START_DESTROY_BLOCK) {
                var4 = true;
            }

            if (payload.action() == BigPlayerActionPayload.STOP_DESTROY_BLOCK) {
                var4 = true;
            }

            BigInteger x = payload.x();
            int y = payload.y();
            BigInteger z = payload.z();
            if (var4) {
                double var8 = this.player.x - (x.doubleValue() + 0.5);
                double var10 = this.player.y - (y + 0.5);
                double var12 = this.player.z - (z.doubleValue() + 0.5);
                double var14 = var8 * var8 + var10 * var10 + var12 * var12;
                if (var14 > 36.0) {
                    return true;
                }
            }

            Vec3i spawnPos = level.getSpawnPos();
            int var9 = (int) Mth.abs(x.floatValue() - spawnPos.x);
            int var20 = (int) Mth.abs(z.floatValue() - spawnPos.z);
            if (var9 > var20) {
                var20 = var9;
            }

            if (payload.action() == BigPlayerActionPayload.START_DESTROY_BLOCK) {
                if (var20 <= 16 && !canUpdate) {
                    this.player.connection.sendPayload(new BigTileUpdatePayload(x, y, z, level));
                } else {
                    this.player.gameMode.handleStartDestroyTile(x, y, z, payload.face());
                }
            } else if (payload.action() == BigPlayerActionPayload.STOP_DESTROY_BLOCK) {
                this.player.gameMode.handleStopDestroyTile(x, y, z);
                if (level.getTile(x, y, z) != 0) {
                    this.player.connection.sendPayload(new BigTileUpdatePayload(x, y, z, level));
                }
            } else if (payload.action() == BigPlayerActionPayload.GET_UPDATED_BLOCK) {
                double var11 = this.player.x - (x.doubleValue() + 0.5);
                double var13 = this.player.y - (y + 0.5);
                double var15 = this.player.z - (z.doubleValue() + 0.5);
                double var17 = var11 * var11 + var13 * var13 + var15 * var15;
                if (var17 < 256.0) {
                    this.player.connection.sendPayload(new BigTileUpdatePayload(x, y, z, level));
                }
            }

            level.f_55807453 = false;
        }

        return true;
    }
}
