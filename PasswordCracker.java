import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class PasswordCracker extends JFrame {

    private JComboBox<String> modeSelector;
    private JTextField passwordField, lengthField, genCountField;
    private JCheckBox includeSpecialChars;
    private JTextArea logArea;
    private JButton actionButton, resetButton, exportAllButton;
    private JProgressBar progressBar;
    private JLabel statusLabel, speedLabel, timeLabel;

    private List<String> generatedPasswords = new ArrayList<>();
    private SwingWorker<Void, String> worker;
    private long startTime, attempts, maxAttempts;

    public PasswordCracker() {
        setTitle("🔐 Password Cracker & Generator");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("🔧 Mode & Inputs"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Select Mode:"), gbc);
        modeSelector = new JComboBox<>(new String[]{"Crack My Password", "Generate Random Passwords"});
        modeSelector.addActionListener(e -> updateMode());
        gbc.gridx = 1; gbc.gridwidth = 2;
        topPanel.add(modeSelector, gbc); gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Password to Crack:"), gbc);
        passwordField = new JTextField(15);
        gbc.gridx = 1; gbc.gridwidth = 2;
        topPanel.add(passwordField, gbc); gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2;
        topPanel.add(new JLabel("Max Password Length:"), gbc);
        lengthField = new JTextField("5");
        gbc.gridx = 1; gbc.gridwidth = 2;
        topPanel.add(lengthField, gbc); gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 3;
        topPanel.add(new JLabel("Passwords to Generate:"), gbc);
        genCountField = new JTextField("10");
        gbc.gridx = 1; gbc.gridwidth = 2;
        topPanel.add(genCountField, gbc); gbc.gridwidth = 1;

        includeSpecialChars = new JCheckBox("Include special characters");
        includeSpecialChars.setSelected(true);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        topPanel.add(includeSpecialChars, gbc); gbc.gridwidth = 1;

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("📜 Output Log"));

        exportAllButton = new JButton("💾 Export All to USB");
        exportAllButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        exportAllButton.setBackground(new Color(210, 255, 210));
        exportAllButton.setForeground(Color.BLACK);
        exportAllButton.addActionListener(this::exportAllPasswords);

        JPanel exportPanel = new JPanel();
        exportPanel.setLayout(new BorderLayout());
        exportPanel.setPreferredSize(new Dimension(250, 0));
        exportPanel.setBorder(BorderFactory.createTitledBorder("Export Panel"));

        exportPanel.add(exportAllButton, BorderLayout.NORTH);
        JLabel infoLabel = new JLabel("📦 Export All Generated Passwords");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        exportPanel.add(infoLabel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new GridLayout(4, 1));
        statusLabel = new JLabel("🟢 Status: Ready");
        speedLabel = new JLabel("⚡ Speed: 0 attempts/sec");
        timeLabel = new JLabel("⏳ Estimated Time: --");
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        statusPanel.add(statusLabel);
        statusPanel.add(speedLabel);
        statusPanel.add(timeLabel);
        statusPanel.add(progressBar);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        actionButton = new JButton();
        actionButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        actionButton.setPreferredSize(new Dimension(180, 35));
        actionButton.setMaximumSize(new Dimension(180, 35));
        actionButton.setBackground(new Color(200, 230, 255));
        actionButton.setForeground(Color.BLACK);
        actionButton.addActionListener(this::handleAction);

        resetButton = new JButton("🔁 Reset");
        resetButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        resetButton.setPreferredSize(new Dimension(180, 35));
        resetButton.setMaximumSize(new Dimension(180, 35));
        resetButton.setBackground(new Color(245, 200, 200));
        resetButton.setForeground(Color.BLACK);
        resetButton.addActionListener(this::resetFields);

        buttonPanel.add(actionButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(resetButton);

        add(topPanel, BorderLayout.NORTH);
        add(logScrollPane, BorderLayout.CENTER);
        add(exportPanel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.EAST);

        updateMode();
        setVisible(true);
    }

    private void updateMode() {
        boolean isCrack = modeSelector.getSelectedItem().toString().equals("Crack My Password");

        passwordField.setEnabled(isCrack);
        lengthField.setEnabled(isCrack);
        genCountField.setEnabled(!isCrack);
        includeSpecialChars.setEnabled(!isCrack);
        exportAllButton.setVisible(!isCrack);

        progressBar.setVisible(isCrack);
        speedLabel.setVisible(isCrack);
        timeLabel.setVisible(isCrack);

        actionButton.setText(isCrack ? "🔓 Crack Password" : "Generate Passwords");
        revalidate();
        repaint();
    }

    private void handleAction(ActionEvent e) {
        if (modeSelector.getSelectedItem().equals("Crack My Password")) {
            crackPasswordMode(e);
        } else {
            generatePasswordsMode(e);
        }
    }

    private void crackPasswordMode(ActionEvent e) {
        String targetPassword = passwordField.getText().trim();
        if (targetPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a password to crack.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int maxLength;
        try {
            maxLength = Integer.parseInt(lengthField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid max length.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String targetHash = hashSHA256(targetPassword);
        logArea.setText("🔐 Target SHA-256 Hash: " + targetHash + "\n");
        statusLabel.setText("🔍 Cracking started...");
        attempts = 0;
        startTime = System.currentTimeMillis();
        maxAttempts = (long) Math.pow(62, maxLength);

        actionButton.setEnabled(false);
        resetButton.setEnabled(false);

        worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                crack("", targetHash, maxLength);
                return null;
            }

            @Override
            protected void done() {
                actionButton.setEnabled(true);
                resetButton.setEnabled(true);
            }
        };
        worker.execute();
    }

    private void crack(String prefix, String targetHash, int maxLength) {
        if (worker.isCancelled()) return;
        if (prefix.length() > maxLength) return;

        String currentHash = hashSHA256(prefix);
        attempts++;

        if (attempts % 1000 == 0) {
            long elapsed = System.currentTimeMillis() - startTime;
            double seconds = elapsed / 1000.0;
            double speed = attempts / (seconds == 0 ? 1 : seconds);
            int progress = (int) Math.min((attempts * 100 / maxAttempts), 100);
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(progress);
                speedLabel.setText("⚡ Speed: " + String.format("%.2f", speed) + " attempts/sec");
                timeLabel.setText("⏳ Estimated Time: ~" + (int)((maxAttempts - attempts) / (speed == 0 ? 1 : speed)) + "s");
            });
        }

        if (currentHash.equals(targetHash)) {
            SwingUtilities.invokeLater(() -> {
                logArea.append("✅ Password cracked: " + prefix + "\n");
                statusLabel.setText("🎉 Password successfully cracked!");
                progressBar.setValue(100);
            });
            worker.cancel(true);
            return;
        }

        String charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < charset.length(); i++) {
            crack(prefix + charset.charAt(i), targetHash, maxLength);
            if (worker.isCancelled()) return;
        }
    }

    private void generatePasswordsMode(ActionEvent e) {
        logArea.setText("");
        generatedPasswords.clear();

        int count;
        try {
            count = Integer.parseInt(genCountField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid count.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        if (includeSpecialChars.isSelected()) {
            chars += "!@#$%^&*()-_=+[]{}|;:,.<>?";
        }

        SecureRandom rand = new SecureRandom();
        statusLabel.setText("🔄 Generating passwords...");

        for (int i = 0; i < count; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 10; j++) {
                sb.append(chars.charAt(rand.nextInt(chars.length())));
            }
            String password = sb.toString();
            generatedPasswords.add(password);
            logArea.append("🔐 " + password + "\n");
        }

        statusLabel.setText("✅ Generated " + count + " passwords.");
    }

    private void exportAllPasswords(ActionEvent e) {
        if (generatedPasswords.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No passwords to export.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select USB Drive or Folder to Export");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File dir = chooser.getSelectedFile();
            File outFile = new File(dir, "all_passwords.txt");

            try (FileWriter fw = new FileWriter(outFile)) {
                for (String pwd : generatedPasswords) {
                    fw.write(pwd + "\n");
                }
                logArea.append("✅ Exported all passwords to: " + outFile.getAbsolutePath() + "\n");
            } catch (IOException ex) {
                logArea.append("❌ Failed to export: " + ex.getMessage() + "\n");
            }
        } else {
            logArea.append("⚠️ Export cancelled.\n");
        }
    }

    private void resetFields(ActionEvent e) {
        if (worker != null && !worker.isDone()) worker.cancel(true);

        passwordField.setText("");
        lengthField.setText("5");
        genCountField.setText("10");
        logArea.setText("");
        generatedPasswords.clear();
        progressBar.setValue(0);
        statusLabel.setText("🟢 Status: Ready");
        speedLabel.setText("⚡ Speed: 0 attempts/sec");
        timeLabel.setText("⏳ Estimated Time: --");
        actionButton.setEnabled(true);
        resetButton.setEnabled(true);
    }

    private String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PasswordCracker::new);
    }
}
