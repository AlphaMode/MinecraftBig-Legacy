package me.alphamode.mcbig.server.commands;

import me.alphamode.mcbig.commands.CommandSource;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import net.minecraft.server.ConsoleInputSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerList;
import net.minecraft.world.entity.Entity;

import java.math.BigDecimal;
import java.util.logging.Logger;

public class ServerCommandSource implements CommandSource {
    private static Logger logger = Logger.getLogger("Minecraft");
    private final MinecraftServer server;
    private final ConsoleInputSource source;
    private final Entity entity;

    public ServerCommandSource(MinecraftServer server, ConsoleInputSource source) {
        this.server = server;
        this.source = source;
        this.entity = server.players.getPlayerByName(source.getConsoleName());
    }

    public void success(String name, String message) {
        String msg = name + ": " + message;
        this.server.players.broadcastMessageToOps("§7(" + msg + ")");
        logger.info(msg);
    }

    public PlayerList getPlayers() {
        return server.players;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public void info(String message) {
        this.source.info(message);
    }

    @Override
    public String getName() {
        return this.source.getConsoleName();
    }

    @Override
    public void sendMessage(String message) {
        this.source.info(message);
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public BigDecimal getX() {
        if (this.entity == null) return BigDecimal.ZERO;
        return entity.isBigMovementEnabled() ? ((BigEntityExtension) entity).getX() : BigDecimal.valueOf(entity.x);
    }

    @Override
    public BigDecimal getZ() {
        if (this.entity == null) return BigDecimal.ZERO;
        return entity.isBigMovementEnabled() ? ((BigEntityExtension) entity).getZ() : BigDecimal.valueOf(entity.z);
    }
}
