package com.school.lms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SettingsDialog extends JDialog {

    private JTextField libNameField, fineRateField, staffUserField;
    private JPasswordField passField, passConfirmField;
    private JPasswordField staffPassField, staffPassConfirmField;
    private String adminUsername;
    private String currentStaffUsername = "staff";

    public SettingsDialog(Frame parent, String adminUsername) {
        super(parent, "System Settings", true);
        this.adminUsername = adminUsername;
        setSize(550, 720);
        setLocationRelativeTo(parent);
        setResizable(false);
        UIUtils.registerEscClose(this);
        fetchStaffUsername();
        buildUI();
    }

    private void fetchStaffUsername() {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT username FROM users WHERE role='staff' LIMIT 1");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                currentStaffUsername = rs.getString("username");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.BG_MAIN);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtils.SIDEBAR_BG);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel title = new JLabel("System Settings");
        title.setFont(UIUtils.FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        // Content
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.CARD_BG);
        form.setBorder(new EmptyBorder(20, 25, 20, 25)); // Compact

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 0, 6, 0); // Compact
        g.weightx = 1.0;
        g.gridx = 0; g.gridy = 0;

        // General
        form.add(UIUtils.createHeader("General"), g);
        libNameField = UIUtils.createField(true);
        libNameField.setText(AppConfig.getLibraryName());
        g.gridy++; form.add(lbl("Library Name:"), g);
        g.gridy++; form.add(libNameField, g);

        fineRateField = UIUtils.createField(true);
        fineRateField.setText(String.valueOf(AppConfig.getFineRatePerDay()));
        g.gridy++; form.add(lbl("Overdue Fine Rate (₱ per day):"), g);
        g.gridy++; form.add(fineRateField, g);

        // Admin Security
        g.gridy++;
        g.insets = new Insets(15, 0, 6, 0); // extra top margin
        form.add(UIUtils.createHeader("Admin Security"), g);
        g.insets = new Insets(6, 0, 6, 0);

        passField = new JPasswordField();
        stylePass(passField);
        g.gridy++; form.add(lbl("Change Admin Password (leave blank to keep current):"), g);
        g.gridy++; form.add(passField, g);

        passConfirmField = new JPasswordField();
        stylePass(passConfirmField);
        g.gridy++; form.add(lbl("Confirm New Password:"), g);
        g.gridy++; form.add(passConfirmField, g);

        // Staff Security
        g.gridy++;
        g.insets = new Insets(15, 0, 6, 0);
        form.add(UIUtils.createHeader("Staff Account"), g);
        g.insets = new Insets(6, 0, 6, 0);

        staffUserField = UIUtils.createField(true);
        staffUserField.setText(currentStaffUsername);
        g.gridy++; form.add(lbl("Staff Username:"), g);
        g.gridy++; form.add(staffUserField, g);

        staffPassField = new JPasswordField();
        stylePass(staffPassField);
        g.gridy++; form.add(lbl("Change Staff Password (leave blank to keep current):"), g);
        g.gridy++; form.add(staffPassField, g);

        staffPassConfirmField = new JPasswordField();
        stylePass(staffPassConfirmField);
        g.gridy++; form.add(lbl("Confirm New Staff Password:"), g);
        g.gridy++; form.add(staffPassConfirmField, g);

        // Buttons
        JButton saveBtn = UIUtils.createButton("Save Changes");
        JButton cancelBtn = UIUtils.createButton("Cancel");
        cancelBtn.setBackground(new Color(230, 230, 230));
        cancelBtn.setForeground(UIUtils.TEXT_PRIMARY);
        
        saveBtn.addActionListener(e -> saveSettings());
        cancelBtn.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(15, 20, 20, 20));
        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(btnPanel, BorderLayout.SOUTH);
        add(root);
    }

    private void stylePass(JPasswordField p) {
        p.setFont(UIUtils.FONT_REGULAR);
        p.setForeground(UIUtils.TEXT_PRIMARY);
        p.setCaretColor(UIUtils.TEXT_PRIMARY);
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                new EmptyBorder(5, 8, 5, 8))); // Compact
    }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIUtils.FONT_BOLD);
        l.setForeground(UIUtils.TEXT_MUTED);
        return l;
    }

    private void saveSettings() {
        String libName = libNameField.getText().trim();
        String fineStr = fineRateField.getText().trim();
        String pass = new String(passField.getPassword());
        String conf = new String(passConfirmField.getPassword());
        
        String sUser = staffUserField.getText().trim();
        String sPass = new String(staffPassField.getPassword());
        String sConf = new String(staffPassConfirmField.getPassword());

        if (libName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Library name cannot be empty."); return;
        }
        if (sUser.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Staff username cannot be empty."); return;
        }

        double fine;
        try {
            fine = Double.parseDouble(fineStr);
            if (fine < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid fine rate."); return;
        }

        // Validate Admin Password
        if (!pass.isEmpty()) {
            if (pass.length() < 6) {
                JOptionPane.showMessageDialog(this, "Admin password must be at least 6 characters."); return;
            }
            if (!pass.equals(conf)) {
                JOptionPane.showMessageDialog(this, "Admin passwords do not match."); return;
            }
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement("UPDATE users SET password_hash=? WHERE username=?")) {
                ps.setString(1, DatabaseConnection.hashPassword(pass));
                ps.setString(2, adminUsername);
                ps.executeUpdate();
            } catch (Exception ex) { ex.printStackTrace(); }
        }

        // Update Staff Username and Password
        try (Connection c = DatabaseConnection.getConnection()) {
            // Check if new username already exists (if changed)
            if (!sUser.equals(currentStaffUsername)) {
                try (PreparedStatement check = c.prepareStatement("SELECT id FROM users WHERE username=? AND role != 'staff'")) {
                    check.setString(1, sUser);
                    ResultSet rs = check.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Username '" + sUser + "' is already taken.");
                        return;
                    }
                }
            }

            // Validate Staff Password
            if (!sPass.isEmpty()) {
                if (sPass.length() < 6) {
                    JOptionPane.showMessageDialog(this, "Staff password must be at least 6 characters."); return;
                }
                if (!sPass.equals(sConf)) {
                    JOptionPane.showMessageDialog(this, "Staff passwords do not match."); return;
                }
                // Update username and password
                try (PreparedStatement ps = c.prepareStatement("UPDATE users SET username=?, password_hash=? WHERE role='staff'")) {
                    ps.setString(1, sUser);
                    ps.setString(2, DatabaseConnection.hashPassword(sPass));
                    ps.executeUpdate();
                }
            } else {
                // Just update username
                try (PreparedStatement ps = c.prepareStatement("UPDATE users SET username=? WHERE role='staff'")) {
                    ps.setString(1, sUser);
                    ps.executeUpdate();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating staff account.");
            return;
        }

        AppConfig.setLibraryName(libName);
        AppConfig.setFineRatePerDay(fine);
        
        JOptionPane.showMessageDialog(this, "Settings saved! Any library name changes will fully apply on the next restart.");
        dispose();
    }
}
