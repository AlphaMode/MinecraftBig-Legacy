package me.alphamode.mcbig.server.level;

import me.alphamode.mcbig.level.chunk.BigChunkPos;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.networking.payload.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packets.*;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.TileEntity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.SERVER)
public class BigServerChunk {
    private List<ServerPlayer> players = new ArrayList<>();
    private final ChunkMap chunkMap;
    private final BigInteger x;
    private final BigInteger z;
    private final BigChunkPos pos;
    private final short[] changedBlocks = new short[10];
    private int changes = 0;
    private int x1;
    private int x0;
    private int y1;
    private int y0;
    private int z1;
    private int z0;

    public BigServerChunk(ChunkMap chunkMap, BigInteger x, BigInteger z) {
        this.chunkMap = chunkMap;
        this.x = x;
        this.z = z;
        this.pos = new BigChunkPos(x, z);
        chunkMap.getLevel().serverCache.loadChunk(x, z);
    }

    public void addPlayer(ServerPlayer player) {
        if (this.players.contains(player)) {
            throw new IllegalStateException("Failed to add player. " + player + " already is in chunk " + this.x + ", " + this.z);
        } else {
            player.getTrackedBigChunks().add(this.pos);
            player.connection.sendPayload(new BigChunkVisibilityPayload(this.pos.x(), this.pos.z(), true));
            this.players.add(player);
            player.getBigChunks().add(this.pos);
        }
    }

    public void removePlayer(ServerPlayer player) {
        if (this.players.contains(player)) {
            this.players.remove(player);
            if (this.players.size() == 0) {
                this.chunkMap.getChunkMap().remove(this.pos);
                if (this.changes > 0) {
                    this.chunkMap.getBigChunks().remove(this);
                }

                this.chunkMap.getLevel().serverCache.dropNoneSpawnChunk(this.x, this.z);
            }

            player.getBigChunks().remove(this.pos);
            if (player.getTrackedBigChunks().contains(this.pos)) {
                player.connection.sendPayload(new BigChunkVisibilityPayload(this.x, this.z, false));
            }
        }
    }

    public void blockChanged(int x, int y, int z) {
        if (this.changes == 0) {
            this.chunkMap.getBigChunks().add(this);
            this.x1 = this.x0 = x;
            this.y1 = this.y0 = y;
            this.z1 = this.z0 = z;
        }

        if (this.x1 > x) {
            this.x1 = x;
        }

        if (this.x0 < x) {
            this.x0 = x;
        }

        if (this.y1 > y) {
            this.y1 = y;
        }

        if (this.y0 < y) {
            this.y0 = y;
        }

        if (this.z1 > z) {
            this.z1 = z;
        }

        if (this.z0 < z) {
            this.z0 = z;
        }

        if (this.changes < 10) {
            short var4 = (short)(x << 12 | z << 8 | y);

            for (int var5 = 0; var5 < this.changes; var5++) {
                if (this.changedBlocks[var5] == var4) {
                    return;
                }
            }

            this.changedBlocks[this.changes++] = var4;
        }
    }

    public void broadcast(Packet packet) {
        for (ServerPlayer player : this.players) {
            if (player.getTrackedBigChunks().contains(this.pos)) {
                player.connection.send(packet);
            }
        }
    }

    public void broadcast(Payload payload) {
        for (ServerPlayer player : this.players) {
            if (player.getTrackedBigChunks().contains(this.pos)) {
                player.connection.sendPayload(payload);
            }
        }
    }

    public void broadcastChanges() {
        ServerLevel level = this.chunkMap.getLevel();
        if (this.changes != 0) {
            if (this.changes == 1) {
                BigInteger xt = this.x.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf(this.x1));
                int yt = this.y1;
                BigInteger zt = this.z.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf(this.z1));
                this.broadcast(new BigTileUpdatePayload(xt, yt, zt, level));
                if (Tile.isEntityTile[level.getTile(xt, yt, zt)]) {
                    this.broadcastTileEntity(level.getTileEntity(xt, yt, zt));
                }
            } else if (this.changes == 10) {
                this.y1 = this.y1 / 2 * 2;
                this.y0 = (this.y0 / 2 + 1) * 2;
                BigInteger xt = this.x.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf(this.x1));
                int yt = this.y1;
                BigInteger zt = this.z.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf(this.z1));
                int xs = this.x0 - this.x1 + 1;
                int ys = this.y0 - this.y1 + 2;
                int zs = this.z0 - this.z1 + 1;
                this.broadcast(new BigBlockRegionUpdatePayload(xt, yt, zt, xs, ys, zs, level));
                List<TileEntity> tileEntities = level.getTileEntities(xt, yt, zt, xt.add(BigInteger.valueOf(xs)), yt + ys, zt.add(BigInteger.valueOf(zs)));

                for (int i = 0; i < tileEntities.size(); i++) {
                    this.broadcastTileEntity(tileEntities.get(i));
                }
            } else {
                this.broadcast(new BigChunkTilesUpdatePayload(this.x, this.z, this.changedBlocks, this.changes, level));

                for (int i = 0; i < this.changes; i++) {
                    BigInteger xt = this.x.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf((this.changes >> 12 & 15)));
                    int yt = this.changes & 0xFF;
                    BigInteger zt = this.z.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf((this.changes >> 8 & 15)));
                    if (Tile.isEntityTile[level.getTile(xt, yt, zt)]) {
                        System.out.println("Sending!");
                        this.broadcastTileEntity(level.getTileEntity(xt, yt, zt));
                    }
                }
            }

            this.changes = 0;
        }
    }

    private void broadcastTileEntity(TileEntity tile) {
        if (tile != null) {
            Packet packet = tile.getUpdatePacket();
            if (packet != null) {
                this.broadcast(packet);
            }
        }
    }
}
