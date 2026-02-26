package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.extensions.BigGameModeExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gamemode.GameMode;
import net.minecraft.client.gamemode.SurvivalGameMode;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(SurvivalGameMode.class)
public abstract class SurvivalGameModeMixin extends GameMode implements BigGameModeExtension {
    @Shadow private float destroyProgress;

    @Shadow private int destroyDelay;

    @Shadow private int yDestroyBlock;

    @Shadow private float destroyTicks;
    @Shadow private float oDestroyProgress;

    private BigInteger xDestroyBlockBig = BigInteger.ONE.negate();
    private BigInteger zDestroyBlockBig = BigInteger.ONE.negate();

    public SurvivalGameModeMixin(Minecraft minecraft) {
        super(minecraft);
    }

    @Override
    public boolean destroyBlock(BigInteger x, int y, BigInteger z, int face) {
        int tile = this.minecraft.level.getTile(x, y, z);
        int data = this.minecraft.level.getData(x, y, z);
        boolean success = super.destroyBlock(x, y, z, face);
        ItemInstance item = this.minecraft.player.getSelectedItem();
        boolean canDestroy = this.minecraft.player.canDestroy(Tile.tiles[tile]);
        if (item != null) {
            item.mineBlock(tile, x, y, z, this.minecraft.player);
            if (item.count == 0) {
                item.snap(this.minecraft.player);
                this.minecraft.player.removeSelectedItem();
            }
        }

        if (success && canDestroy) {
            Tile.tiles[tile].playerDestroy(this.minecraft.level, this.minecraft.player, x, y, z, data);
        }

        return success;
    }

    @Override
    public void startDestroyBlock(BigInteger x, int y, BigInteger z, int face) {
        this.minecraft.level.extinguishFire(this.minecraft.player, x, y, z, face);
        int tile = this.minecraft.level.getTile(x, y, z);
        if (tile > 0 && this.destroyProgress == 0.0F) {
            Tile.tiles[tile].attack(this.minecraft.level, x, y, z, this.minecraft.player);
        }

        if (tile > 0 && Tile.tiles[tile].getDestroyProgress(this.minecraft.player) >= 1.0F) {
            this.destroyBlock(x, y, z, face);
        }
    }

    @Override
    public void continueDestroyBlock(BigInteger x, int y, BigInteger z, int face) {
        if (this.destroyDelay > 0) {
            --this.destroyDelay;
        } else {
            if (x.equals(this.xDestroyBlockBig) && y == this.yDestroyBlock && z.equals(this.zDestroyBlockBig)) {
                int tileId = this.minecraft.level.getTile(x, y, z);
                if (tileId == 0) {
                    return;
                }

                Tile tile = Tile.tiles[tileId];
                this.destroyProgress += tile.getDestroyProgress(this.minecraft.player);
                if (this.destroyTicks % 4.0F == 0.0F && tile != null) {
                    this.minecraft
                            .soundEngine
                            .play(
                                    tile.soundType.getStepSound(),
                                    (float)x.floatValue() + 0.5F,
                                    (float)y + 0.5F,
                                    (float)z.floatValue() + 0.5F,
                                    (tile.soundType.getVolume() + 1.0F) / 8.0F,
                                    tile.soundType.getPitch() * 0.5F
                            );
                }

                ++this.destroyTicks;
                if (this.destroyProgress >= 1.0F) {
                    this.destroyBlock(x, y, z, face);
                    this.destroyProgress = 0.0F;
                    this.oDestroyProgress = 0.0F;
                    this.destroyTicks = 0.0F;
                    this.destroyDelay = 5;
                }
            } else {
                this.destroyProgress = 0.0F;
                this.oDestroyProgress = 0.0F;
                this.destroyTicks = 0.0F;
                this.xDestroyBlockBig = x;
                this.yDestroyBlock = y;
                this.zDestroyBlockBig = z;
            }
        }
    }
}
