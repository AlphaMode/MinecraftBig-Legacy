package me.alphamode.mcbig.client.commands.gui;

import me.alphamode.mcbig.util.Mth2;
import me.alphamode.mcbig.util.ScreenUtil;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.Tesselator;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ChatEditBox extends GuiComponent {
    private final Font font;
    private final int x, y;
    private final int width, height;

    private final Consumer<String> valueSetter;
    private final Supplier<String> valueGetter;

    private int displayPos;
    private int cursorPos;
    private int highlightPos;

    @Nullable
    private Consumer<String> responder;

    @Nullable
    private String suggestion;

    public ChatEditBox(final Font font, final int x, final int y, final int width, final int height, final Consumer<String> valueSetter, final Supplier<String> valueGetter) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.font = font;
        this.valueSetter = valueSetter;
        this.valueGetter = valueGetter;
    }

    public void setResponder(final Consumer<String> responder) {
        this.responder = responder;
    }

    private void onValueChange(final String value) {
        if (this.responder != null) {
            this.responder.accept(value);
        }
    }

    public String getHighlighted() {
        int start = Math.min(this.cursorPos, this.highlightPos);
        int end = Math.max(this.cursorPos, this.highlightPos);
        return this.getValue().substring(start, end);
    }

    public void render(boolean showCursor, int color) {
        drawString(this.font, "> ", this.x, this.y, color);

        int relCursorPos = this.cursorPos - this.displayPos;

        String value = getValue();
        String displayed = getValue();
        boolean cursorOnScreen = relCursorPos >= 0 && relCursorPos <= displayed.length();

        int textX = this.x + this.font.width("> ");
        int textY = this.y;

        int drawX = textX;
        int relHighlightPos = Mth2.clamp(this.highlightPos - this.displayPos, 0, displayed.length());
        if (!displayed.isEmpty()) {
            String half = cursorOnScreen ? displayed.substring(0, relCursorPos) : displayed;
            drawString(this.font, half, drawX, this.y, color);
            drawX += this.font.width(half) + 1;
        }

        boolean insert = this.cursorPos < value.length();
        int cursorX = drawX;
        if (!cursorOnScreen) {
            cursorX = relCursorPos > 0 ? textX + this.width : textX;
        } else if (insert) {
            cursorX = drawX - 1;
            drawX--;
        }

        if (!displayed.isEmpty() && cursorOnScreen && relCursorPos < displayed.length()) {
            drawString(this.font, displayed.substring(relCursorPos), drawX, textY, color);
        }

        if (!insert && this.suggestion != null) {
            drawString(this.font, this.suggestion, cursorX - 1, textY, -8355712);
        }

        if (relHighlightPos != relCursorPos) {
            int highlightX = textX + this.font.width(displayed.substring(0, relHighlightPos));
            renderHighlight(
                    Math.min(cursorX, this.x + this.width),
                    textY - 1,
                    Math.min(highlightX - 1, this.x + this.width),
                    textY + 1 + 9
            );
        }

        if (showCursor) {
            if (insert) {
                fill(cursorX, textY - 1, cursorX + 1, textY + 1 + 9, -2039584);
            } else {
                drawString(this.font, "_", cursorX, y, color);
            }
        }
    }

    private void renderHighlight(int x1, int y1, int x2, int y2) {
        if (x1 < x2) {
            int i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            int i = y1;
            y1 = y2;
            y2 = i;
        }

        if (x2 > this.x + this.width) {
            x2 = this.x + this.width;
        }

        if (x1 > this.x + this.width) {
            x1 = this.x + this.width;
        }

        Tesselator tesselator = Tesselator.instance;
        GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glLogicOp(GL11.GL_OR_REVERSE);
        tesselator.begin();
        tesselator.vertex(x1, y2, 0.0);
        tesselator.vertex(x2, y2, 0.0);
        tesselator.vertex(x2, y1, 0.0);
        tesselator.vertex(x1, y1, 0.0);
        tesselator.end();
        GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public void setValue(String input) {
        this.valueSetter.accept(input);
    }

    public void setValueWithUpdate(String input) {
        this.valueSetter.accept(input);
        this.onValueChange(getValue());
    }

    public String getValue() {
        return this.valueGetter.get();
    }

    public void insertText(String input) {
        int start = Math.min(this.cursorPos, this.highlightPos);
        int end = Math.max(this.cursorPos, this.highlightPos);

        String text = filterText(input);

        String newValue = new StringBuilder(this.getValue()).replace(start, end, text).toString();
        int insertionLength = text.length();
        this.setValue(newValue);
        this.setCursorPosition(start + insertionLength);
        this.setHighlightPos(this.cursorPos);
        this.onValueChange(this.getValue());
    }

    public void deleteText(final int dir, final boolean wholeWord) {
        if (wholeWord) {
            this.deleteWords(dir);
        } else {
            this.deleteChars(dir);
        }
    }

    public void deleteWords(final int dir) {
        if (!this.getValue().isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                this.deleteCharsToPos(this.getWordPosition(dir));
            }
        }
    }

    public void deleteChars(final int dir) {
        this.deleteCharsToPos(this.getCursorPos(dir));
    }

    public void deleteCharsToPos(final int pos) {
        String value = this.getValue();
        if (!value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                int start = Math.min(pos, this.cursorPos);
                int end = Math.max(pos, this.cursorPos);
                if (start != end) {
                    String newValue = new StringBuilder(value).delete(start, end).toString();
                    setValue(newValue);
                    this.moveCursorTo(start, false);
                }
            }
        }
    }

    public int getWordPosition(final int dir) {
        return this.getWordPosition(dir, this.getCursorPosition());
    }

    private int getWordPosition(final int dir, final int from) {
        return this.getWordPosition(dir, from, true);
    }

    private int getWordPosition(final int dir, final int from, final boolean stripSpaces) {
        int result = from;
        boolean reverse = dir < 0;
        int abs = Math.abs(dir);
        String value = this.getValue();

        for (int i = 0; i < abs; i++) {
            if (!reverse) {
                int length = value.length();
                result = value.indexOf(32, result);
                if (result == -1) {
                    result = length;
                } else {
                    while (stripSpaces && result < length && value.charAt(result) == ' ') {
                        result++;
                    }
                }
            } else {
                while (stripSpaces && result > 0 && value.charAt(result - 1) == ' ') {
                    result--;
                }

                while (result > 0 && value.charAt(result - 1) != ' ') {
                    result--;
                }
            }
        }

        return result;
    }

    public static String filterText(final String input) {
        StringBuilder builder = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (SharedConstants.acceptableLetters.indexOf(c) >= 0) {
                builder.append(c);
            }
        }

        return builder.toString();
    }

    public int getCursorPosition() {
        return this.cursorPos;
    }

    public void moveCursor(final int dir, final boolean hasShiftDown) {
        this.moveCursorTo(this.getCursorPos(dir), hasShiftDown);
    }

    private int getCursorPos(final int dir) {
        return offsetByCodepoints(this.getValue(), this.cursorPos, dir);
    }

    public static int offsetByCodepoints(final String input, int pos, final int offset) {
        int length = input.length();
        if (offset >= 0) {
            for (int i = 0; pos < length && i < offset; i++) {
                if (Character.isHighSurrogate(input.charAt(pos++)) && pos < length && Character.isLowSurrogate(input.charAt(pos))) {
                    pos++;
                }
            }
        } else {
            for (int ix = offset; pos > 0 && ix < 0; ix++) {
                pos--;
                if (Character.isLowSurrogate(input.charAt(pos)) && pos > 0 && Character.isHighSurrogate(input.charAt(pos - 1))) {
                    pos--;
                }
            }
        }

        return pos;
    }

    public void moveCursorTo(final int dir, final boolean extendSelection) {
        this.setCursorPosition(dir);
        if (!extendSelection) {
            this.setHighlightPos(this.cursorPos);
        }

        this.onValueChange(this.getValue());
    }

    public void setCursorPosition(final int pos) {
        this.cursorPos = Mth2.clamp(pos, 0, this.getValue().length());
        this.scrollTo(this.cursorPos);
    }

    public void moveCursorToStart(final boolean hasShiftDown) {
        this.moveCursorTo(0, hasShiftDown);
    }

    public void moveCursorToEnd(final boolean hasShiftDown) {
        this.moveCursorTo(this.getValue().length(), hasShiftDown);
    }

    public int getInnerWidth() {
        return this.width;
    }

    public void setHighlightPos(final int pos) {
        this.highlightPos = Mth2.clamp(pos, 0, this.getValue().length());
        this.scrollTo(this.highlightPos);
    }

    private void scrollTo(final int pos) {
        if (this.font != null) {
            this.displayPos = Math.min(this.displayPos, this.getValue().length());
            int innerWidth = this.getInnerWidth();
            String displayed = ScreenUtil.trim(this.font, this.getValue().substring(this.displayPos), innerWidth);
            int lastPos = displayed.length() + this.displayPos;
            if (pos == this.displayPos) {
                this.displayPos = this.displayPos - ScreenUtil.trim(this.font, this.getValue(), innerWidth, true).length();
            }

            if (pos > lastPos) {
                this.displayPos += pos - lastPos;
            } else if (pos <= this.displayPos) {
                this.displayPos = this.displayPos - (this.displayPos - pos);
            }

            this.displayPos = Mth2.clamp(this.displayPos, 0, this.getValue().length());
        }
    }

    public void setSuggestion(@Nullable final String suggestion) {
        this.suggestion = suggestion;
    }

    public int getScreenX(final int charIndex) {
        return charIndex > this.valueGetter.get().length() ? this.x : this.x + this.font.width(this.valueGetter.get().substring(0, charIndex)) + this.font.width("> ");
    }
}
