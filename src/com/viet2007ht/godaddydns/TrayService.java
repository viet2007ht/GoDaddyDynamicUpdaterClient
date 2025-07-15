package com.viet2007ht.godaddydns;

import java.awt.*;
import java.awt.event.*;
import java.util.Properties;
import javax.swing.*;

public class TrayService {
    private Properties config;
    private TrayIcon trayIcon;
    private String currentIP = "";
    private long lastUpdateTime = 0;
    private String lastUpdateStatus = "Waiting";

    public TrayService(Properties config) {
        this.config = config;
    }

    public void init() {
        if (!SystemTray.isSupported()) {
            System.err.println("System tray not supported!");
            return;
        }

        Image image = Toolkit.getDefaultToolkit().getImage("resources/icon.png");
        trayIcon = new TrayIcon(image, "GoDaddy DDNS");
        trayIcon.setImageAutoSize(true);

        PopupMenu menu = new PopupMenu();

        MenuItem updateNow = new MenuItem("Update Now");
        updateNow.addActionListener(e -> {
            currentIP = IPChecker.fetchPublicIP();
            GoDaddyUpdater.updateDNS(currentIP, config);
            lastUpdateTime = System.currentTimeMillis();
            lastUpdateStatus = "Manual update triggered";
            updateTooltip();
        });
        menu.add(updateNow);

        MenuItem openConfig = new MenuItem("Open Config");
        openConfig.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                ConfigUI configUI = new ConfigUI(config, updatedConfig -> {
                    ConfigManager.saveConfig(updatedConfig);
                    this.config = updatedConfig;
                });
                configUI.syncStatus(
                    config.getProperty("domain", "N/A"),
                    getRemainingTime(),
                    lastUpdateStatus
                );
                configUI.setVisible(true);
            });
        });
        menu.add(openConfig);

        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        menu.add(exit);

        trayIcon.setPopupMenu(menu);

        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        IPChecker.startMonitoring(config, this::onUpdate);
    }

    private void onUpdate(String ip, String status) {
        currentIP = ip;
        lastUpdateTime = System.currentTimeMillis();
        lastUpdateStatus = status;
        updateTooltip();
    }

    private void updateTooltip() {
        long remainingMs = getRemainingTime();
        String tooltip = "IP: " + currentIP +
                         "\nNext update in: " + (remainingMs / 1000) + "s" +
                         "\nStatus: " + lastUpdateStatus;
        trayIcon.setToolTip(tooltip);
    }

    private long getRemainingTime() {
        long intervalMs = Long.parseLong(config.getProperty("intervalMs", "300000"));
        long nextUpdate = lastUpdateTime + intervalMs;
        return Math.max(0, nextUpdate - System.currentTimeMillis());
    }
}
