//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.extensions.BigGameModeExtension;
import me.alphamode.mcbig.extensions.client.gamemode.BigCreativeModeExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gamemode.CreativeMode;
import net.minecraft.client.gamemode.GameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(CreativeMode.class)
public abstract class CreativeModeMixin extends GameMode implements BigGameModeExtension {
    @Shadow
    private int destroyDelay;

    public CreativeModeMixin(Minecraft minecraft) {
        super(minecraft);
    }

    @Override
    public boolean useItemOn(Player player, Level level, ItemInstance item, BigInteger x, int y, BigInteger z, int face) {
        int t = level.getTile(x, y, z);
        if (t > 0) {
            if (Tile.tiles[t].use(level, x, y, z, player)) return true;
        }
        if (item == null) return false;
        int aux = item.getAuxValue();
        int count = item.count;
        boolean success = item.useOn(player, level, x, y, z, face);
        item.setDamage(aux);
        item.count = count;
        return success;
    }

    @Override
    public void startDestroyBlock(BigInteger x, int y, BigInteger z, int face) {
        BigCreativeModeExtension.creativeDestroyBlock(this.minecraft, this, x, y, z, face);
        this.destroyDelay = 5;
    }

    @Override
    public void continueDestroyBlock(BigInteger x, int y, BigInteger z, int face) {
        this.destroyDelay--;
        if (this.destroyDelay <= 0) {
            this.destroyDelay = 5;
            BigCreativeModeExtension.creativeDestroyBlock(this.minecraft, this, x, y, z, face);
        }
    }
}
*///? }
