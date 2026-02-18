package me.alphamode.mcbig.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.alphamode.mcbig.commands.SuggestionProvider;
import net.minecraft.world.ItemInstance;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ItemArgument implements ArgumentType<ItemInstance> {
    private static List<String> EXAMPLES = List.of("1 2 0", "1 1", "1");
    @Override
    public ItemInstance parse(StringReader reader) throws CommandSyntaxException {
        int id = reader.readInt();
        int count = 0;
        int data = 0;
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            if (reader.canRead()) {
                count = reader.readInt();
                if (reader.canRead() && reader.peek() == ' ') {
                    reader.skip();
                    if (reader.canRead()) {
                        data = reader.readInt();
                    }
                }
            }
        }
        return new ItemInstance(id, count, data);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        final StringReader reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());
        return SuggestionProvider.suggestItems(builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
