package me.alphamode.mcbig.mixin.fixes;

import me.alphamode.mcbig.networking.payload.SpawnerUpdatePayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.level.tile.entity.MobSpawnerTileEntity;
import net.minecraft.world.level.tile.entity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MobSpawnerTileEntity.class)
public class MobSpawnerTileEntityMixin extends TileEntity {
    private static final boolean FIX_SPAWNER_DESYNC = false;

    @Shadow
    private String entityId;

    @Override
    @Environment(EnvType.SERVER)
    public Packet getUpdatePacket() {
        if (FIX_SPAWNER_DESYNC)
            return new SpawnerUpdatePayload(getX(), this.y, getZ(), this.entityId).createPacket();
        return null;
    }
}
