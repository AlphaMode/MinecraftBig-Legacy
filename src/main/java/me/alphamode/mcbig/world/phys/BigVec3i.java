package me.alphamode.mcbig.world.phys;

import java.math.BigInteger;

public record BigVec3i(BigInteger x, int y, BigInteger z) {
    public static final BigVec3i ZERO =  new BigVec3i(BigInteger.ZERO, 0, BigInteger.ZERO);

    public BigVec3i subtract(BigVec3i value) {
        return new BigVec3i(x.subtract(value.x), y - value.y, z.subtract(value.z));
    }

    public BigVec3i offset(final BigInteger x, final int y, final BigInteger z) {
        return new BigVec3i(this.x.add(x), this.y + y, this.z.add(z));
    }

    public BigVec3i above(int steps) {
        return new BigVec3i(this.x, this.y + steps, this.z);
    }
}
