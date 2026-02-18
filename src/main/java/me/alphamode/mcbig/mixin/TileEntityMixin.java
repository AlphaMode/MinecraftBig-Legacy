package me.alphamode.mcbig.mixin;

import com.mojang.nbt.CompoundTag;
import me.alphamode.mcbig.extensions.BigTileEntityExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Map;

@Mixin(TileEntity.class)
public class TileEntityMixin implements BigTileEntityExtension {
    @Shadow
    public int y;
    @Shadow
    public int x;
    @Shadow
    public int z;
    @Shadow
    private static Map<Class<? extends TileEntity>, String> classIdMap;
    @Shadow
    public Level level;
    public BigInteger xBig = BigInteger.ZERO;
    public BigInteger zBig = BigInteger.ZERO;

    @Override
    public BigInteger getX() {
        return this.xBig;
    }

    @Override
    public BigInteger getZ() {
        return this.zBig;
    }

    @Override
    public void setX(BigInteger x) {
        this.xBig = x;
        this.x = x.intValue();
    }

    @Override
    public void setZ(BigInteger z) {
        this.zBig = z;
        this.z = z.intValue();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void load(CompoundTag tag) {
        this.setX(new BigInteger(tag.getString("x")));
        this.y = tag.getInt("y");
        this.setZ(new BigInteger(tag.getString("z")));
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void save(CompoundTag tag) {
        String id = classIdMap.get(this.getClass());
        if (id == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        } else {
            tag.putString("id", id);
            tag.putString("x", getX().toString());
            tag.putInt("y", this.y);
            tag.putString("z", getZ().toString());
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getData() {
        return this.level.getData(getX(), this.y, getZ());
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void setChanged() {
        if (this.level != null) {
            this.level.tileEntityChanged(getX(), this.y, getZ(), (TileEntity) (Object) this);
        }
    }

    /**
     * @author
     * @reason
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public double distanceSqrt(double x, double y, double z) {
        double var7 = getX().doubleValue() + 0.5 - x;
        double var9 = this.y + 0.5 - y;
        double var11 = getZ().doubleValue() + 0.5 - z;
        return var7 * var7 + var9 * var9 + var11 * var11;
    }

    /**
     * @author
     * @reason
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public Tile getTile() {
        return Tile.tiles[this.level.getTile(getX(), this.y, getZ())];
    }
}
