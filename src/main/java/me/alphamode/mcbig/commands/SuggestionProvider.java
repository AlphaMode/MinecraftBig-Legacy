package me.alphamode.mcbig.commands;

import com.google.common.collect.Iterators;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class SuggestionProvider {
    public static CompletableFuture<Suggestions> suggestItems(SuggestionsBuilder builder) {
        String contents = builder.getRemaining().toLowerCase(Locale.ROOT);
        filterIds(Item.items, contents, t -> t.id, v -> builder.suggest(v.toString()));
        for (Item value : Item.items) {
            if (value == null)
                continue;
            builder.suggest(value.id, new LiteralMessage(value.getName()));
        }
        return builder.buildFuture();
    }

    static <T> void filterIds(final T[] values, final String contents, final Function<T, Integer> converter, final Consumer<T> consumer) {

    }
}
