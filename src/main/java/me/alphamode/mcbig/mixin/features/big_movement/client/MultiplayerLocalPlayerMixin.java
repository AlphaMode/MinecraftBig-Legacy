package me.alphamode.mcbig.mixin.features.big_movement.client;

import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.networking.payload.BigMovePlayerPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiplayerLocalPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.packets.PlayerCommandPacket;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;

@Mixin(MultiplayerLocalPlayer.class)
public abstract class MultiplayerLocalPlayerMixin extends LocalPlayer implements BigEntityExtension {
    @Shadow
    private int lastInventorySendTime;

    @Shadow
    protected abstract void ensureHasSentInventory();

    @Shadow
    private boolean lastSneaked;

    @Shadow
    public ClientPacketListener connection;

    @Shadow
    private int noSendTime;

    @Shadow
    private boolean lastOnGround;

    @Shadow
    private float yRotLast;

    @Shadow
    private float xRotLast;

    @Shadow
    private double yLast1;

    @Shadow
    private double yLast2;

    private BigDecimal xLastBig = BigDecimal.ZERO;
    private BigDecimal zLastBig = BigDecimal.ZERO;

    public MultiplayerLocalPlayerMixin(Minecraft minecraft, Level level, User session, int dimension) {
        super(minecraft, level, session, dimension);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tick() {
        if (this.level.hasChunkAt(BigMath.floor(this.getX()), 64, BigMath.floor(this.getZ()))) {
            super.tick();
            this.sendPosition();
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void sendPosition() {
        if (this.lastInventorySendTime++ == 20) {
            this.ensureHasSentInventory();
            this.lastInventorySendTime = 0;
        }

        boolean var1 = this.isSneaking();
        if (var1 != this.lastSneaked) {
            if (var1) {
                this.connection.send(new PlayerCommandPacket(this, 1));
            } else {
                this.connection.send(new PlayerCommandPacket(this, 2));
            }

            this.lastSneaked = var1;
        }

        double var2 = BigMath.subD(this.getX(), this.xLastBig).doubleValue();
        double var4 = this.bb.y0 - this.yLast1;
        double var6 = this.y - this.yLast2;
        double var8 = BigMath.subD(this.getZ(), this.zLastBig).doubleValue();
        double var10 = this.yRot - this.yRotLast;
        double var12 = this.xRot - this.xRotLast;
        boolean var14 = var4 != 0.0 || var6 != 0.0 || var2 != 0.0 || var8 != 0.0;
        boolean var15 = var10 != 0.0 || var12 != 0.0;
        if (this.riding != null) {
            if (var15) {
                this.connection.sendPayload(new BigMovePlayerPayload.Pos(BigMath.decimal(this.xd), -999.0, -999.0, BigMath.decimal(this.zd), this.onGround));
            } else {
                this.connection.sendPayload(new BigMovePlayerPayload.PosRot(BigMath.decimal(this.xd), -999.0, -999.0, BigMath.decimal(this.zd), this.yRot, this.xRot, this.onGround));
            }

            var14 = false;
        } else if (var14 && var15) {
            this.connection.sendPayload(new BigMovePlayerPayload.PosRot(this.getX(), this.bb.y0, this.y, this.getZ(), this.yRot, this.xRot, this.onGround));
            this.noSendTime = 0;
        } else if (var14) {
            this.connection.sendPayload(new BigMovePlayerPayload.Pos(this.getX(), this.bb.y0, this.y, this.getZ(), this.onGround));
            this.noSendTime = 0;
        } else if (var15) {
            this.connection.sendPayload(new BigMovePlayerPayload.Rot(this.yRot, this.xRot, this.onGround));
            this.noSendTime = 0;
        } else {
            this.connection.sendPayload(new BigMovePlayerPayload.StatusOnly(this.onGround));
            if (this.lastOnGround == this.onGround && this.noSendTime <= 200) {
                this.noSendTime++;
            } else {
                this.noSendTime = 0;
            }
        }

        this.lastOnGround = this.onGround;
        if (var14) {
            this.xLastBig = this.getX();
            this.yLast1 = this.bb.y0;
            this.yLast2 = this.y;
            this.zLastBig = this.getZ();
        }

        if (var15) {
            this.yRotLast = this.yRot;
            this.xRotLast = this.xRot;
        }
    }
}
