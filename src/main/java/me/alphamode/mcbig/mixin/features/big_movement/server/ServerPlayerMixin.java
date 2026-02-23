package me.alphamode.mcbig.mixin.features.big_movement.server;

import me.alphamode.mcbig.extensions.server.BigServerPlayerExtension;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigDecimal;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements BigServerPlayerExtension {
    public BigDecimal lastXBig = BigDecimal.ZERO;
    public BigDecimal lastZBig = BigDecimal.ZERO;

    @Override
    public void setLastX(BigDecimal x) {
        this.lastXBig = x;
    }

    @Override
    public BigDecimal getLastX() {
        return this.lastXBig;
    }

    @Override
    public void setLastZ(BigDecimal z) {
        this.lastZBig = z;
    }

    @Override
    public BigDecimal getLastZ() {
        return this.lastZBig;
    }
}
