package me.alphamode.mcbig.server.level;

import me.alphamode.mcbig.level.chunk.BigChunkPos;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.networking.payload.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packets.*;
import net.minecraft.server.level.PlayerChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.TileEntity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.SERVER)
public class BigPlayerChunk {
    private List<ServerPlayer> players = new ArrayList<>();
    private final PlayerChunkMap chunkMap;
    private final BigInteger x;
    private final BigInteger z;
    private final BigChunkPos pos;
    private final short[] changedBlocks = new short[10];
    private int changes = 0;
    private int xChangeMin;
    private int xChangeMax;
    private int yChangeMin;
    private int yChangeMax;
    private int zChangeMin;
    private int zChangeMax;

    public BigPlayerChunk(PlayerChunkMap chunkMap, BigInteger x, BigInteger z) {
        this.chunkMap = chunkMap;
        this.x = x;
        this.z = z;
        this.pos = new BigChunkPos(x, z);
        chunkMap.getLevel().serverCache.loadChunk(x, z);
    }

    public void add(ServerPlayer player) {
        if (this.players.contains(player)) {
            throw new IllegalStateException("Failed to add player. " + player + " already is in chunk " + this.x + ", " + this.z);
        } else {
            player.getTrackedBigChunks().add(this.pos);
            player.connection.sendPayload(new BigChunkVisibilityPayload(this.pos.x(), this.pos.z(), true));
            this.players.add(player);
            player.getBigChunks().add(this.pos);
        }
    }

    public void remove(ServerPlayer player) {
        if (this.players.contains(player)) {
            this.players.remove(player);
            if (this.players.size() == 0) {
                this.chunkMap.getBigChunks().remove(this.pos);
                if (this.changes > 0) {
                    this.chunkMap.getBigChangedChunks().remove(this);
                }

                this.chunkMap.getLevel().serverCache.dropNoneSpawnChunk(this.x, this.z);
            }

            player.getBigChunks().remove(this.pos);
            if (player.getTrackedBigChunks().contains(this.pos)) {
                player.connection.sendPayload(new BigChunkVisibilityPayload(this.x, this.z, false));
            }
        }
    }

    public void tileChanged(int x, int y, int z) {
        if (this.changes == 0) {
            this.chunkMap.getBigChangedChunks().add(this);
            this.xChangeMin = this.xChangeMax = x;
            this.yChangeMin = this.yChangeMax = y;
            this.zChangeMin = this.zChangeMax = z;
        }

        if (this.xChangeMin > x) {
            this.xChangeMin = x;
        }

        if (this.xChangeMax < x) {
            this.xChangeMax = x;
        }

        if (this.yChangeMin > y) {
            this.yChangeMin = y;
        }

        if (this.yChangeMax < y) {
            this.yChangeMax = y;
        }

        if (this.zChangeMin > z) {
            this.zChangeMin = z;
        }

        if (this.zChangeMax < z) {
            this.zChangeMax = z;
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
                BigInteger xt = this.x.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf(this.xChangeMin));
                int yt = this.yChangeMin;
                BigInteger zt = this.z.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf(this.zChangeMin));
                this.broadcast(new BigTileUpdatePayload(xt, yt, zt, level));
                if (Tile.isEntityTile[level.getTile(xt, yt, zt)]) {
                    this.broadcast(level.getTileEntity(xt, yt, zt));
                }
            } else if (this.changes == 10) {
                this.yChangeMin = this.yChangeMin / 2 * 2;
                this.yChangeMax = (this.yChangeMax / 2 + 1) * 2;
                BigInteger xt = this.x.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf(this.xChangeMin));
                int yt = this.yChangeMin;
                BigInteger zt = this.z.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf(this.zChangeMin));
                int xs = this.xChangeMax - this.xChangeMin + 1;
                int ys = this.yChangeMax - this.yChangeMin + 2;
                int zs = this.zChangeMax - this.zChangeMin + 1;
                this.broadcast(new BigBlockRegionUpdatePayload(xt, yt, zt, xs, ys, zs, level));
                List<TileEntity> tileEntities = level.getTileEntities(xt, yt, zt, xt.add(BigInteger.valueOf(xs)), yt + ys, zt.add(BigInteger.valueOf(zs)));

                for (int i = 0; i < tileEntities.size(); i++) {
                    this.broadcast(tileEntities.get(i));
                }
            } else {
                this.broadcast(new BigChunkTilesUpdatePayload(this.x, this.z, this.changedBlocks, this.changes, level));

                for (int i = 0; i < this.changes; i++) {
                    BigInteger xt = this.x.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf((this.changes >> 12 & 15)));
                    int yt = this.changes & 0xFF;
                    BigInteger zt = this.z.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf((this.changes >> 8 & 15)));
                    if (Tile.isEntityTile[level.getTile(xt, yt, zt)]) {
                        System.out.println("Sending!");
                        this.broadcast(level.getTileEntity(xt, yt, zt));
                    }
                }
            }

            this.changes = 0;
        }
    }

    private void broadcast(TileEntity te) {
        if (te != null) {
            Packet packet = te.getUpdatePacket();
            if (packet != null) {
                this.broadcast(packet);
            }
        }
    }
}
