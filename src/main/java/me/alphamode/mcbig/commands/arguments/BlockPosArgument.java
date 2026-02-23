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
import me.alphamode.mcbig.util.BigCoordinates;
import me.alphamode.mcbig.util.BigWorldCoordinate;
import me.alphamode.mcbig.world.phys.BigVec3i;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

public class BlockPosArgument implements ArgumentType<BigCoordinates.BigIntegerCoordinates> {
    public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(new LiteralMessage("Incomplete (expected 3 coordinates)"));

    public static BigInteger readBigInteger(final StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }
        String number = reader.getString().substring(start, reader.getCursor());
        return new BigInteger(number);
    }

    public static BigWorldCoordinate<BigInteger> parseBigInteger(final StringReader reader) throws CommandSyntaxException {
        boolean relative = isRelative(reader);
        int start = reader.getCursor();
        BigInteger value = reader.canRead() && reader.peek() != ' ' ? readBigInteger(reader) : BigInteger.ZERO;
        String number = reader.getString().substring(start, reader.getCursor());
        if (relative && number.isEmpty()) {
            return new BigWorldCoordinate<>(true, new BigWorldCoordinate.BigIntegerValue(BigInteger.ZERO));
        } else {
            return new BigWorldCoordinate<>(relative, new BigWorldCoordinate.BigIntegerValue(value));
        }
    }

    public static BigWorldCoordinate<Integer> parseInteger(final StringReader reader) throws CommandSyntaxException {
        boolean relative = isRelative(reader);
        int start = reader.getCursor();
        int value = reader.canRead() && reader.peek() != ' ' ? reader.readInt() : 0;
        String number = reader.getString().substring(start, reader.getCursor());
        if (relative && number.isEmpty()) {
            return new BigWorldCoordinate<>(true, new BigWorldCoordinate.IntValue(0));
        } else {
            return new BigWorldCoordinate<>(relative, new BigWorldCoordinate.IntValue(value));
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

    public static BigVec3i getBlockPos(final CommandContext<CommandSource> context, final String name) {
        return context.getArgument(name, BigCoordinates.class).getBlockPos(context.getSource());
    }

    @Override
    public BigCoordinates.BigIntegerCoordinates parse(StringReader reader) throws CommandSyntaxException {
        BigWorldCoordinate<BigInteger> x = parseBigInteger(reader);
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            BigWorldCoordinate<Integer> y = parseInteger(reader);
            if (reader.canRead() && reader.peek() == ' ') {
                reader.skip();
                BigWorldCoordinate<BigInteger> z = parseBigInteger(reader);
                return new BigCoordinates.BigIntegerCoordinates(x, y, z);
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
