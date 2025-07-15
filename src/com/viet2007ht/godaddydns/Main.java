package com.viet2007ht.godaddydns;

import javax.swing.*;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        // Use system look-and-feel for native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Load saved config
        Properties config = ConfigManager.loadConfig();

        // Launch config UI
        SwingUtilities.invokeLater(() -> {
            ConfigUI configUI = new ConfigUI(config, updatedConfig -> {
                ConfigManager.saveConfig(updatedConfig);
                TrayService tray = new TrayService(updatedConfig);
                tray.init();
                //configUI.dispose();
            });
            configUI.setVisible(true);
        });
    }
}
