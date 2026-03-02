package me.alphamode.mcbig.mixin.server;

import me.alphamode.mcbig.extensions.server.BigServerPlayerExtension;
import me.alphamode.mcbig.level.chunk.BigChunkPos;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.networking.payload.BigBlockRegionUpdatePayload;
import net.minecraft.network.packets.Packet;
import net.minecraft.network.packets.SetHealthPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ComplexItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.entity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements BigServerPlayerExtension {
    @Shadow
    public PlayerConnection connection;
    @Shadow
    public MinecraftServer server;

    @Shadow
    protected abstract void broadcast(TileEntity tileEntity);

    @Shadow
    private int lastSentHealth;
    public final List<BigChunkPos> bigChunks = new LinkedList<>();
    public final Set<BigChunkPos> trackedBigChunks = new HashSet<>();

    public ServerPlayerMixin(Level level) {
        super(level);
    }

    @Override
    public List<BigChunkPos> getBigChunks() {
        return this.bigChunks;
    }

    @Override
    public Set<BigChunkPos> getTrackedBigChunks() {
        return this.trackedBigChunks;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void doTick(boolean force) {
        super.tick();

        for (int var2 = 0; var2 < this.inventory.getContainerSize(); var2++) {
            ItemInstance item = this.inventory.getItem(var2);
            if (item != null && Item.items[item.id].isComplex() && this.connection.countDelayedPackets() <= 2) {
                Packet packet = ((ComplexItem) Item.items[item.id]).getUpdatePacket(item, this.level, this);
                if (packet != null) {
                    this.connection.send(packet);
                }
            }
        }

        if (force && !this.bigChunks.isEmpty()) {
            BigChunkPos pos = this.bigChunks.get(0);
            if (pos != null) {
                boolean broadcastChanges = false;
                if (this.connection.countDelayedPackets() < 4) {
                    broadcastChanges = true;
                }

                if (broadcastChanges) {
                    ServerLevel l = this.server.getLevel(this.dimension);
                    this.bigChunks.remove(pos);
                    BigInteger xt = pos.x().multiply(BigConstants.SIXTEEN);
                    BigInteger zt = pos.z().multiply(BigConstants.SIXTEEN);
                    this.connection.sendPayload(new BigBlockRegionUpdatePayload(xt, 0, zt, 16, 128, 16, l));
                    List<TileEntity> tileEntities = l.getTileEntities(xt, 0, zt, xt.add(BigConstants.SIXTEEN), 128, zt.add(BigConstants.SIXTEEN));

                    for (int var6 = 0; var6 < tileEntities.size(); var6++) {
                        this.broadcast(tileEntities.get(var6));
                    }
                }
            }
        }

        if (this.isInsidePortal) {
            if (this.server.settings.getBoolean("allow-nether", true)) {
                if (this.containerMenu != this.inventoryMenu) {
                    this.closeContainer();
                }

                if (this.riding != null) {
                    this.ride(this.riding);
                } else {
                    this.portalTime += 0.0125F;
                    if (this.portalTime >= 1.0F) {
                        this.portalTime = 1.0F;
                        this.changingDimensionDelay = 10;
                        this.server.players.toggleDimension((ServerPlayer) (Object) this);
                    }
                }

                this.isInsidePortal = false;
            }
        } else {
            if (this.portalTime > 0.0F) {
                this.portalTime -= 0.05F;
            }

            if (this.portalTime < 0.0F) {
                this.portalTime = 0.0F;
            }
        }

        if (this.changingDimensionDelay > 0) {
            this.changingDimensionDelay--;
        }

        if (this.health != this.lastSentHealth) {
            this.connection.send(new SetHealthPacket(this.health));
            this.lastSentHealth = this.health;
        }
    }
}
