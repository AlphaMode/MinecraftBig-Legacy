package me.alphamode.mcbig.mixin.server;

import me.alphamode.mcbig.extensions.server.BigServerPlayerGameModeExtension;
import me.alphamode.mcbig.networking.payload.BigTileUpdatePayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin implements BigServerPlayerGameModeExtension {
    @Shadow
    private ServerLevel level;
    @Shadow
    public Player player;
    @Shadow
    private float f_97603496;
    @Shadow
    private int destroyProgressStart;
    @Shadow
    private int destroyY;
    @Shadow
    private int gameTicks;
    @Shadow
    private boolean hasDelayedDestroy;
    @Shadow
    private int delayedDestroyY;
    @Shadow
    private int delayedTickStart;

    private BigInteger destroyXBig = BigInteger.ZERO;
    private BigInteger destroyZBig = BigInteger.ZERO;

    private BigInteger delayedDestroyXBig = BigInteger.ZERO;
    private BigInteger delayedDestroyZBig = BigInteger.ZERO;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tick() {
        this.gameTicks++;
        if (this.hasDelayedDestroy) {
            int a = this.gameTicks - this.delayedTickStart;
            int t = this.level.getTile(this.delayedDestroyXBig, this.delayedDestroyY, this.delayedDestroyZBig);
            if (t != 0) {
                Tile tile = Tile.tiles[t];
                float destroyProgress = tile.getDestroyProgress(this.player) * (a + 1);
                if (destroyProgress >= 1.0F) {
                    this.hasDelayedDestroy = false;
                    this.destroyAndAck(this.delayedDestroyXBig, this.delayedDestroyY, this.delayedDestroyZBig);
                }
            } else {
                this.hasDelayedDestroy = false;
            }
        }
    }

    @Override
    public void handleStartDestroyTile(BigInteger x, int y, BigInteger z, int direction) {
        this.level.extinguishFire(null, x, y, z, direction);
        this.destroyProgressStart = this.gameTicks;
        int t = this.level.getTile(x, y, z);
        if (t > 0) {
            Tile.tiles[t].attack(this.level, x, y, z, this.player);
        }

        if (t > 0 && Tile.tiles[t].getDestroyProgress(this.player) >= 1.0F) {
            this.destroyAndAck(x, y, z);
        } else {
            this.destroyXBig = x;
            this.destroyY = y;
            this.destroyZBig = z;
        }
    }

    @Override
    public void handleStopDestroyTile(BigInteger x, int y, BigInteger z) {
        if (x.equals(this.destroyXBig) && y == this.destroyY && z.equals(this.destroyZBig)) {
            int var4 = this.gameTicks - this.destroyProgressStart;
            int t = this.level.getTile(x, y, z);
            if (t != 0) {
                Tile tile = Tile.tiles[t];
                float destroyProgress = tile.getDestroyProgress(this.player) * (var4 + 1);
                if (destroyProgress >= 0.7F) {
                    this.destroyAndAck(x, y, z);
                } else if (!this.hasDelayedDestroy) {
                    this.hasDelayedDestroy = true;
                    this.delayedDestroyXBig = x;
                    this.delayedDestroyY = y;
                    this.delayedDestroyZBig = z;
                    this.delayedTickStart = this.destroyProgressStart;
                }
            }
        }

        this.f_97603496 = 0.0F;
    }

    @Override
    public boolean destroyTile(BigInteger x, int y, BigInteger z) {
        Tile tile = Tile.tiles[this.level.getTile(x, y, z)];
        int d = this.level.getData(x, y, z);
        boolean destroyed = this.level.setTile(x, y, z, 0);
        if (tile != null && destroyed) {
            tile.destroy(this.level, x, y, z, d);
        }

        return destroyed;
    }

    @Override
    public boolean destroyAndAck(BigInteger x, int y, BigInteger z) {
        int t = this.level.getTile(x, y, z);
        int d = this.level.getData(x, y, z);
        this.level.levelEvent(this.player, 2001, x, y, z, t + this.level.getData(x, y, z) * 256);
        boolean destroyed = this.destroyTile(x, y, z);
        ItemInstance item = this.player.getSelectedItem();
        if (item != null) {
            item.mineBlock(t, x, y, z, this.player);
            if (item.count == 0) {
                item.snap(this.player);
                this.player.removeSelectedItem();
            }
        }

        if (destroyed && this.player.canDestroy(Tile.tiles[t])) {
            Tile.tiles[t].playerDestroy(this.level, this.player, x, y, z, d);
            ((ServerPlayer) this.player).connection.sendPayload(new BigTileUpdatePayload(x, y, z, this.level));
        }

        return destroyed;
    }

    @Override
    public boolean useItemOn(Player player, Level level, ItemInstance item, BigInteger x, int y, BigInteger z, int face) {
        int t = level.getTile(x, y, z);
        if (t > 0 && Tile.tiles[t].use(level, x, y, z, player)) {
            return true;
        } else {
            return item == null ? false : item.useOn(player, level, x, y, z, face);
        }
    }
}
