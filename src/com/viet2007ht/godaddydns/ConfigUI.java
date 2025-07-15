package com.viet2007ht.godaddydns;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Properties;

public class ConfigUI extends JFrame {
    private final JTextField apiKeyField = new JTextField(30);
    private final JTextField apiSecretField = new JTextField(30);
    private final JTextField domainField = new JTextField(30);
    private final JTextField recordField = new JTextField(30);
    private final JTextField intervalField = new JTextField(10);

    private final JLabel currentDomainLabel = new JLabel("Updating: ");
    private final JLabel remainingTimeLabel = new JLabel("Next update in: ");
    private final JLabel updateStatusLabel = new JLabel("Status: ");

    private final JButton saveButton = new JButton("Save & Start");

    public interface ConfigCallback {
        void onSave(Properties config);
    }

    public ConfigUI(Properties existingConfig, ConfigCallback callback) {
        setTitle("GoDaddy DDNS Configuration");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addLabelAndField("API Key:", apiKeyField, existingConfig.getProperty("apiKey", ""), gbc, 0);
        addLabelAndField("API Secret:", apiSecretField, existingConfig.getProperty("apiSecret", ""), gbc, 1);
        addLabelAndField("Domain:", domainField, existingConfig.getProperty("domain", ""), gbc, 2);
        addLabelAndField("Record Name:", recordField, existingConfig.getProperty("record", ""), gbc, 3);
        addLabelAndField("Interval (ms):", intervalField, existingConfig.getProperty("intervalMs", "300000"), gbc, 4);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        saveButton.addActionListener((ActionEvent e) -> {
            Properties config = new Properties();
            config.setProperty("apiKey", apiKeyField.getText().trim());
            config.setProperty("apiSecret", apiSecretField.getText().trim());
            config.setProperty("domain", domainField.getText().trim());
            config.setProperty("record", recordField.getText().trim());
            config.setProperty("intervalMs", intervalField.getText().trim());
            callback.onSave(config);
        });
        add(saveButton, gbc);

        // Status labels
        gbc.gridy = 6;
        add(currentDomainLabel, gbc);
        gbc.gridy = 7;
        add(remainingTimeLabel, gbc);
        gbc.gridy = 8;
        add(updateStatusLabel, gbc);
    }

    private void addLabelAndField(String labelText, JTextField field, String value, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        field.setText(value);
        add(field, gbc);
    }

    public void syncStatus(String domain, long remainingMs, String status) {
        currentDomainLabel.setText("Updating: " + domain);
        remainingTimeLabel.setText("Next update in: " + (remainingMs / 1000) + "s");
        updateStatusLabel.setText("Status: " + status);
    }
}
