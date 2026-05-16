package com.school.lms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private JButton loginBtn;
    private MainFrame mainFrame;

    public Login(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_MAIN);

        // Split Layout: Left side branding, Right side login
        JPanel splitPane = new JPanel(new GridLayout(1, 2));
        splitPane.setBackground(UIUtils.BG_MAIN);

        // Left Branding Panel
        JPanel brandPanel = new JPanel(new GridBagLayout());
        brandPanel.setBackground(UIUtils.SIDEBAR_BG);
        
        JPanel brandContent = new JPanel();
        brandContent.setLayout(new BoxLayout(brandContent, BoxLayout.Y_AXIS));
        brandContent.setOpaque(false);

        JLabel l1 = new JLabel("Library Management System", SwingConstants.CENTER);
        l1.setFont(new Font("Segoe UI", Font.BOLD, 22));
        l1.setForeground(UIUtils.ACCENT_COLOR);
        l1.setAlignmentX(Component.CENTER_ALIGNMENT);

        String libName = AppConfig.getLibraryName();
        JLabel l2 = new JLabel("<html><div style='text-align: center; width: 300px;'>" + libName + "</div></html>", SwingConstants.CENTER);
        l2.setFont(new Font("Segoe UI", Font.BOLD, 26));
        l2.setForeground(Color.WHITE);
        l2.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel devLabel = new JLabel("Developed by Jerome Misa • 2026", SwingConstants.CENTER);
        devLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        devLabel.setForeground(UIUtils.TEXT_MUTED);
        devLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        brandContent.add(l1);
        brandContent.add(Box.createVerticalStrut(15));
        brandContent.add(l2);
        brandContent.add(Box.createVerticalStrut(40));
        brandContent.add(devLabel);

        brandPanel.add(brandContent);
        splitPane.add(brandPanel);

        // Right Login Panel
        JPanel loginContainer = new JPanel(new GridBagLayout());
        loginContainer.setBackground(UIUtils.BG_MAIN);
        loginContainer.add(createLoginForm());
        
        splitPane.add(loginContainer);

        add(splitPane, BorderLayout.CENTER);
    }

    private JLabel brandLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 36));
        l.setForeground(Color.WHITE);
        return l;
    }

    private JPanel createLoginForm() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UIUtils.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                new EmptyBorder(30, 40, 30, 40))); // Compact
        card.setPreferredSize(new Dimension(400, 430));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0; gbc.gridwidth = 2; gbc.gridy = 0;

        JLabel title = new JLabel("Welcome Back", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(UIUtils.TEXT_PRIMARY);
        card.add(title, gbc);

        JLabel subTitle = new JLabel("Please enter your credentials to login", SwingConstants.CENTER);
        subTitle.setFont(UIUtils.FONT_REGULAR);
        subTitle.setForeground(UIUtils.TEXT_MUTED);
        gbc.gridy = 1;
        card.add(subTitle, gbc);
        gbc.gridwidth = 1;

        // Fields
        usernameField = UIUtils.createField(true);
        passwordField = new JPasswordField();
        passwordField.setFont(UIUtils.FONT_REGULAR);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                new EmptyBorder(8, 12, 8, 12)));

        gbc.gridy = 2; addField(card, gbc, "Username:", usernameField);
        gbc.gridy = 3; addField(card, gbc, "Password:", passwordField);

        // Show Password
        JCheckBox showPass = new JCheckBox("Show Password");
        showPass.setOpaque(false);
        showPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPass.setForeground(UIUtils.TEXT_MUTED);
        showPass.addActionListener(e -> passwordField.setEchoChar(showPass.isSelected() ? (char) 0 : '*'));
        gbc.gridx = 1; gbc.gridy = 4;
        card.add(showPass, gbc);

        // Buttons
        loginBtn = UIUtils.createButton("Login to System");
        loginBtn.setPreferredSize(new Dimension(200, 45));

        JButton guestBtn = UIUtils.createSecondaryButton("Guest Book Search");
        guestBtn.setPreferredSize(new Dimension(200, 45));
        guestBtn.addActionListener(e -> mainFrame.showGuestSearch());

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        btnPanel.setOpaque(false);
        btnPanel.add(loginBtn);
        btnPanel.add(guestBtn);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        card.add(btnPanel, gbc);
        
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 10, 10, 10);
        card.add(messageLabel, gbc);

        gbc.gridy = 7;
        JLabel userGuideLabel = new JLabel("<html><u>User Guide</u></html>", SwingConstants.CENTER);
        userGuideLabel.setForeground(UIUtils.ACCENT_COLOR);
        userGuideLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userGuideLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                javax.swing.JOptionPane.showMessageDialog(Login.this, "Welcome to the Library Management System!\n\n1. Use 'admin' to log in (password: the password you created during the first-time setup).\n2. Use your staff account (username and password assigned by the admin) to log in for daily operations.\n3. Guest Search allows anyone to browse the catalog.\n4. Manage Books: Add, edit, or delete books.\n5. Issue/Return: Track borrowing and returns.\n6. Reports: View transactions and overdue books.\n\nEnjoy the system!", "User Guide", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        });
        card.add(userGuideLabel, gbc);

        loginBtn.addActionListener(e -> authenticate());
        passwordField.addActionListener(e -> authenticate());
        usernameField.addActionListener(e -> passwordField.requestFocus());

        return card;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, JComponent field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIUtils.FONT_BOLD);
        lbl.setForeground(UIUtils.TEXT_PRIMARY);
        gbc.gridx = 0; gbc.weightx = 0.3;
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void authenticate() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        loginBtn.setEnabled(false);
        messageLabel.setText("Authenticating...");
        messageLabel.setForeground(UIUtils.TEXT_MUTED);

        SwingUtilities.invokeLater(() -> {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT username, role FROM users WHERE username=? AND password_hash=?")) {
                stmt.setString(1, user);
                stmt.setString(2, DatabaseConnection.hashPassword(pass));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    usernameField.setText("");
                    passwordField.setText("");
                    messageLabel.setText(" ");
                    mainFrame.loginSuccess(user, rs.getString("role"));
                } else {
                    messageLabel.setText("Invalid username or password.");
                    messageLabel.setForeground(Color.RED);
                }
            } catch (Exception ex) {
                messageLabel.setText("Database error: " + ex.getMessage());
                messageLabel.setForeground(Color.RED);
            } finally {
                loginBtn.setEnabled(true);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseConnection.initializeSchema();
            if (!AppConfig.isSetupComplete()) {
                JFrame dummy = new JFrame();
                SetupWizard wizard = new SetupWizard(dummy);
                wizard.setVisible(true);
                if (!wizard.isCompleted()) System.exit(0);
            }
            new MainFrame().setVisible(true);
        });
    }
}


