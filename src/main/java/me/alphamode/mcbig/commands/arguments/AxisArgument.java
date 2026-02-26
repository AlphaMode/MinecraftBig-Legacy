package me.alphamode.mcbig.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.alphamode.mcbig.util.Axis;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AxisArgument implements ArgumentType<Axis> {
    @Override
    public Axis parse(StringReader reader) throws CommandSyntaxException {
        return Axis.valueOf(reader.readString().toUpperCase());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        builder.suggest("x");
        builder.suggest("y");
        builder.suggest("z");
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return List.of("x", "y", "z");
    }
}
