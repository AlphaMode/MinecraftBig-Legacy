package me.alphamode.mcbig.world.phys;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.math.BigInteger;

public class BigHitResult extends HitResult {

    public BigInteger xBig;
    public BigInteger zBig;

    public BigHitResult(BigInteger x, int y, BigInteger z, int face, Vec3 pos) {
        super(x.intValue(), y, z.intValue(), face, pos);
        this.xBig = x;
        this.zBig = z;
    }

    public BigHitResult(Entity entity) {
        super(entity);
    }
}
