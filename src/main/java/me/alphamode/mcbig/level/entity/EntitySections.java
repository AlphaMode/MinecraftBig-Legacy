package me.alphamode.mcbig.level.entity;

import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.math.BigMath;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class EntitySections {
    private final Level level;
    private final BigInteger x;
    private final BigInteger z;

    private boolean loaded;
    public final List<Entity>[] entityBlocks = new List[8];
    public boolean lastSaveHadEntities = false;

    public EntitySections(Level level, BigInteger x, BigInteger z) {
        this.level = level;
        this.x = x;
        this.z = z;

        for (int i = 0; i < this.entityBlocks.length; i++) {
            this.entityBlocks[i] = new ArrayList<>();
        }
    }

    public BigInteger getX() {
        return this.x;
    }

    public BigInteger getZ() {
        return this.z;
    }

    public void addEntity(Entity entity) {
        this.lastSaveHadEntities = true;
        BigInteger xt;
        BigInteger zt;
        if (entity instanceof BigEntityExtension bigEntity && entity.isBigMovementEnabled()) {
            xt = BigMath.floor(bigEntity.getX().divide(BigConstants.SIXTEEN_F, RoundingMode.HALF_UP));
            zt = BigMath.floor(bigEntity.getZ().divide(BigConstants.SIXTEEN_F, RoundingMode.HALF_UP));
        } else {
            xt = BigMath.floor(entity.x / 16.0);
            zt = BigMath.floor(entity.z / 16.0);
        }
        if (!xt.equals(this.x) || !zt.equals(this.z)) {
            System.out.println("Wrong location! " + entity);
            Thread.dumpStack();
        }

        int yt = Mth.floor(entity.y / 16.0);
        if (yt < 0) {
            yt = 0;
        }

        if (yt >= this.entityBlocks.length) {
            yt = this.entityBlocks.length - 1;
        }

        entity.inChunk = true;
        entity.setXChunk(this.x);
        entity.yChunk = yt;
        entity.setZChunk(this.z);
        this.entityBlocks[yt].add(entity);
    }

    public void removeEntity(Entity e, int yc) {
        if (yc < 0) {
            yc = 0;
        }

        if (yc >= this.entityBlocks.length) {
            yc = this.entityBlocks.length - 1;
        }

        this.entityBlocks[yc].remove(e);
    }

    public void load() {
        this.loaded = true;

        for (List<Entity> entityBlock : this.entityBlocks) {
            this.level.addEntities(entityBlock);
        }
    }

    public void unload() {
        this.loaded = false;

        for (List<Entity> entityBlock : this.entityBlocks) {
            this.level.removeEntities(entityBlock);
        }
    }

    public void getEntities(Entity except, AABB bb, List<Entity> es) {
        int y0 = Mth.floor((bb.y0 - 2.0) / 16.0);
        int y1 = Mth.floor((bb.y1 + 2.0) / 16.0);
        if (y0 < 0) {
            y0 = 0;
        }

        if (y1 >= this.entityBlocks.length) {
            y1 = this.entityBlocks.length - 1;
        }

        for (int yc = y0; yc <= y1; yc++) {
            List<Entity> entities = this.entityBlocks[yc];

            for (Entity e : entities) {
                if (e != except && e.bb.intersects(bb)) {
                    es.add(e);
                }
            }
        }
    }

    public <T extends Entity> void getEntitiesOfClass(Class<? extends T> ec, AABB bb, List<T> es) {
        int y0 = Mth.floor((bb.y0 - 2.0) / 16.0);
        int y1 = Mth.floor((bb.y1 + 2.0) / 16.0);
        if (y0 < 0) {
            y0 = 0;
        }

        if (y1 >= this.entityBlocks.length) {
            y1 = this.entityBlocks.length - 1;
        }

        for (int yc = y0; yc <= y1; yc++) {
            List<Entity> entities = this.entityBlocks[yc];

            for (Entity e : entities) {
                if (ec.isAssignableFrom(e.getClass()) && e.bb.intersects(bb)) {
                    es.add((T) e);
                }
            }
        }
    }
}
