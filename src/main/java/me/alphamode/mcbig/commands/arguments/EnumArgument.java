package me.alphamode.mcbig.commands.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class EnumArgument<T extends Enum<T> & EnumArgument.EnumData> implements ArgumentType<T> {

    private final Class<T> clazz;
    private final T[] values;

    public EnumArgument(Class<T> clazz, T[] values) {
        this.clazz = clazz;
        this.values = values;
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        return Enum.valueOf(this.clazz, reader.readString());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (T value : values) {
            if (value.name().startsWith(builder.getRemaining())) {
                builder.suggest(value.name(), value.description().isEmpty() ? null : new LiteralMessage(value.description()));
            }
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return Arrays.stream(values).map(Enum::name).toList();
    }

    public interface EnumData {
        String description();
    }
}
