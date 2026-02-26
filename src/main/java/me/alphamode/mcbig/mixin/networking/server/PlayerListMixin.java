package me.alphamode.mcbig.mixin.networking.server;

import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerList;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
    protected abstract ChunkMap getChunkMap(int dimension);

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void addPlayer(ServerPlayer player) {
        this.players.add(player);
        BigEntityExtension bigPlayer = (BigEntityExtension) player;
        ServerLevel level = this.server.getLevel(player.dimension);
        level.serverCache.loadChunk(bigPlayer.getX().toBigInteger().shiftRight(4), bigPlayer.getZ().toBigInteger().shiftRight(4));

        while (level.getCubes(player, bigPlayer.getBigBB()).size() != 0) {
            bigPlayer.setPos(bigPlayer.getX(), player.y + 1.0, bigPlayer.getZ());
        }

        level.addEntity(player);
        this.getChunkMap(player.dimension).addPlayer(player);
    }
}
