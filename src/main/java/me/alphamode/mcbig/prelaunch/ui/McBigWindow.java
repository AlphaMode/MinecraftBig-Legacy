package me.alphamode.mcbig.prelaunch.ui;

import me.alphamode.mcbig.constants.McBigConstants;
import me.alphamode.mcbig.prelaunch.Features;
import me.alphamode.mcbig.prelaunch.McBigPreLaunch;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

public class McBigWindow extends JFrame implements WindowListener {

    public McBigWindow() {
        super("McBig " + McBigConstants.MC_VERSION);
        this.addWindowListener(this);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public void open() throws Exception {
        BufferedImage logo = ImageIO.read(McBigPreLaunch.class.getClassLoader().getResourceAsStream("mcbig_logo.png"));
        this.setIconImage(logo);

        this.setMinimumSize(new Dimension(300, 300));
        this.setLocationByPlatform(true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        BufferedImage setupArt = ImageIO.read(McBigPreLaunch.class.getClassLoader().getResourceAsStream("installerart.png"));
        JLabel art = new JLabel("McBig Legacy (" + McBigConstants.MC_BIG_VERSION + ") for " + McBigConstants.MC_VERSION, new ImageIcon(setupArt), SwingConstants.CENTER);
        art.setFont(art.getFont().deriveFont(art.getFont().getSize() * 1.1f));
        art.setBackground(Color.BLACK);
        art.setBorder(new EmptyBorder(0, 15, 0, 15));
        art.setPreferredSize(new Dimension(100, 100));
        art.setHorizontalAlignment(SwingConstants.CENTER);
        this.getContentPane().add(art, BorderLayout.NORTH);

        JPanel buttons = new JPanel();

        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));

        for (Features feature : Features.values()) {
            addFeature(buttons, feature);
        }

        JPanel bottom = new JPanel();

        JButton launchButton = new JButton("Launch");
        launchButton.addActionListener(e -> this.dispose());

        bottom.add(launchButton);

        mainPanel.add(buttons);
        mainPanel.add(bottom);

        this.add(mainPanel);

        this.pack();
        this.setVisible(true);
        this.requestFocus();

        synchronized (this) {
            wait();
        }
    }

    private void addFeature(JPanel panel, Features feature) {
        JCheckBox featureBox = new JCheckBox(feature.getText());

        featureBox.addActionListener(e -> System.setProperty(feature.getFlag(), String.valueOf(featureBox.isSelected())));

        panel.add(featureBox);
    }

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {
        synchronized (this) {
            notify();
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
}
