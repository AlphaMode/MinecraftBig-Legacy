package me.alphamode.mcbig.util;

import net.minecraft.client.gui.Font;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class ScreenUtil {
    public static String getClipboard() {
        try {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) transferable.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception ignored) {
        }

        return "";
    }

    public static void setClipboard(String text) {
        if (!StringUtils.isEmpty(text)) {
            try {
                StringSelection stringSelection = new StringSelection(text);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            } catch (Exception ignored) {
            }
        }
    }

    public static String trim(final Font font, String text, int width) {
        return trim(font, text, width, false);
    }

    public static String trim(Font font, String text, int width, boolean inverse) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        int j = inverse ? text.length() - 1 : 0;
        int k = inverse ? -1 : 1;
        boolean bl = false;
        boolean bl2 = false;

        for (int l = j; l >= 0 && l < text.length() && i < width; l += k) {
            char c = text.charAt(l);
            int m = font.width(c);
            if (bl) {
                bl = false;
                if (c == 'l' || c == 'L') {
                    bl2 = true;
                } else if (c == 'r' || c == 'R') {
                    bl2 = false;
                }
            } else if (m < 0) {
                bl = true;
            } else {
                i += m;
                if (bl2) {
                    i++;
                }
            }

            if (i > width) {
                break;
            }

            if (inverse) {
                stringBuilder.insert(0, c);
            } else {
                stringBuilder.append(c);
            }
        }

        return stringBuilder.toString();
    }
}
