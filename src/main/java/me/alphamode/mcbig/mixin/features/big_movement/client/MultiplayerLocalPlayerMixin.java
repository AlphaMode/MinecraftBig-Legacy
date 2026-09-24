package me.alphamode.mcbig.mixin.features.big_movement.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.kikugie.fletching_table.mixin.MixinEnvironment;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.networking.payload.BigMovePlayerPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.ClientConnection;
import net.minecraft.client.multiplayer.MultiplayerLocalPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.packet.PlayerCommandPacket;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;

@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
@Mixin(MultiplayerLocalPlayer.class)
public abstract class MultiplayerLocalPlayerMixin extends LocalPlayer implements BigEntityExtension {
    @Shadow
    private int lastInventorySendTime;

    @Shadow
    protected abstract void ensureHasSentInventory();

    @Shadow
    private boolean lastSneaked;

    @Shadow
    public ClientConnection connection;

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

    @Shadow
    public abstract void sendPosition();

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

    @WrapMethod(method = "sendPosition")
    public void sendBigPosition(Operation<Void> original) {
        if (!isBigMovementEnabled()) {
            original.call();
            return;
        }
        if (this.lastInventorySendTime++ == 20) {
            this.ensureHasSentInventory();
            this.lastInventorySendTime = 0;
        }

        boolean sneaking = this.isSneaking();
        if (sneaking != this.lastSneaked) {
            if (sneaking) {
                this.connection.send(new PlayerCommandPacket(this, 1));
            } else {
                this.connection.send(new PlayerCommandPacket(this, 2));
            }

            this.lastSneaked = sneaking;
        }

        double xdd = BigMath.subD(this.getX(), this.xLastBig).doubleValue();
        double ydd1 = getBigBB().y0() - this.yLast1;
        double ydd2 = this.y - this.yLast2;
        double zdd = BigMath.subD(this.getZ(), this.zLastBig).doubleValue();

        double rydd = this.yRot - this.yRotLast;
        double rxdd = this.xRot - this.xRotLast;

        boolean move = ydd1 != 0.0 || ydd2 != 0.0 || xdd != 0.0 || zdd != 0.0;
        boolean rot = rydd != 0.0 || rxdd != 0.0;
        if (this.riding != null) {
            if (rot) {
                this.connection.sendPayload(new BigMovePlayerPayload.Pos(BigMath.decimal(this.xd), -999.0, -999.0, BigMath.decimal(this.zd), this.onGround));
            } else {
                this.connection.sendPayload(new BigMovePlayerPayload.PosRot(BigMath.decimal(this.xd), -999.0, -999.0, BigMath.decimal(this.zd), this.yRot, this.xRot, this.onGround));
            }

            move = false;
        } else if (move && rot) {
            this.connection.sendPayload(new BigMovePlayerPayload.PosRot(this.getX(), getBigBB().y0(), this.y, this.getZ(), this.yRot, this.xRot, this.onGround));
            this.noSendTime = 0;
        } else if (move) {
            this.connection.sendPayload(new BigMovePlayerPayload.Pos(this.getX(), getBigBB().y0(), this.y, this.getZ(), this.onGround));
            this.noSendTime = 0;
        } else if (rot) {
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
        if (move) {
            this.xLastBig = this.getX();
            this.yLast1 = getBigBB().y0();
            this.yLast2 = this.y;
            this.zLastBig = this.getZ();
        }

        if (rot) {
            this.yRotLast = this.yRot;
            this.xRotLast = this.xRot;
        }
    }
}
