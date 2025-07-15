package com.viet2007ht.godaddydns;

import java.io.*;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.properties";

    public static Properties loadConfig() {
        Properties props = new Properties();
        File file = new File(CONFIG_FILE);

        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("Failed to load config: " + e.getMessage());
            }
        }

        return props;
    }

    public static void saveConfig(Properties props) {
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "GoDaddy DDNS Configuration");
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }
}
