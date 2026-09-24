package me.alphamode.mcbig.mixin.networking.server;

import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import net.minecraft.Pos;
import net.minecraft.network.packet.GameEventPacket;
import net.minecraft.network.packet.Packet;
//? >=1.0.0-beta.8.0.r
//import net.minecraft.network.packet.PlayerInfoPacket;
import net.minecraft.network.packet.RespawnPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerList;
import net.minecraft.server.level.PlayerChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Shadow
    public List<ServerPlayer> players;

    @Shadow
    private MinecraftServer server;

    @Shadow
    protected abstract PlayerChunkMap getChunkMap(int dimension);

    @Shadow
    public abstract void sendLevelInfo(ServerPlayer player, ServerLevel level);

    //? >=1.0.0-beta.8.0.r
    //@Shadow public abstract void broadcastAll(Packet packet);

    /**
     * @author
     * @reason
     */
    @Overwrite
    public ServerPlayer respawn(ServerPlayer player, int targetDimension) {
        this.server.getEntityTracker(player.dimension).clear(player);
        this.server.getEntityTracker(player.dimension).removeEntity(player);
        this.getChunkMap(player.dimension).remove(player);
        this.players.remove(player);
        this.server.getLevel(player.dimension).removeEntityImmediately(player);
        Pos pos = player.getRespawnPosition();
        player.dimension = targetDimension;
        ServerPlayer newPlayer = new ServerPlayer(
                this.server, this.server.getLevel(player.dimension), player.name, new ServerPlayerGameMode(this.server.getLevel(player.dimension))
        );
        BigEntityExtension newPlayerB = (BigEntityExtension) newPlayer;
        newPlayer.id = player.id;
        newPlayer.connection = player.connection;
        ServerLevel level = this.server.getLevel(player.dimension);
        //? >=1.0.0-beta.8.0.r {
        /*newPlayer.gameMode.setGameModeForPlayer(player.gameMode.getGameModeForPlayer());
        newPlayer.gameMode.updateGameMode(level.getLevelData().getGameType());
        *///? }
        if (pos != null) {
            Pos spawnPos = Player.checkBedValidRespawnPosition(this.server.getLevel(player.dimension), pos);
            if (spawnPos != null) {
                newPlayer.moveTo(spawnPos.x + 0.5F, spawnPos.y + 0.1F, spawnPos.z + 0.5F, 0.0F, 0.0F);
                newPlayer.setRespawnPosition(pos);
            } else {
                //~ if >=1.0.0-beta.8.0.r '(0)' -> '(0, 0)'
                newPlayer.connection.send(new GameEventPacket(0));
            }
        }

        level.serverCache.create((int) newPlayer.x >> 4, (int) newPlayer.z >> 4);

        while (level.getCubes(newPlayer, newPlayer.bb).size() != 0) {
            newPlayer.setPos(newPlayer.x, newPlayer.y + 1.0, newPlayer.z);
        }

        //? >=1.0.0-beta.8.0.r {
        /*newPlayer.connection.send(new RespawnPacket((byte)newPlayer.dimension, (byte)newPlayer.level.difficulty, newPlayer.level.getSeed(), 128, newPlayer.gameMode.getGameModeForPlayer()));
        *///? } else
        newPlayer.connection.send(new RespawnPacket((byte) newPlayer.dimension));
        newPlayer.connection.teleport(newPlayerB.getX(), newPlayer.y, newPlayerB.getZ(), newPlayer.yRot, newPlayer.xRot);
        this.sendLevelInfo(newPlayer, level);
        this.getChunkMap(newPlayer.dimension).add(newPlayer);
        level.addEntity(newPlayer);
        this.players.add(newPlayer);
        newPlayer.initMenu();
        newPlayer.onRespawn();
        return newPlayer;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void addPlayer(ServerPlayer player) {
        //? >=1.0.0-beta.8.0.r
        //this.broadcastAll(new PlayerInfoPacket(player.name, true, 1000));
        this.players.add(player);
        BigEntityExtension bigPlayer = (BigEntityExtension) player;
        ServerLevel level = this.server.getLevel(player.dimension);
        level.serverCache.create(bigPlayer.getX().toBigInteger().shiftRight(4), bigPlayer.getZ().toBigInteger().shiftRight(4));

        while (level.getCubes(player, bigPlayer.getBigBB()).size() != 0) {
            bigPlayer.setPos(bigPlayer.getX(), player.y + 1.0, bigPlayer.getZ());
        }

        level.addEntity(player);
        this.getChunkMap(player.dimension).add(player);

        //? >=1.0.0-beta.8.0.r {
        /*for (int i = 0; i < this.players.size(); i++) {
            ServerPlayer p = this.players.get(i);
            player.connection.send(new PlayerInfoPacket(p.name, true, p.latency));
        }
        *///? }
    }
}
