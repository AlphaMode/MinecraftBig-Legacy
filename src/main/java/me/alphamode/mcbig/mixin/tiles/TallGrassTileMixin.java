package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.tile.Bush;
import net.minecraft.world.level.tile.TallGrassTile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(TallGrassTile.class)
public class TallGrassTileMixin extends Bush implements BigTileExtension {
    protected TallGrassTileMixin(int id, int texture) {
        super(id, texture);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public int getFoliageColor(LevelSource level, BigInteger x, int y, BigInteger z) {
//        int data = level.getData(x, y, z);
//        if (data == 0) {
//            return 16777215;
//        } else {
//            long hash = x.longValue() * 3129871 + z.longValue() * 6129781 + y;
//            hash = hash * hash * 42317861L + hash * 11L;
//            x = x.add(BigInteger.valueOf(hash >> 14 & 31L));
//            y = (int)(y + (hash >> 19 & 31L));
//            z = z.add(BigInteger.valueOf(hash >> 24 & 31L));
//            double temperature = level.getBiomeSource().getTemperature(x, z);
//            double downfall = level.getBiomeSource().getDownfall(x, z);
//            return GrassColor.get(temperature, downfall);
//        }
//        throw new RuntimeException("Alpha Fix me");
        return 16777215;
    }
}
