package me.alphamode.mcbig.mixin.networking.client;

import me.alphamode.mcbig.networking.payload.BigPlayerActionPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gamemode.GameMode;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiplayerGameMode;
import net.minecraft.network.packets.PlayerActionPacket;
import net.minecraft.network.packets.UseItemPacket;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(MultiplayerGameMode.class)
public abstract class MultiplayerGameModeMixin extends GameMode {

    public MultiplayerGameModeMixin(Minecraft minecraft) {
        super(minecraft);
    }

    @Shadow
    private int destroyPosY;
    @Shadow
    private float destroyProgress;
    @Shadow
    private float oDestroyProgress;
    @Shadow
    private float destroyTicks;
    @Shadow
    private int destroyDelay;
    @Shadow
    private boolean hasDelayedDestroy;
    @Shadow
    private ClientPacketListener connection;

    private BigInteger destroyPosXBig = BigInteger.ONE.negate();
    private BigInteger destroyPosZBig = BigInteger.ONE.negate();

    @Shadow
    protected abstract void ensureHasSentCarriedItem();

    @Override
    public boolean destroyBlock(BigInteger x, int y, BigInteger z, int face) {
        int t = this.minecraft.level.getTile(x, y, z);
        boolean changed = super.destroyBlock(x, y, z, face);
        ItemInstance item = this.minecraft.player.getSelectedItem();
        if (item != null) {
            item.mineBlock(t, x.intValue(), y, z.intValue(), this.minecraft.player);
            if (item.count == 0) {
                item.snap(this.minecraft.player);
                this.minecraft.player.removeSelectedItem();
            }
        }

        return changed;
    }

    @Override
    public void startDestroyBlock(BigInteger x, int y, BigInteger z, int face) {
        if (!this.hasDelayedDestroy || !x.equals(this.destroyPosXBig) || y != this.destroyPosY || !z.equals(this.destroyPosZBig)) {
            this.connection.sendPayload(new BigPlayerActionPayload(x, y, z, face, BigPlayerActionPayload.START_DESTROY_BLOCK));
            int t = this.minecraft.level.getTile(x, y, z);
            if (t > 0 && this.destroyProgress == 0.0F) {
                Tile.tiles[t].attack(this.minecraft.level, x, y, z, this.minecraft.player);
            }

            if (t > 0 && Tile.tiles[t].getDestroyProgress(this.minecraft.player) >= 1.0F) {
                this.destroyBlock(x, y, z, face);
            } else {
                this.hasDelayedDestroy = true;
                this.destroyPosXBig = x;
                this.destroyPosY = y;
                this.destroyPosZBig = z;
                this.destroyProgress = 0.0F;
                this.oDestroyProgress = 0.0F;
                this.destroyTicks = 0.0F;
            }
        }
    }

    @Override
    public void continueDestroyBlock(BigInteger x, int y, BigInteger z, int face) {
        if (this.hasDelayedDestroy) {
            this.ensureHasSentCarriedItem();
            if (this.destroyDelay > 0) {
                this.destroyDelay--;
            } else {
                if (x.equals(this.destroyPosXBig) && y == this.destroyPosY && z.equals(this.destroyPosZBig)) {
                    int t = this.minecraft.level.getTile(x, y, z);
                    if (t == 0) {
                        this.hasDelayedDestroy = false;
                        return;
                    }

                    Tile tile = Tile.tiles[t];
                    this.destroyProgress = this.destroyProgress + tile.getDestroyProgress(this.minecraft.player);
                    if (this.destroyTicks % 4.0F == 0.0F && tile != null) {
                        this.minecraft
                                .soundEngine
                                .play(tile.soundType.getStepSound(), x.floatValue() + 0.5F, y + 0.5F, z.floatValue() + 0.5F, (tile.soundType.getVolume() + 1.0F) / 8.0F, tile.soundType.getPitch() * 0.5F);
                    }

                    this.destroyTicks++;
                    if (this.destroyProgress >= 1.0F) {
                        this.hasDelayedDestroy = false;
                        this.connection.sendPayload(new BigPlayerActionPayload(x, y, z, face, BigPlayerActionPayload.STOP_DESTROY_BLOCK));
                        this.destroyBlock(x, y, z, face);
                        this.destroyProgress = 0.0F;
                        this.oDestroyProgress = 0.0F;
                        this.destroyTicks = 0.0F;
                        this.destroyDelay = 5;
                    }
                } else {
                    this.startDestroyBlock(x, y, z, face);
                }
            }
        }
    }

    @Override
    public boolean useItemOn(Player player, Level level, ItemInstance item, BigInteger x, int y, BigInteger z, int face) {
        this.ensureHasSentCarriedItem();
        this.connection.send(new UseItemPacket(x.intValue(), y, z.intValue(), face, player.inventory.getSelected()));
        return super.useItemOn(player, level, item, x, y, z, face);
    }
}
