package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.extensions.BigGameModeExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gamemode.GameMode;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.LevelEvent;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(GameMode.class)
public class GameModeMixin implements BigGameModeExtension {
    @Shadow @Final protected Minecraft minecraft;

    @Override
    public void startDestroyBlock(BigInteger x, int y, BigInteger z, int face) {
        this.minecraft.level.extinguishFire(this.minecraft.player, x, y, z, face);
        this.destroyBlock(x, y, z, face);
    }

    @Override
    public boolean destroyBlock(BigInteger x, int y, BigInteger z, int face) {
        Level level = this.minecraft.level;
        Tile tile = Tile.tiles[level.getTile(x, y, z)];
        level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, x, y, z, tile.id + level.getData(x, y, z) * 256);
        int data = level.getData(x, y, z);
        boolean success = level.setTile(x, y, z, 0);
        if (tile != null && success) {
            tile.destroy(level, x, y, z, data);
        }

        return success;
    }

    @Override
    public boolean useItemOn(Player player, Level level, ItemInstance item, BigInteger x, int y, BigInteger z, int face) {
        int tile = level.getTile(x, y, z);
        if (tile > 0 && Tile.tiles[tile].use(level, x, y, z, player)) {
            return true;
        } else {
            return item != null && item.useOn(player, level, x, y, z, face);
        }
    }
}
