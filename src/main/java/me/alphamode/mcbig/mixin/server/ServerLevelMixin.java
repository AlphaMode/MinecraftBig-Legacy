package me.alphamode.mcbig.mixin.server;

import me.alphamode.mcbig.extensions.server.BigServerLevelExtension;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.networking.packets.McBigPayloadPacket;
import me.alphamode.mcbig.networking.payload.BigTileEventPayload;
import net.minecraft.network.packets.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.storage.LevelIO;
import net.minecraft.world.level.tile.entity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements BigServerLevelExtension {
    @Shadow
    private MinecraftServer server;

    public ServerLevelMixin(LevelIO levelIo, String name, Dimension dimension, long seed) {
        super(levelIo, name, dimension, seed);
    }

    @Override
    public List<TileEntity> getTileEntities(BigInteger x0, int y0, BigInteger z0, BigInteger x1, int y1, BigInteger z1) {
        ArrayList<TileEntity> tileEntities = new ArrayList<>();

        for (int i = 0; i < this.tileEntityList.size(); ++i) {
            TileEntity tileEntity = this.tileEntityList.get(i);
            if (tileEntity.getX().compareTo(x0) >= 0 && tileEntity.y >= y0 && tileEntity.getZ().compareTo(z0) >= 0 && tileEntity.getX().compareTo(x1) < 0 && tileEntity.y < y1 && tileEntity.getZ().compareTo(z1) < 0) {
                tileEntities.add(tileEntity);
            }
        }

        return tileEntities;
    }

    @Override
    public boolean mayInteract(Player player, BigInteger x, int y, BigInteger z) {
        BigInteger rX = x.subtract(this.levelData.getBigSpawnX()).abs();
        BigInteger rZ = z.subtract(this.levelData.getBigSpawnZ()).abs();
        if (rX.compareTo(rZ) > 0) {
            rZ = rX;
        }

        return rZ.compareTo(BigConstants.SIXTEEN) > 0 || this.server.playerList.isOp(player.name);
    }

    @Override
    public void tileEvent(BigInteger x, int y, BigInteger z, int b0, int b1) {
        super.tileEvent(x, y, z, b0, b1);
        this.server.playerList.broadcastToAllInRange((double) x.doubleValue(), (double) y, (double) z.doubleValue(), (double) 64.0F, this.dimension.id, new McBigPayloadPacket(new BigTileEventPayload(x, y, z, b0, b1)));
    }
}
