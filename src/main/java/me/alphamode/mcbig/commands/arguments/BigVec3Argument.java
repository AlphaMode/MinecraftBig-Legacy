package me.alphamode.mcbig.commands.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.alphamode.mcbig.commands.CommandSource;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.util.BigCoordinate;
import me.alphamode.mcbig.util.BigWorldCoordinate;
import me.alphamode.mcbig.util.WorldCoordinate;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public class BigVec3Argument implements ArgumentType<BigCoordinate> {
    public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(new LiteralMessage("Incomplete (expected 3 coordinates)"));

    public static BigDecimal readBigDecimal(final StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }
        String number = reader.getString().substring(start, reader.getCursor());
        return new BigDecimal(number);
    }

    public static BigWorldCoordinate parseBidDecimal(final StringReader reader) throws CommandSyntaxException {
        boolean relative = isRelative(reader);
        int start = reader.getCursor();
        BigDecimal value = reader.canRead() && reader.peek() != ' ' ? readBigDecimal(reader) : BigDecimal.ZERO;
        String number = reader.getString().substring(start, reader.getCursor());
        if (relative && number.isEmpty()) {
            return new BigWorldCoordinate(true, BigDecimal.ZERO);
        } else {
            if (!number.contains(".") && !relative) {
                value = value.add(BigConstants.POINT_FIVE);
            }

            return new BigWorldCoordinate(relative, value);
        }
    }

    public static WorldCoordinate parseDouble(final StringReader reader) throws CommandSyntaxException {
        boolean relative = isRelative(reader);
        int start = reader.getCursor();
        double value = reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : 0.0;
        String number = reader.getString().substring(start, reader.getCursor());
        if (relative && number.isEmpty()) {
            return new WorldCoordinate(true, 0.0);
        } else {
            if (!number.contains(".") && !relative) {
                value += 0.5;
            }

            return new WorldCoordinate(relative, value);
        }
    }

    public static boolean isRelative(final StringReader reader) {
        boolean relative;
        if (reader.peek() == '~') {
            relative = true;
            reader.skip();
        } else {
            relative = false;
        }

        return relative;
    }

    @Override
    public BigCoordinate parse(StringReader reader) throws CommandSyntaxException {
        BigWorldCoordinate x = parseBidDecimal(reader);
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            WorldCoordinate y = parseDouble(reader);
            if (reader.canRead() && reader.peek() == ' ') {
                reader.skip();
                BigWorldCoordinate z = parseBidDecimal(reader);
                return new BigCoordinate(x, y, z);
            }
        }
        throw ERROR_NOT_COMPLETE.createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSource commandSource) {

        }
        return ArgumentType.super.listSuggestions(context, builder);
    }
}
