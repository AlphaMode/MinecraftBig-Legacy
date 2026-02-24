package me.alphamode.mcbig.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alphamode.mcbig.client.commands.ChatHistory;
import org.jetbrains.annotations.Nullable;

public class Commands {
    public static final CommandDispatcher<CommandSource> DISPATCHER = new CommandDispatcher<>();

    private final ChatHistory chatHistory;

    public Commands(ChatHistory chatHistory) {
        this.chatHistory = chatHistory;
    }

    public ChatHistory chatHistory() {
        return chatHistory;
    }

    public static LiteralArgumentBuilder<CommandSource> literal(String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public static <T> RequiredArgumentBuilder<CommandSource, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    @Nullable
    public static <S> CommandSyntaxException getParseException(final ParseResults<S> parse) {
        if (!parse.getReader().canRead()) {
            return null;
        } else if (parse.getExceptions().size() == 1) {
            return parse.getExceptions().values().iterator().next();
        } else {
            return parse.getContext().getRange().isEmpty()
                    ? CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parse.getReader())
                    : CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parse.getReader());
        }
    }

    static {
        HelpCommand.register(DISPATCHER);
        TeleportCommand.register(DISPATCHER);
        GiveCommand.register(DISPATCHER);
        SetBlockCommand.register(DISPATCHER);
        FlyCommand.register(DISPATCHER);
        TimeCommand.register(DISPATCHER);
        WeatherCommand.register(DISPATCHER);
        SetSpawnCommand.register(DISPATCHER);
        HealthCommand.register(DISPATCHER);
        SeedCommand.register(DISPATCHER);
    }
}
