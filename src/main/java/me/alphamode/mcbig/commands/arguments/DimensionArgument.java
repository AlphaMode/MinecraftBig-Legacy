package me.alphamode.mcbig.commands.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class DimensionArgument implements ArgumentType<Integer> {
    private static final Dynamic2CommandExceptionType DIMENSION_ID_TOO_SMALL = new Dynamic2CommandExceptionType((found, min) -> new LiteralMessage("Dimension id must not be less than " + min + ", found " + found));
    private static final Dynamic2CommandExceptionType DIMENSION_ID_TOO_BIG = new Dynamic2CommandExceptionType((found, max) -> new LiteralMessage("Dimension id must not be more than " + max + ", found " + found));

    @Override
    public Integer parse(StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final int result = reader.readInt();
        if (result < -1) {
            reader.setCursor(start);
            throw DIMENSION_ID_TOO_SMALL.createWithContext(reader, result, -1);
        }
        if (result > 1) {
            reader.setCursor(start);
            throw DIMENSION_ID_TOO_BIG.createWithContext(reader, result, 1);
        }
        return result;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String contents = builder.getRemaining().toLowerCase(Locale.ROOT);
        if ("sky".startsWith(contents)) builder.suggest(1, new LiteralMessage("Sky Dimension"));
        if ("nether".startsWith(contents) || "hell".startsWith(contents)) builder.suggest(-1, new LiteralMessage("Nether Dimension"));
        if ("overworld".startsWith(contents)) builder.suggest(0, new LiteralMessage("Overworld Dimension"));
        if (contents.startsWith("-")) builder.suggest(-1, new LiteralMessage("Nether Dimension"));
        return builder.buildFuture();
    }
}
