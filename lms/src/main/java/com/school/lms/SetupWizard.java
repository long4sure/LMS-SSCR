package com.school.lms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SetupWizard extends JDialog {

    private JTextField libNameField, fineRateField;
    private JPasswordField passField, passConfirmField;
    private boolean isCompleted = false;

    public SetupWizard(Frame parent) {
        super(parent, "First-Time Setup", true);
        setSize(500, 480);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // Must complete
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.BG_MAIN);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtils.SIDEBAR_BG);
        header.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JLabel title = new JLabel("Welcome to LMS Setup");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        
        JLabel sub = new JLabel("Let's configure your system for the first time.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(new Color(255, 200, 200));

        JPanel hText = new JPanel(new GridLayout(2, 1));
        hText.setOpaque(false);
        hText.add(title);
        hText.add(sub);
        header.add(hText, BorderLayout.WEST);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.CARD_BG);
        form.setBorder(new EmptyBorder(20, 25, 20, 25)); // Compact

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 0, 6, 0); // Compact
        g.weightx = 1.0;
        g.gridx = 0; g.gridy = 0;

        libNameField     = UIUtils.createField(true);
        libNameField.setText(AppConfig.getLibraryName());
        fineRateField    = UIUtils.createField(true);
        fineRateField.setText("5.0");
        passField        = new JPasswordField(); stylePass(passField);
        passConfirmField = new JPasswordField(); stylePass(passConfirmField);

        form.add(lbl("Library Name:"), g); g.gridy++;
        form.add(libNameField, g);         g.gridy++;
        
        g.insets = new Insets(15, 0, 6, 0);
        form.add(lbl("Fine Rate per overdue day (₱):"), g); g.gridy++;
        g.insets = new Insets(6, 0, 6, 0);
        form.add(fineRateField, g);        g.gridy++;

        g.insets = new Insets(15, 0, 6, 0);
        form.add(lbl("Set 'admin' Password (minimum 6 chars):"), g); g.gridy++;
        g.insets = new Insets(6, 0, 6, 0);
        form.add(passField, g);            g.gridy++;

        form.add(lbl("Confirm 'admin' Password:"), g); g.gridy++;
        form.add(passConfirmField, g);

        // Footer
        JButton finishBtn = UIUtils.createButton("Finish Setup");
        finishBtn.setPreferredSize(new Dimension(150, 45));
        finishBtn.addActionListener(e -> submit());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(15, 25, 20, 25));
        footer.add(finishBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        add(root);
    }

    private void stylePass(JPasswordField p) {
        p.setFont(UIUtils.FONT_REGULAR);
        p.setForeground(UIUtils.TEXT_PRIMARY);
        p.setCaretColor(Color.WHITE);
        p.setBackground(UIUtils.SIDEBAR_BG);
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

    private void submit() {
        String name = libNameField.getText().trim();
        String fineStr = fineRateField.getText().trim();
        String pass = new String(passField.getPassword());
        String conf = new String(passConfirmField.getPassword());

        if (name.isEmpty() || pass.isEmpty() || fineStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields."); return;
        }

        double fine;
        try {
            fine = Double.parseDouble(fineStr);
            if (fine < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Fine rate must be a positive number."); return;
        }

        if (pass.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long."); return;
        }
        if (!pass.equals(conf)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match."); return;
        }

        try {
            // Update the admin user password via DB Connection
            java.sql.Connection conn = DatabaseConnection.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement("UPDATE users SET password_hash=? WHERE username='admin'");
            ps.setString(1, DatabaseConnection.hashPassword(pass));
            ps.executeUpdate();
            
            AppConfig.setLibraryName(name);
            AppConfig.setFineRatePerDay(fine);
            AppConfig.markSetupComplete();

            isCompleted = true;
            JOptionPane.showMessageDialog(this, "Setup complete! Please log in.");
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during setup: " + ex.getMessage());
        }
    }

    public boolean isCompleted() { return isCompleted; }
}
