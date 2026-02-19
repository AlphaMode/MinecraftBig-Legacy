package me.alphamode.mcbig.client.commands.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.alphamode.mcbig.commands.CommandSource;
import me.alphamode.mcbig.commands.Commands;
import me.alphamode.mcbig.util.Mth2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.Screen;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandSuggestions extends GuiComponent {
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");

    private final Minecraft minecraft;
    private final Screen screen;
    private final ChatEditBox input;
    private final Font font;
    private final int lineStartOffset;
    private final int suggestionLineLimit;
    private final int fillColor;
    private final List<String> commandUsage = Lists.newArrayList();
    private int commandUsagePosition;
    private int commandUsageWidth;
    @Nullable
    private ParseResults<CommandSource> currentParse;
    @Nullable
    private CompletableFuture<Suggestions> pendingSuggestions;
    private SuggestionList suggestions;
    private boolean allowSuggestions;
    private boolean keepSuggestions;
    private boolean allowHiding = false;
    private final boolean commandsOnly = true;

    public CommandSuggestions(
            final Minecraft minecraft,
            final Screen screen,
            final ChatEditBox input,
            final Font font,
            final int lineStartOffset,
            final int suggestionLineLimit,
            final int fillColor
    ) {
        this.minecraft = minecraft;
        this.screen = screen;
        this.input = input;
        this.font = font;
        this.lineStartOffset = lineStartOffset;
        this.suggestionLineLimit = suggestionLineLimit;
        this.fillColor = fillColor;
    }

    public boolean keyPressed(char eventCharacter, int eventKey) {
        boolean isVisible = this.suggestions != null;
        if (isVisible && this.suggestions.keyPressed(eventCharacter, eventKey)) {
            return true;
        } else if (this.allowHiding && !isVisible) {
            return false;
        } else {
            this.showSuggestions();
            return true;
        }
    }

    public void setAllowSuggestions(final boolean allowSuggestions) {
        this.allowSuggestions = allowSuggestions;
        if (!allowSuggestions) {
            this.suggestions = null;
        }
    }

    public boolean mouseClicked(final int mouseX, final int mouseY, final int button) {
        return this.suggestions != null && this.suggestions.mouseClicked(mouseX, mouseY, button);
    }

    public void showSuggestions() {
        if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
            Suggestions suggestions = this.pendingSuggestions.join();
            if (!suggestions.isEmpty()) {
                int maxSuggestionWidth = 0;

                for (Suggestion suggestion : suggestions.getList()) {
                    maxSuggestionWidth = Math.max(maxSuggestionWidth, this.font.width(suggestion.getText()));
                }

                int x = Mth2.clamp(this.input.getScreenX(suggestions.getRange().getStart()), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - maxSuggestionWidth);
                int y = this.screen.height - 12;
                this.suggestions = new SuggestionList(x, y, maxSuggestionWidth, this.sortSuggestions(suggestions));
            }
        }
    }

    public void hide() {
        this.suggestions = null;
    }

    private List<Suggestion> sortSuggestions(final Suggestions suggestions) {
        String partialCommand = this.input.getValue().substring(0, this.input.getCursorPosition());
        int lastWordIndex = getLastWordIndex(partialCommand);
        String lastWord = partialCommand.substring(lastWordIndex).toLowerCase(Locale.ROOT);
        List<Suggestion> suggestionList = Lists.newArrayList();
        List<Suggestion> partial = Lists.newArrayList();

        for (Suggestion suggestion : suggestions.getList()) {
            if (!suggestion.getText().startsWith(lastWord) && !suggestion.getText().startsWith("minecraft:" + lastWord)) {
                partial.add(suggestion);
            } else {
                suggestionList.add(suggestion);
            }
        }

        suggestionList.addAll(partial);
        return suggestionList;
    }

    public void updateCommandInfo() {
        String command = this.input.getValue();
        if (this.currentParse != null && !this.currentParse.getReader().getString().equals(command)) {
            this.currentParse = null;
        }

        if (!this.keepSuggestions) {
            this.input.setSuggestion(null);
            this.suggestions = null;
        }

        this.commandUsage.clear();
        StringReader reader = new StringReader(command);
        boolean startsWithSlash = reader.canRead() && reader.peek() == '/';
        if (startsWithSlash) {
            reader.skip();
        }

        boolean isCommand = this.commandsOnly || startsWithSlash;
        int cursorPosition = this.input.getCursorPosition();
        if (isCommand) {
            CommandDispatcher<CommandSource> commands = Commands.DISPATCHER;
            if (this.currentParse == null) {
                this.currentParse = commands.parse(reader, this.minecraft.player.getCommandSource());
            }

            int parseStart = 1;
            if (cursorPosition >= parseStart && (this.suggestions == null || !this.keepSuggestions)) {
                this.pendingSuggestions = commands.getCompletionSuggestions(this.currentParse, cursorPosition);
                this.pendingSuggestions.thenRun(() -> {
                    if (this.pendingSuggestions.isDone()) {
                        this.updateUsageInfo();
                    }
                });
            }
        } /*else { we don't care about non commands right now
            String partialCommand = command.substring(0, cursorPosition);
            int lastWord = getLastWordIndex(partialCommand);
            Collection<String> nonCommandSuggestions = this.minecraft.player.connection.getSuggestionsProvider().getCustomTabSugggestions();
            this.pendingSuggestions = SharedSuggestionProvider.suggest(nonCommandSuggestions, new SuggestionsBuilder(partialCommand, lastWord));
        }*/
    }

    private static int getLastWordIndex(final String text) {
        if (Strings.isNullOrEmpty(text)) {
            return 0;
        } else {
            int result = 0;
            Matcher matcher = WHITESPACE_PATTERN.matcher(text);

            while (matcher.find()) {
                result = matcher.end();
            }

            return result;
        }
    }

    private void updateUsageInfo() {
        boolean trailingCharacters = false;
        if (this.input.getCursorPosition() == this.input.getValue().length()) {
            if (this.pendingSuggestions.join().isEmpty() && !this.currentParse.getExceptions().isEmpty()) {
                int literals = 0;

                for (Map.Entry<CommandNode<CommandSource>, CommandSyntaxException> entry : this.currentParse.getExceptions().entrySet()) {
                    CommandSyntaxException exception = entry.getValue();
                    if (exception.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                        literals++;
                    } else {
                        this.commandUsage.add(exception.getMessage());
                    }
                }

                if (literals > 0) {
                    this.commandUsage
                            .add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(this.currentParse.getReader()).getMessage());
                }
            } else if (this.currentParse.getReader().canRead()) {
                trailingCharacters = true;
            }
        }

        this.commandUsagePosition = 0;
        this.commandUsageWidth = this.screen.width;
        if (this.commandUsage.isEmpty() && !this.fillNodeUsage() && trailingCharacters) {
            this.commandUsage.add(Commands.getParseException(this.currentParse).getMessage());
        }

        this.suggestions = null;
        if (this.allowSuggestions/* && this.minecraft.options.autoSuggestions().get()*/) {
            this.showSuggestions();
        }
    }

    private boolean fillNodeUsage() {
        CommandContextBuilder<CommandSource> rootContext = this.currentParse.getContext();
        SuggestionContext<CommandSource> suggestionContext = rootContext.findSuggestionContext(this.input.getCursorPosition());
        Map<CommandNode<CommandSource>, String> usage = Commands.DISPATCHER
                .getSmartUsage(suggestionContext.parent, this.minecraft.player.getCommandSource());
        List<String> lines = Lists.newArrayList();
        int longest = 0;

        for (Map.Entry<CommandNode<CommandSource>, String> entry : usage.entrySet()) {
            if (!(entry.getKey() instanceof LiteralCommandNode)) {
                lines.add(entry.getValue());
                longest = Math.max(longest, this.font.width(entry.getValue()));
            }
        }

        if (!lines.isEmpty()) {
            this.commandUsage.addAll(lines);
            this.commandUsagePosition = Mth2.clamp(this.input.getScreenX(suggestionContext.startPos), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - longest);
            this.commandUsageWidth = longest;
            return true;
        } else {
            return false;
        }
    }

    public void render(final int mouseX, final int mouseY) {
        if (!this.renderSuggestions(mouseX, mouseY)) {
            this.renderUsage();
        }
    }

    public boolean renderSuggestions(final int mouseX, final int mouseY) {
        if (this.suggestions != null) {
            this.suggestions.render(mouseX, mouseY);
            return true;
        } else {
            return false;
        }
    }

    public void renderUsage() {
        int y = 0;

        for (String line : this.commandUsage) {
            int lineY = this.screen.height - 14 - 13 - 12 * y;
            fill(this.commandUsagePosition - 1, lineY, this.commandUsagePosition + this.commandUsageWidth + 1, lineY + 12, this.fillColor);
            drawString(this.font, line, this.commandUsagePosition, lineY + 2, -1);
            y++;
        }
    }

    @Nullable
    private static String calculateSuggestionSuffix(final String contents, final String suggestion) {
        return suggestion.startsWith(contents) ? suggestion.substring(contents.length()) : null;
    }

    public class SuggestionList extends GuiComponent {
        private final Area area;
        private final String originalContents;
        private final List<Suggestion> suggestions;
        private int offset;
        private int current;
        private int lastMouseX;
        private int lastMouseY;
        private boolean tabCycles;

        public SuggestionList(final int x, final int y, final int width, final List<Suggestion> suggestions) {
            int listX = x - 1;
            int listY = y - 3 - Math.min(suggestions.size(), suggestionLineLimit) * 12;
            this.area = new Area(listX, listY, width + 1, Math.min(suggestions.size(), suggestionLineLimit) * 12);
            this.originalContents = input.getValue();
            this.suggestions = suggestions;
            this.select(0);
        }

        public void render(final int mouseX, final int mouseY) {
            int limit = Math.min(this.suggestions.size(), suggestionLineLimit);
            int unselectedColor = -5592406;
            boolean hasPrevious = this.offset > 0;
            boolean hasNext = this.suggestions.size() > this.offset + limit;
            boolean limited = hasPrevious || hasNext;
            boolean mouseMoved = lastMouseX != mouseX || lastMouseY != mouseY;

            if (mouseMoved) {
                this.lastMouseX = mouseX;
                this.lastMouseY = mouseY;
            }

            if (limited) {
                fill(this.area.x(), this.area.y() - 1, this.area.x() + this.area.width(), this.area.y(), CommandSuggestions.this.fillColor);
                fill(
                        this.area.x(),
                        this.area.y() + this.area.height(),
                        this.area.x() + this.area.width(),
                        this.area.y() + this.area.height() + 1,
                        CommandSuggestions.this.fillColor
                );
                if (hasPrevious) {
                    for (int x = 0; x < this.area.width(); x++) {
                        if (x % 2 == 0) {
                            fill(this.area.x() + x, this.area.y() - 1, this.area.x() + x + 1, this.area.y(), -1);
                        }
                    }
                }

                if (hasNext) {
                    for (int xx = 0; xx < this.area.width(); xx++) {
                        if (xx % 2 == 0) {
                            fill(
                                    this.area.x() + xx, this.area.y() + this.area.height(), this.area.x() + xx + 1, this.area.y() + this.area.height() + 1, -1
                            );
                        }
                    }
                }
            }

            boolean hovered = false;

            for (int i = 0; i < limit; i++) {
                Suggestion suggestion = this.suggestions.get(i + this.offset);
                fill(
                        this.area.x(), this.area.y() + 12 * i, this.area.x() + this.area.width(), this.area.y() + 12 * i + 12, CommandSuggestions.this.fillColor
                );
                if (mouseX > this.area.x()
                        && mouseX < this.area.x() + this.area.width()
                        && mouseY > this.area.y() + 12 * i
                        && mouseY < this.area.y() + 12 * i + 12) {
                    if (mouseMoved) {
                        this.select(i + this.offset);
                    }

                    hovered = true;
                }

                drawString(
                        CommandSuggestions.this.font, suggestion.getText(), this.area.x() + 1, this.area.y() + 2 + 12 * i, i + this.offset == this.current ? -256 : unselectedColor
                );
            }

            if (hovered) {
                Message tooltip = this.suggestions.get(this.current).getTooltip();
                if (tooltip != null) {
                    String msg = tooltip.getString();
                    int x = mouseX + 12;
                    int y = mouseY - 12;
                    int width = font.width(msg);
                    this.fillGradient(x - 3, y - 3, x + width + 3, y + 8 + 3, -1073741824, -1073741824);
                    font.drawShadow(msg, x, y, -1);
                }
            }

            if (this.area.contains(mouseX, mouseY)) {
//                graphics.requestCursor(CursorTypes.POINTING_HAND);
            }
        }

        public boolean mouseClicked(final int x, final int y, final int button) {
            if (!this.area.contains(x, y) || button != 0) {
                return false;
            } else {
                int line = (y - this.area.y()) / 12 + this.offset;
                if (line >= 0 && line < this.suggestions.size()) {
                    this.select(line);
                    this.useSuggestion();
                }

                return true;
            }
        }

        public boolean keyPressed(char eventCharacter, int eventKey) {
            if (eventKey == Keyboard.KEY_UP) {
                this.cycle(-1);
                this.tabCycles = false;
                return true;
            } else if (eventKey == Keyboard.KEY_DOWN) {
                this.cycle(1);
                this.tabCycles = false;
                return true;
            } else if (eventKey == Keyboard.KEY_TAB) {
                if (this.tabCycles) {
                    this.cycle(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? -1 : 1);
                }

                this.useSuggestion();
                return true;
            } else if (eventKey == Keyboard.KEY_ESCAPE) {
                hide();
                input.setSuggestion(null);
                return true;
            } else {
                return false;
            }
        }

        public void cycle(final int direction) {
            this.select(this.current + direction);
            int first = this.offset;
            int last = this.offset + CommandSuggestions.this.suggestionLineLimit - 1;
            if (this.current < first) {
                this.offset = Mth2.clamp(this.current, 0, Math.max(this.suggestions.size() - CommandSuggestions.this.suggestionLineLimit, 0));
            } else if (this.current > last) {
                this.offset = Mth2.clamp(
                        this.current + lineStartOffset - CommandSuggestions.this.suggestionLineLimit,
                        0,
                        Math.max(this.suggestions.size() - CommandSuggestions.this.suggestionLineLimit, 0)
                );
            }
        }

        public void select(final int index) {
            this.current = index;
            if (this.current < 0) {
                this.current = this.current + this.suggestions.size();
            }

            if (this.current >= this.suggestions.size()) {
                this.current = this.current - this.suggestions.size();
            }

            Suggestion suggestion = this.suggestions.get(this.current);
            input.setSuggestion(CommandSuggestions.calculateSuggestionSuffix(input.getValue(), suggestion.apply(this.originalContents)));
        }

        public void useSuggestion() {
            Suggestion suggestion = this.suggestions.get(this.current);
            keepSuggestions = true;
            input.setValueWithUpdate(suggestion.apply(this.originalContents));
            int end = suggestion.getRange().getStart() + suggestion.getText().length();
            input.setCursorPosition(end);
            input.setHighlightPos(end);
            this.select(this.current);
            keepSuggestions = false;
            this.tabCycles = true;
        }

        public record Area(int x, int y, int width, int height) {
            public boolean contains(final int x, final int y) {
                return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
            }
        }
    }
}
