package me.alphamode.mcbig.level;

import net.minecraft.world.level.TickNextTickData;

import java.math.BigInteger;

public class BigTickNextTickData extends TickNextTickData {
    public BigInteger xBig;
    public BigInteger zBig;

    public BigTickNextTickData(BigInteger x, int y, BigInteger z, int priority) {
        super(x.intValue(), y, z.intValue(), priority);
        this.xBig = x;
        this.zBig = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TickNextTickData)) {
            return false;
        } else {
            BigTickNextTickData other = (BigTickNextTickData) obj;
            return this.xBig.equals(other.x) && this.y == other.y && this.zBig.equals(other.zBig) && this.priority == other.priority;
        }
    }
}
