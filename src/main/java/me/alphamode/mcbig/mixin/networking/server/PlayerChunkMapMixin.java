package me.alphamode.mcbig.mixin.networking.server;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.extensions.networking.server.BigPlayerChunkMapExtension;
import me.alphamode.mcbig.level.chunk.BigChunkPos;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.server.level.BigPlayerChunk;
import net.minecraft.server.level.PlayerChunkMap;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerChunkMap.class)
public class PlayerChunkMapMixin implements BigPlayerChunkMapExtension {
    @Shadow
    private int radius;
    @Shadow
    @Final
    private int[][] direction;
    @Shadow
    public List<ServerPlayer> players;
    private final Object2ObjectMap<BigChunkPos, BigPlayerChunk> bigChunks = new Object2ObjectOpenHashMap<>();
    private List<BigPlayerChunk> bigChangedChunks = new ArrayList<>();

    @Override
    public Object2ObjectMap<BigChunkPos, BigPlayerChunk> getBigChunks() {
        return this.bigChunks;
    }

    @Override
    public List<BigPlayerChunk> getBigChangedChunks() {
        return this.bigChangedChunks;
    }

    /**
     * @author AlphaMode
     * @reason big chunks
     */
    @Overwrite
    public void tick() {
        for(int i = 0; i < this.bigChangedChunks.size(); ++i) {
            this.bigChangedChunks.get(i).broadcastChanges();
        }

        this.bigChangedChunks.clear();
    }

    private BigPlayerChunk getChunk(BigInteger x, BigInteger z, boolean force) {
        BigChunkPos pos = new BigChunkPos(x, z);
        BigPlayerChunk serverChunk = this.bigChunks.get(pos);
        if (serverChunk == null && force) {
            serverChunk = new BigPlayerChunk((PlayerChunkMap) (Object) this, x, z);
            this.bigChunks.put(pos, serverChunk);
        }

        return serverChunk;
    }

    @Override
    public void blockChanged(BigInteger x, int y, BigInteger z) {
        BigInteger xc = x.shiftRight(4);
        BigInteger zc = z.shiftRight(4);
        BigPlayerChunk chunk = this.getChunk(xc, zc, false);
        if (chunk != null) {
            chunk.tileChanged(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue());
        }
    }

    /**
     * @author AlphaMode
     * @reason fallback to big version
     */
    @Overwrite
    public void blockChanged(int x, int y, int z) {
        this.blockChanged(BigInteger.valueOf(x), y, BigInteger.valueOf(z));
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void addPlayer(ServerPlayer player) {
        BigEntityExtension bigPlayer = (BigEntityExtension) player;
        BigInteger xc = bigPlayer.getX().toBigInteger().shiftRight(4);
        BigInteger zc = bigPlayer.getZ().toBigInteger().shiftRight(4);
        player.setLastX(bigPlayer.getX());
        player.setLastZ(bigPlayer.getZ());
        int var4 = 0;
        int var5 = this.radius;
        int xo = 0;
        int zo = 0;
        this.getChunk(xc, zc, true).add(player);

        for (int var8 = 1; var8 <= var5 * 2; var8++) {
            for (int var9 = 0; var9 < 2; var9++) {
                int[] var10 = this.direction[var4++ % 4];

                for (int var11 = 0; var11 < var8; var11++) {
                    xo += var10[0];
                    zo += var10[1];
                    this.getChunk(xc.add(BigInteger.valueOf(xo)), zc.add(BigInteger.valueOf(zo)), true).add(player);
                }
            }
        }

        var4 %= 4;

        for (int var13 = 0; var13 < var5 * 2; var13++) {
            xo += this.direction[var4][0];
            zo += this.direction[var4][1];
            this.getChunk(xc.add(BigInteger.valueOf(xo)), zc.add(BigInteger.valueOf(zo)), true).add(player);
        }

        this.players.add(player);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void removePlayer(ServerPlayer player) {
        BigInteger xc = player.getLastX().toBigInteger().shiftRight(4);
        BigInteger zc = player.getLastZ().toBigInteger().shiftRight(4);

        BigInteger bigViewRadius = BigInteger.valueOf(this.radius);

        for (BigInteger x = xc.subtract(bigViewRadius); x.compareTo(xc.add(bigViewRadius)) <= 0; x = x.add(BigInteger.ONE)) {
            for (BigInteger z = zc.subtract(bigViewRadius); z.compareTo(zc.add(bigViewRadius)) <= 0; z = z.add(BigInteger.ONE)) {
                BigPlayerChunk chunk = this.getChunk(x, z, false);
                if (chunk != null) {
                    chunk.remove(player);
                }
            }
        }

        this.players.remove(player);
    }

    private boolean inRange(BigInteger x0, BigInteger z0, BigInteger x1, BigInteger z1) {
        int xd = x0.subtract(x1).intValue();
        int zd = z0.subtract(z1).intValue();
        return xd < -this.radius || xd > this.radius ? false : zd >= -this.radius && zd <= this.radius;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void move(ServerPlayer player) {
        BigEntityExtension bigPlayer = (BigEntityExtension) player;
        BigInteger x0 = bigPlayer.getX().toBigInteger().shiftRight(4);
        BigInteger z0 = bigPlayer.getZ().toBigInteger().shiftRight(4);
        double xd = player.getLastX().subtract(bigPlayer.getX()).doubleValue();
        double zd = player.getLastZ().subtract(bigPlayer.getZ()).doubleValue();
        double distance = xd * xd + zd * zd;
        if (!(distance < 64.0)) {
            BigInteger x1 = player.getLastX().toBigInteger().shiftRight(4);
            BigInteger z1 = player.getLastZ().toBigInteger().shiftRight(4);
            BigInteger xOff = x0.subtract(x1);
            BigInteger zOff = z0.subtract(z1);
            if (!xOff.equals(BigInteger.ZERO) || !zOff.equals(BigInteger.ZERO)) {
                BigInteger bigViewRadius = BigInteger.valueOf(this.radius);
                for (BigInteger x = x0.subtract(bigViewRadius); x.compareTo(x0.add(bigViewRadius)) <= 0; x = x.add(BigInteger.ONE)) {
                    for (BigInteger z = z0.subtract(bigViewRadius); z.compareTo(z0.add(bigViewRadius)) <= 0; z = z.add(BigInteger.ONE)) {
                        if (!this.inRange(x, z, x1, z1)) {
                            this.getChunk(x, z, true).add(player);
                        }

                        if (!this.inRange(x.subtract(xOff), z.subtract(zOff), x0, z0)) {
                            BigPlayerChunk chunk = this.getChunk(x.subtract(xOff), z.subtract(zOff), false);
                            if (chunk != null) {
                                chunk.remove(player);
                            }
                        }
                    }
                }

                player.setLastX(bigPlayer.getX());
                player.setLastZ(bigPlayer.getZ());
            }
        }
    }
}
