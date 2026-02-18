package me.alphamode.mcbig.util;

public class Mth2 {
    public static int clamp(final int value, final int min, final int max) {
        return Math.min(Math.max(value, min), max);
    }
}
