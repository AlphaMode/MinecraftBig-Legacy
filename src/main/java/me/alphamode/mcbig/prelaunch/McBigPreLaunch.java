package me.alphamode.mcbig.prelaunch;

import me.alphamode.mcbig.prelaunch.ui.McBigWindow;
import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.fabricmc.loader.impl.util.SystemProperties;

import javax.swing.*;

public class McBigPreLaunch implements LanguageAdapter {

    @Override
    public <T> T create(ModContainer mod, String value, Class<T> type) {
        throw new UnsupportedOperationException();
    }

    static {
        // Todo also check for headless
        if (!Boolean.getBoolean(SystemProperties.UNIT_TEST)) {
            try {
                // Fix AA
                System.setProperty("awt.useSystemAAFontSettings", "lcd");
                System.setProperty("swing.aatext", "true");

                // Force GTK if available
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                for (var laf : UIManager.getInstalledLookAndFeels()) {
                    if (!"GTK+".equals(laf.getName())) continue;
                    UIManager.setLookAndFeel(laf.getClassName());
                }

                McBigWindow window = new McBigWindow();
                window.open();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
