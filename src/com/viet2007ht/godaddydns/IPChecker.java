package com.viet2007ht.godaddydns;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class IPChecker {
    private static String lastIP = "";

    public static void startMonitoring(Properties config, BiConsumer<String, String> onUpdateCallback) {
        long intervalMs = Long.parseLong(config.getProperty("intervalMs", "300000"));
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            String currentIP = fetchPublicIP();
            if (!currentIP.equals(lastIP)) {
                GoDaddyUpdater.updateDNS(currentIP, config);
                lastIP = currentIP;
                onUpdateCallback.accept(currentIP, "Updated");
            } else {
                onUpdateCallback.accept(currentIP, "Skipped (IP unchanged)");
            }
        }, 0, intervalMs, TimeUnit.MILLISECONDS);
    }

    public static String fetchPublicIP() {
        try (Scanner scanner = new Scanner(new URL("https://api.ipify.org").openStream())) {
            return scanner.nextLine();
        } catch (IOException e) {
            System.err.println("Failed to fetch public IP: " + e.getMessage());
            return "";
        }
    }
}
