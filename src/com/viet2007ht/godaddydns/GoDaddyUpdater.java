package com.viet2007ht.godaddydns;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class GoDaddyUpdater {
    public static void updateDNS(String ip, Properties config) {
        String apiKey = config.getProperty("apiKey");
        String apiSecret = config.getProperty("apiSecret");
        String domain = config.getProperty("domain");
        String record = config.getProperty("record");

        try {
            URL url = new URL("https://api.godaddy.com/v1/domains/" + domain + "/records/A/" + record);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", "sso-key " + apiKey + ":" + apiSecret);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = "[{\"data\":\"" + ip + "\"}]";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            int responseCode = conn.getResponseCode();
            System.out.println("DNS update response: " + responseCode);
        } catch (Exception e) {
            System.err.println("DNS update failed: " + e.getMessage());
        }
    }
}


