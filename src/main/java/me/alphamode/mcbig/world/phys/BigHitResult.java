package me.alphamode.mcbig.world.phys;

import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigHitResult extends HitResult {

    public BigInteger xBig;
    public BigInteger zBig;
    public BigVec3 posBig;

    public BigHitResult(BigInteger x, int y, BigInteger z, int face, BigVec3 pos) {
        super(x.intValue(), y, z.intValue(), face, pos.toVanilla());
        this.xBig = x;
        this.zBig = z;
        this.posBig = pos;
        this.xBig.add(BigInteger.ONE);
    }

    public BigHitResult(BigInteger x, int y, BigInteger z, int face, Vec3 pos) {
        this(x, y, z, face, BigVec3.fromVanilla(pos));
        this.xBig = x;
        this.zBig = z;
        this.xBig.add(BigInteger.ONE);
    }

    public BigHitResult(Entity entity) {
        super(entity);
        this.xBig = BigInteger.ZERO;
        this.zBig = BigInteger.ZERO;
        boolean hasBigMovement = entity.isBigMovementEnabled();
        BigDecimal x;
        if (hasBigMovement) x = ((BigEntityExtension) entity).getX();
        else x = new BigDecimal(entity.x);
        BigDecimal z;
        if (hasBigMovement) z = ((BigEntityExtension) entity).getZ();
        else z = new BigDecimal(entity.z);
        this.posBig = BigVec3.newTemp(x, entity.y, z);
    }
}
