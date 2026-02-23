package me.alphamode.mcbig.client.networking.level;

import java.math.BigInteger;

public class BigResetInfo {
    public final BigInteger x;
    public final int y;
    public final BigInteger z;
    public int ticks;
    public final int tile;
    public final int data;

    public BigResetInfo(BigInteger x, int y, BigInteger z, int tile, int data) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.ticks = 80;
        this.tile = tile;
        this.data = data;
    }

    public BigInteger x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public BigInteger z() {
        return this.z;
    }


}
