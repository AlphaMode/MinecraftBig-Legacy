package me.alphamode.mcbig.client.renderer;

import net.minecraft.client.renderer.Chunk;
import net.minecraft.world.entity.Entity;

import java.util.Comparator;

public class BigDistanceChunkSorter implements Comparator<BigChunk> {
    private double x;
    private double y;
    private double z;

    public BigDistanceChunkSorter(Entity entity) {
        this.x = -entity.x;
        this.y = -entity.y;
        this.z = -entity.z;
    }

    @Override
    public int compare(BigChunk o1, BigChunk o2) {
        double var3 = (double)o1.bigXm.doubleValue() + this.x;
        double var5 = (double)o1.ym + this.y;
        double var7 = (double)o1.bigZm.doubleValue() + this.z;
        double var9 = (double)o2.bigXm.doubleValue() + this.x;
        double var11 = (double)o2.ym + this.y;
        double var13 = (double)o2.bigZm.doubleValue() + this.z;
        return (int)((var3 * var3 + var5 * var5 + var7 * var7 - (var9 * var9 + var11 * var11 + var13 * var13)) * 1024.0);
    }
}
