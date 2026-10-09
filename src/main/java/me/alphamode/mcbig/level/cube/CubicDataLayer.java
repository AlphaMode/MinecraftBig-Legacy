package me.alphamode.mcbig.level.cube;

public class CubicDataLayer {
    public final byte[] data;

    public CubicDataLayer(int size) {
        this.data = new byte[size >> 1];
    }

    public CubicDataLayer(byte[] data) {
        this.data = data;
    }

    public int get(int x, int y, int z) {
        int var4 = x << 8 | z << 4 | y;
        int var5 = var4 >> 1;
        int var6 = var4 & 1;
        return var6 == 0 ? this.data[var5] & 15 : this.data[var5] >> 4 & 15;
    }

    public void set(int x, int y, int z, int data) {
        int var5 = x << 8 | z << 4 | y;
        int var6 = var5 >> 1;
        int var7 = var5 & 1;
        if (var7 == 0) {
            this.data[var6] = (byte)(this.data[var6] & 240 | data & 15);
        } else {
            this.data[var6] = (byte)(this.data[var6] & 15 | (data & 15) << 4);
        }
    }

    public boolean isValid() {
        return this.data != null;
    }
}
