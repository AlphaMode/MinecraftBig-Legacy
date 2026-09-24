package me.alphamode.mcbig.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alphamode.mcbig.client.commands.CommandHistory;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

public class Commands<S extends CommandSource> {
    private final CommandDispatcher<S> dispatcher = new CommandDispatcher<>();

    public Commands() {
        init(this.dispatcher);
    }

    public CommandDispatcher<S> getDispatcher() {
        return dispatcher;
    }

    public static <S> LiteralArgumentBuilder<S> literal(String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public static <S extends CommandSource, T> RequiredArgumentBuilder<S, T> argument(final String name, final ArgumentType<T> type) {
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

    public void init(CommandDispatcher<S> dispatcher) {
        HelpCommand.register(dispatcher);
        TeleportCommand.register(dispatcher);
        TeleportConstantCommand.register(dispatcher);
        GiveCommand.register(dispatcher);
        SetBlockCommand.register(dispatcher);
        FlyCommand.register(dispatcher);
        FlySpeedCommand.register(dispatcher);
        TimeCommand.register(dispatcher);
        WeatherCommand.register(dispatcher);
        SetSpawnCommand.register(dispatcher);
        HealthCommand.register(dispatcher);
        SeedCommand.register(dispatcher);
        DimensionCommand.register(dispatcher);
        NoclipCommand.register(dispatcher);
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            dispatcher.register(Commands.<S>literal("debug").executes(context -> {
                BigAABB.USE_VANILLA = !BigAABB.USE_VANILLA;
                return 1;
            }));
        }
    }
}
