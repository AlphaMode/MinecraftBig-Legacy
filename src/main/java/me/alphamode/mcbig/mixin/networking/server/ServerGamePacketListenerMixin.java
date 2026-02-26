package me.alphamode.mcbig.mixin.networking.server;

import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.extensions.networking.BigServerGamePacketListenerExtension;
import me.alphamode.mcbig.extensions.networking.PayloadPacketListenerExtension;
import me.alphamode.mcbig.networking.packets.McBigPayloadPacket;
import me.alphamode.mcbig.networking.payload.BigMovePlayerPayload;
import me.alphamode.mcbig.networking.payload.BigPlayerActionPayload;
import me.alphamode.mcbig.networking.payload.BigTileUpdatePayload;
import me.alphamode.mcbig.networking.payload.Payload;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.minecraft.network.packets.MovePlayerPacket;
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
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.logging.Logger;

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

    @Shadow
    private boolean clientIsFloating;

    @Shadow
    public static Logger LOGGER;

    @Shadow
    public abstract void disconnect(String reason);

    @Shadow
    private int aboveGroundTickCount;

    @Override
    public void sendPayload(Payload payload) {
        send(new McBigPayloadPacket(payload));
    }

    @Override
    public boolean handleBigMovePlayer(BigMovePlayerPayload payload) {
        ServerLevel var2 = this.server.getLevel(this.player.dimension);
        BigEntityExtension bigPlayer = (BigEntityExtension) this.player;
        this.clientIsFloating = true;
        if (!this.awaitingPositionFromClient) {
            double var3 = payload.y() - this.lastGoodY;
//            if (payload.x().equals(this.lastGoodBigX) && var3 * var3 < 0.01 && payload.z().equals(this.lastGoodBigZ)) {
                this.awaitingPositionFromClient = true;
//            }
        }

        if (this.awaitingPositionFromClient) {
            if (this.player.riding != null) {
                float var27 = this.player.yRot;
                float var4 = this.player.xRot;
                this.player.riding.positionRider();
                BigDecimal var28 = bigPlayer.getX();
                double var29 = this.player.y;
                BigDecimal var30 = bigPlayer.getZ();
                BigDecimal x = BigDecimal.ZERO;
                BigDecimal z = BigDecimal.ZERO;
                if (payload.hasRot()) {
                    var27 = payload.yRot();
                    var4 = payload.xRot();
                }

                if (payload.hasPos() && payload.y() == -999.0 && payload.yView() == -999.0) {
                    x = payload.x();
                    z = payload.z();
                }

                this.player.onGround = payload.onGround();
                this.player.doTick(true);
                bigPlayer.bigMove(x.doubleValue(), 0.0, z.doubleValue());
                bigPlayer.absMoveTo(var28, var29, var30, var27, var4);
                this.player.xd = x.doubleValue();
                this.player.zd = z.doubleValue();
                if (this.player.riding != null) {
                    var2.tickEntity(this.player.riding, true);
                }

                if (this.player.riding != null) {
                    this.player.riding.positionRider();
                }

                this.server.playerList.move(this.player);
                this.lastGoodBigX = bigPlayer.getX();
                this.lastGoodY = this.player.y;
                this.lastGoodBigZ = bigPlayer.getZ();
                var2.tick(this.player);
                return true;
            }

            if (this.player.isSleeping()) {
                this.player.doTick(true);
                bigPlayer.absMoveTo(this.lastGoodBigX, this.lastGoodY, this.lastGoodBigZ, this.player.yRot, this.player.xRot);
                var2.tick(this.player);
                return true;
            }

            double var26 = this.player.y;
            this.lastGoodBigX = bigPlayer.getX();
            this.lastGoodY = this.player.y;
            this.lastGoodBigZ = bigPlayer.getZ();
            BigDecimal var5 = bigPlayer.getX();
            double var7 = this.player.y;
            BigDecimal var9 = bigPlayer.getZ();
            float var11 = this.player.yRot;
            float var12 = this.player.xRot;
            boolean hasPos = payload.hasPos();
            if (payload.hasPos() && payload.y() == -999.0 && payload.yView() == -999.0) {
                hasPos = false;
            }

            if (hasPos) {
                var5 = payload.x();
                var7 = payload.y();
                var9 = payload.z();
                double var13 = payload.yView() - payload.y();
                if (!this.player.isSleeping() && (var13 > 1.65 || var13 < 0.1)) {
                    this.disconnect("Illegal stance");
                    LOGGER.warning(this.player.name + " had an illegal stance: " + var13);
                    return true;
                }

                // Our idea's are to grand to have a illegal position
//                if (Math.abs(payload.x) > 3.2E7 || Math.abs(payload.z) > 3.2E7) {
//                    this.disconnect("Illegal position");
//                    return true;
//                }
            }

            if (payload.hasRot()) {
                var11 = payload.yRot();
                var12 = payload.xRot();
            }

            this.player.doTick(true);
            this.player.ySlideOffset = 0.0F;
            bigPlayer.absMoveTo(this.lastGoodBigX, this.lastGoodY, this.lastGoodBigZ, var11, var12);
            if (!this.awaitingPositionFromClient) {
                return true;
            }

            double var32 = var5.subtract(bigPlayer.getX()).doubleValue();
            double var15 = var7 - this.player.y;
            double var17 = var9.subtract(bigPlayer.getZ()).doubleValue();
            double dist = var32 * var32 + var15 * var15 + var17 * var17;
            if (dist > 100.0) {
                LOGGER.warning(this.player.name + " moved too quickly!");
//                this.disconnect("You moved too quickly :( (Hacking?)");
//                return true;
            }

            float var21 = 0.0625F;
            boolean var22 = var2.getCubes(this.player, bigPlayer.getBigBB().copy().deflate(var21, var21, var21)).size() == 0;
            this.player.move(var32, var15, var17);
            var32 = var5.subtract(bigPlayer.getX()).doubleValue();
            var15 = var7 - this.player.y;
            if (var15 > -0.5 || var15 < 0.5) {
                var15 = 0.0;
            }

            var17 = var9.subtract(bigPlayer.getZ()).doubleValue();
            dist = var32 * var32 + var15 * var15 + var17 * var17;
            boolean var23 = false;
            if (dist > 0.0625 && !this.player.isSleeping()) {
                var23 = true;
                LOGGER.warning(this.player.name + " moved wrongly!");
                System.out.println("Got position " + var5 + ", " + var7 + ", " + var9);
                System.out.println("Expected " + this.player.x + ", " + this.player.y + ", " + this.player.z);
            }

            bigPlayer.absMoveTo(var5, var7, var9, var11, var12);
            boolean var24 = var2.getCubes(this.player, bigPlayer.getBigBB().copy().deflate(var21, var21, var21)).size() == 0;
            if (var22 && (var23 || !var24) && !this.player.isSleeping()) {
                this.teleport(this.lastGoodBigX, this.lastGoodY, this.lastGoodBigZ, var11, var12);
                return true;
            }

            BigAABB var25 = bigPlayer.getBigBB().copy().inflate(var21, var21, var21).expand(0.0, -0.55, 0.0);
            if (this.server.allowFlight || var2.containsAnyTiles(var25)) {
                this.aboveGroundTickCount = 0;
            } else if (var15 >= -0.03125) {
                this.aboveGroundTickCount++;
                if (this.aboveGroundTickCount > 80) {
                    LOGGER.warning(this.player.name + " was kicked for floating too long!");
                    this.disconnect("Flying is not enabled on this server");
                    return true;
                }
            }

            this.player.onGround = payload.onGround();
            this.server.playerList.move(this.player);
            this.player.doCheckFallDamage(this.player.y - var26, payload.onGround());
        }
        return true;
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
