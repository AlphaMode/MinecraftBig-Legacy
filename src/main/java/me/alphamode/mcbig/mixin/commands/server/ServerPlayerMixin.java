package me.alphamode.mcbig.mixin.commands.server;

import me.alphamode.mcbig.extensions.CommandPlayerExtension;
import me.alphamode.mcbig.networking.payload.AbilitiesPayload;
import me.alphamode.mcbig.networking.payload.FlySpeedPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

    @Shadow
    public PlayerConnection connection;

    public ServerPlayerMixin(Level level) {
        super(level);
    }

    @Override
    public void setFlySpeed(float speed) {
        super.setFlySpeed(speed);
        this.connection.sendPayload(new FlySpeedPayload(speed));
    }

    @Override
    public void setCanFly(boolean canFly) {
        super.setCanFly(canFly);
        this.connection.sendPayload(new AbilitiesPayload(canFly, canNoclip()));
    }

    @Override
    public void setNoclip(boolean noclip) {
        super.setNoclip(noclip);
        this.connection.sendPayload(new AbilitiesPayload(canFly(), noclip));
    }

    @Override
    public void teleport(BigDecimal x, double y, BigDecimal z) {
        this.connection.teleport(x, y, z, this.yRot, this.xRot);
    }
}
