package me.alphamode.mcbig.util;

public record WorldCoordinate(boolean relative, double value) {
    public double get(final double original) {
        return this.relative ? this.value + original : this.value;
    }
}
