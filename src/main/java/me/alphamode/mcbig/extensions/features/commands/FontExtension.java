package me.alphamode.mcbig.extensions.features.commands;

public interface FontExtension {
    default int width(char character) {
        throw new UnsupportedOperationException();
    }
}
