package me.alphamode.mcbig.client;

import me.alphamode.mcbig.constants.McBigConstants;
import net.minecraft.client.CrashInfoPanel;
import net.minecraft.client.CrashReport;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class McBigCrashInfoPanel extends Panel {
    public McBigCrashInfoPanel(CrashReport crash) {
        this.setBackground(new Color(3028036));
        this.setLayout(new BorderLayout());
        StringWriter w = new StringWriter();
        crash.e.printStackTrace(new PrintWriter(w));
        String stacktrace = w.toString();
        String glInfo = "";
        String mcInfo = "";

        try {
            mcInfo = mcInfo + "Generated " + new SimpleDateFormat().format(new Date()) + "\n";
            mcInfo = mcInfo + "\n";
            mcInfo = mcInfo + "Minecraft: Minecraft " + McBigConstants.VERSION_STRING + "\n";
            mcInfo = mcInfo + "McBig Legacy: " + McBigConstants.MC_BIG_VERSION + "\n";
            mcInfo = mcInfo + "OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version") + "\n";
            mcInfo = mcInfo + "Java: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor") + "\n";
            mcInfo = mcInfo + "VM: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor") + "\n";
            mcInfo = mcInfo + "LWJGL: " + Sys.getVersion() + "\n";
            glInfo = GL11.glGetString(7936);
            mcInfo = mcInfo + "OpenGL: " + GL11.glGetString(7937) + " version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936) + "\n";
        } catch (Throwable var8) {
            mcInfo = mcInfo + "[failed to get system properties (" + var8 + ")]\n";
        }

        mcInfo = mcInfo + "\n";
        mcInfo = mcInfo + stacktrace;
        String logInfo = "";
        logInfo = logInfo + "\n";
        logInfo = logInfo + "\n";
        if (stacktrace.contains("Pixel format not accelerated")) {
            logInfo = logInfo + "      Bad video card drivers!      \n";
            logInfo = logInfo + "      -----------------------      \n";
            logInfo = logInfo + "\n";
            logInfo = logInfo + "Minecraft was unable to start because it failed to find an accelerated OpenGL mode.\n";
            logInfo = logInfo + "This can usually be fixed by updating the video card drivers.\n";
            if (glInfo.toLowerCase().contains("nvidia")) {
                logInfo = logInfo + "\n";
                logInfo = logInfo + "You might be able to find drivers for your video card here:\n";
                logInfo = logInfo + "  http://www.nvidia.com/\n";
            } else if (glInfo.toLowerCase().contains("ati")) {
                logInfo = logInfo + "\n";
                logInfo = logInfo + "You might be able to find drivers for your video card here:\n";
                logInfo = logInfo + "  http://www.amd.com/\n";
            }
        } else {
            logInfo = logInfo + "      Minecraft has crashed!      \n";
            logInfo = logInfo + "      ----------------------      \n";
            logInfo = logInfo + "\n";
            logInfo = logInfo + "Minecraft has stopped running because it encountered a problem.\n";
            logInfo = logInfo + "\n";
            logInfo = logInfo + "If you wish to report this, please make a github issue at https://github.com/AlphaMode/MinecraftBig-Legacy/issues\n";
            logInfo = logInfo + "Please include a description of what you did when the error occured.\n";
        }

        logInfo = logInfo + "\n";
        logInfo = logInfo + "\n";
        logInfo = logInfo + "\n";
        logInfo = logInfo + "--- BEGIN ERROR REPORT " + Integer.toHexString(logInfo.hashCode()) + " --------\n";
        logInfo = logInfo + mcInfo;
        logInfo = logInfo + "--- END ERROR REPORT " + Integer.toHexString(logInfo.hashCode()) + " ----------\n";
        logInfo = logInfo + "\n";
        logInfo = logInfo + "\n";
        TextArea var7 = new TextArea(logInfo, 0, 0, 1);
        var7.setFont(new Font("Monospaced", 0, 12));
        this.add(new CrashLogoCanvas(), "North");
        this.add(new CrashCanvas(80), "East");
        this.add(new CrashCanvas(80), "West");
        this.add(new CrashCanvas(100), "South");
        this.add(var7, "Center");
    }

    static class CrashCanvas extends Canvas {
        public CrashCanvas(int s) {
            this.setPreferredSize(new Dimension(s, s));
            this.setMinimumSize(new Dimension(s, s));
        }
    }

    static class CrashLogoCanvas extends Canvas {
        private BufferedImage crashImage;

        public CrashLogoCanvas() {
            try {
                this.crashImage = ImageIO.read(CrashInfoPanel.class.getResource("/gui/logo.png"));
            } catch (IOException var2) {
            }

            byte var1 = 100;
            this.setPreferredSize(new Dimension(var1, var1));
            this.setMinimumSize(new Dimension(var1, var1));
        }

        public void paint(Graphics graphics) {
            super.paint(graphics);
            graphics.drawImage(this.crashImage, this.getWidth() / 2 - this.crashImage.getWidth() / 2, 32, null);
        }
    }
}
