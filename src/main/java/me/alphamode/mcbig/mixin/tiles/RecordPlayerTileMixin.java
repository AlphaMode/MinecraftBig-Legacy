package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import me.alphamode.mcbig.extensions.tiles.BigRecordPlayerTileExtension;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.RecordPlayerTile;
import net.minecraft.world.level.tile.TileEntityTile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(RecordPlayerTile.class)
public abstract class RecordPlayerTileMixin extends TileEntityTile implements BigTileExtension, BigRecordPlayerTileExtension {
    protected RecordPlayerTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public boolean use(Level level, BigInteger x, int y, BigInteger z, Player player) {
        if (level.getData(x, y, z) == 0) {
            return false;
        } else {
            this.dropRecording(level, x, y, z);
            return true;
        }
    }

    @Override
    public void setRecord(Level level, BigInteger x, int y, BigInteger z, int recordId) {
        if (!level.isClientSide) {
            RecordPlayerTile.RecordPlayerTileEntity var6 = (RecordPlayerTile.RecordPlayerTileEntity)level.getTileEntity(x, y, z);
            var6.record = recordId;
            var6.setChanged();
            level.setData(x, y, z, 1);
        }
    }

    @Override
    public void dropRecording(Level level, BigInteger x, int y, BigInteger z) {
        if (!level.isClientSide) {
            RecordPlayerTile.RecordPlayerTileEntity var5 = (RecordPlayerTile.RecordPlayerTileEntity)level.getTileEntity(x, y, z);
            int var6 = var5.record;
            if (var6 != 0) {
                level.levelEvent(1005, x, y, z, 0);
                level.playMusic(null, x, y, z);
                var5.record = 0;
                var5.setChanged();
                level.setData(x, y, z, 0);
                float var8 = 0.7F;
                double var9 = level.random.nextFloat() * var8 + (1.0F - var8) * 0.5;
                double var11 = level.random.nextFloat() * var8 + (1.0F - var8) * 0.2 + 0.6;
                double var13 = level.random.nextFloat() * var8 + (1.0F - var8) * 0.5;
                ItemEntity var15 = new ItemEntity(level, x.doubleValue() + var9, y + var11, z.doubleValue() + var13, new ItemInstance(var6, 1, 0));
                var15.throwTime = 10;
                level.addEntity(var15);
            }
        }
    }

    @Override
    public void onRemove(Level level, BigInteger x, int y, BigInteger z) {
        this.dropRecording(level, x, y, z);
        super.onRemove(level, x, y, z);
    }

    @Override
    public void dropResources(Level level, BigInteger x, int y, BigInteger z, int meta, float dropChance) {
        if (!level.isClientSide) {
            super.dropResources(level, x, y, z, meta, dropChance);
        }
    }
}
