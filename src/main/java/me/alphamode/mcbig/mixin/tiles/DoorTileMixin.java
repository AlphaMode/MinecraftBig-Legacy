package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.DoorTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(DoorTile.class)
public abstract class DoorTileMixin extends Tile implements BigTileExtension {
    @Shadow
    public abstract void setShape(int dir);

    @Shadow
    public abstract int getDir(int meta);

    protected DoorTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public AABB getTileAABB(Level level, BigInteger x, int y, BigInteger z) {
        this.updateShape(level, x, y, z);
        return super.getTileAABB(level, x, y, z);
    }

    @Override
    public BigAABB getTileBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        this.updateShape(level, x, y, z);
        return super.getTileBigAABB(level, x, y, z);
    }

    @Override
    public AABB getAABB(Level level, BigInteger x, int y, BigInteger z) {
        this.updateShape(level, x, y, z);
        return super.getAABB(level, x, y, z);
    }

    @Override
    public BigAABB getBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        this.updateShape(level, x, y, z);
        return super.getBigAABB(level, x, y, z);
    }

    @Override
    public void updateShape(LevelSource source, BigInteger x, int y, BigInteger z) {
        this.setShape(this.getDir(source.getData(x, y, z)));
    }

    @Override
    public void attack(Level level, BigInteger x, int y, BigInteger z, Player player) {
        this.use(level, x, y, z, player);
    }

    @Override
    public boolean use(Level level, BigInteger x, int y, BigInteger z, Player player) {
        if (this.material == Material.METAL) {
            return true;
        } else {
            int var6 = level.getData(x, y, z);
            if ((var6 & 8) != 0) {
                if (level.getTile(x, y - 1, z) == this.id) {
                    this.use(level, x, y - 1, z, player);
                }

                return true;
            } else {
                if (level.getTile(x, y + 1, z) == this.id) {
                    level.setData(x, y + 1, z, (var6 ^ 4) + 8);
                }

                level.setData(x, y, z, var6 ^ 4);
                level.setTilesDirty(x, y - 1, z, x, y, z);
                level.levelEvent(player, 1003, x, y, z, 0);
                return true;
            }
        }
    }

    public void setOpen(Level level, BigInteger x, int y, BigInteger z, boolean open) {
        int var6 = level.getData(x, y, z);
        if ((var6 & 8) != 0) {
            if (level.getTile(x, y - 1, z) == this.id) {
                this.setOpen(level, x, y - 1, z, open);
            }
        } else {
            boolean var7 = (level.getData(x, y, z) & 4) > 0;
            if (var7 != open) {
                if (level.getTile(x, y + 1, z) == this.id) {
                    level.setData(x, y + 1, z, (var6 ^ 4) + 8);
                }

                level.setData(x, y, z, var6 ^ 4);
                level.setTilesDirty(x, y - 1, z, x, y, z);
                level.levelEvent(null, 1003, x, y, z, 0);
            }
        }
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {
        int var6 = level.getData(x, y, z);
        if ((var6 & 8) != 0) {
            if (level.getTile(x, y - 1, z) != this.id) {
                level.setTile(x, y, z, 0);
            }

            if (tile > 0 && Tile.tiles[tile].isSignalSource()) {
                this.neighborChanged(level, x, y - 1, z, tile);
            }
        } else {
            boolean var7 = false;
            if (level.getTile(x, y + 1, z) != this.id) {
                level.setTile(x, y, z, 0);
                var7 = true;
            }

            if (!level.isSolidBlockingTile(x, y - 1, z)) {
                level.setTile(x, y, z, 0);
                var7 = true;
                if (level.getTile(x, y + 1, z) == this.id) {
                    level.setTile(x, y + 1, z, 0);
                }
            }

            if (var7) {
                if (!level.isClientSide) {
                    this.dropResources(level, x, y, z, var6);
                }
            } else if (tile > 0 && Tile.tiles[tile].isSignalSource()) {
                boolean var8 = level.hasNeighborSignal(x, y, z) || level.hasNeighborSignal(x, y + 1, z);
                this.setOpen(level, x, y, z, var8);
            }
        }
    }

    @Override
    public HitResult clip(Level level, BigInteger x, int y, BigInteger z, Vec3 vec1, Vec3 vec2) {
        this.updateShape(level, x, y, z);
        return super.clip(level, x, y, z, vec1, vec2);
    }

    @Override
    public boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        return y >= 127 ? false : level.isSolidBlockingTile(x, y - 1, z) && super.mayPlace(level, x, y, z) && super.mayPlace(level, x, y + 1, z);
    }
}
