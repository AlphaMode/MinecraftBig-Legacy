package me.alphamode.mcbig.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import me.alphamode.mcbig.networking.StreamCodec;

public interface NetworkArgumentType<T> extends ArgumentType<T> {

    Type<T> type();

    record Type<T>(int id, StreamCodec<NetworkArgumentType<T>> codec) {}
}
