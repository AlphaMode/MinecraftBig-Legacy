package me.alphamode.mcbig.mixin.networking.client;

import me.alphamode.mcbig.client.networking.level.BigResetInfo;
import me.alphamode.mcbig.extensions.networking.client.BigMultiPlayerLevelExtension;
import me.alphamode.mcbig.extensions.networking.client.BigMultiplayerChunkCacheExtension;
import me.alphamode.mcbig.math.BigConstants;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.multiplayer.MultiplayerChunkCache;
import net.minecraft.util.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelListener;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.storage.LevelIO;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Set;

@Mixin(MultiPlayerLevel.class)
public abstract class MultiPlayerLevelMixin extends Level implements BigMultiPlayerLevelExtension {
    @Shadow
    private MultiplayerChunkCache chunkCache;
    @Shadow
    private Set<Entity> reEntries;
    @Shadow
    private ClientPacketListener connection;
    private LinkedList<BigResetInfo> updatesToResetBig = new LinkedList<>();

    public MultiPlayerLevelMixin(LevelIO levelIo, String name, Dimension dimension, long seed) {
        super(levelIo, name, dimension, seed);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Override
    public void tick() {
        this.setTime(this.getTime() + 1L);
        int newDark = this.getSkyDarken(1.0F);
        if (newDark != this.skyDarken) {
            this.skyDarken = newDark;

            for (int i = 0; i < this.listeners.size(); i++) {
                this.listeners.get(i).skyColorChanged();
            }
        }

        for (int i = 0; i < 10 && !this.reEntries.isEmpty(); i++) {
            Entity e = this.reEntries.iterator().next();
            if (!this.entities.contains(e)) {
                this.addEntity(e);
            }
        }

        this.connection.tick();

        for (int ix = 0; ix < this.updatesToResetBig.size(); ix++) {
            BigResetInfo r = this.updatesToResetBig.get(ix);
            if (--r.ticks == 0) {
                super.setTileAndDataNoUpdate(r.x(), r.y(), r.z(), r.tile, r.data);
                super.sendTileUpdated(r.x, r.y, r.z);
                this.updatesToResetBig.remove(ix--);
            }
        }
    }

    @Override
    public void clearResetRegion(BigInteger x0, int y0, BigInteger z0, BigInteger x1, int y1, BigInteger z1) {
        for (int i = 0; i < this.updatesToResetBig.size(); i++) {
            BigResetInfo r = this.updatesToResetBig.get(i);
            if (r.x().compareTo(x0) >= 0 && r.y() >= y0 && r.z().compareTo(z0) >= 0 && r.x().compareTo(x1) <= 0 && r.y() <= y1 && r.z().compareTo(z1) <= 0) {
                this.updatesToResetBig.remove(i--);
            }
        }
    }

    @Override
    public void setChunkVisible(BigInteger x, BigInteger z, boolean visible) {
        if (visible) {
            this.chunkCache.loadChunk(x, z);
        } else {
            ((BigMultiplayerChunkCacheExtension) this.chunkCache).unloadChunk(x, z);
        }

        if (!visible) {
            BigInteger xt = x.multiply(BigConstants.SIXTEEN);
            BigInteger zt = z.multiply(BigConstants.SIXTEEN);
            this.setTilesDirty(xt, 0, zt, xt.add(BigConstants.FIFTEEN), 128, zt.add(BigConstants.FIFTEEN));
        }
    }

    @Override
    public boolean setDataNoUpdate(BigInteger x, int y, BigInteger z, int meta) {
        int t = this.getTile(x, y, z);
        int d = this.getData(x, y, z);
        if (super.setDataNoUpdate(x, y, z, meta)) {
            this.updatesToResetBig.add(new BigResetInfo(x, y, z, t, d));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean setTileAndDataNoUpdate(BigInteger x, int y, BigInteger z, int tile, int meta) {
        int t = this.getTile(x, y, z);
        int d = this.getData(x, y, z);
        if (super.setTileAndDataNoUpdate(x, y, z, tile, meta)) {
            this.updatesToResetBig.add(new BigResetInfo(x, y, z, t, d));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean setTileNoUpdate(BigInteger x, int y, BigInteger z, int tile) {
        int t = this.getTile(x, y, z);
        int d = this.getData(x, y, z);
        if (super.setTileNoUpdate(x, y, z, tile)) {
            this.updatesToResetBig.add(new BigResetInfo(x, y, z, t, d));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean doSetTileAndData(BigInteger x, int y, BigInteger z, int tile, int data) {
        this.clearResetRegion(x, y, z, x, y, z);
        if (super.setTileAndDataNoUpdate(x, y, z, tile, data)) {
            this.tileUpdated(x, y, z, tile);
            return true;
        } else {
            return false;
        }
    }
}
