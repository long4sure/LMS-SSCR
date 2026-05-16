package com.school.lms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel rootPanel;
    
    // App container
    private JPanel appContainer;
    private JPanel sidebar;
    private JPanel contentArea;
    private CardLayout contentLayout;
    
    private String currentUsername;
    private String currentRole;

    public MainFrame() {
        setTitle("LMS");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 700));

        cardLayout = new CardLayout();
        rootPanel = new JPanel(cardLayout);
        add(rootPanel);

        // 1. Add Login Panel
        rootPanel.add(new Login(this), "LOGIN");

        // 2. Setup App Container
        setupAppContainer();
        rootPanel.add(appContainer, "APP");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(MainFrame.this,
                        "Are you sure you want to exit?", "Exit",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });
        
        cardLayout.show(rootPanel, "LOGIN");
    }

    private void setupAppContainer() {
        appContainer = new JPanel(new BorderLayout());
        appContainer.setBackground(UIUtils.BG_MAIN);

        contentLayout = new CardLayout();
        contentArea = new JPanel(contentLayout);
        contentArea.setBackground(UIUtils.BG_MAIN);
        contentArea.setBorder(new EmptyBorder(15, 20, 15, 20)); // Compact

        // Pre-load panels
        contentArea.add(new Dashboard(this), "Dashboard");
        contentArea.add(new ManageBooks(), "Manage Books");
        contentArea.add(new IssueBook(), "Issue Book");
        contentArea.add(new ReturnBook(), "Return Book");
        contentArea.add(new SearchBooks(), "Search Books");
        contentArea.add(new Reports(), "Reports");

        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIUtils.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(200, 0)); // Compact
        sidebar.setBorder(new EmptyBorder(20, 0, 10, 0));

        appContainer.add(sidebar, BorderLayout.WEST);
        appContainer.add(contentArea, BorderLayout.CENTER);
    }

    public void loginSuccess(String username, String role) {
        this.currentUsername = username;
        this.currentRole = role;
        
        buildSidebar();
        
        for (Component c : contentArea.getComponents()) {
            if (c instanceof Dashboard) {
                ((Dashboard) c).onShow(username, role);
            }
        }
        
        contentLayout.show(contentArea, "Dashboard");
        cardLayout.show(rootPanel, "APP");
    }

    public void logout() {
        if (JOptionPane.showConfirmDialog(this, "Logout now?", "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            currentUsername = null;
            currentRole = null;
            cardLayout.show(rootPanel, "LOGIN");
        }
    }

    public void showGuestSearch() {
        // Special case for guest search directly from login
        currentUsername = "Guest";
        currentRole = "guest";
        buildSidebar();
        navigateTo("Search Books");
        cardLayout.show(rootPanel, "APP");
    }

    private void buildSidebar() {
        sidebar.removeAll();

        // Brand Text Panel (Left aligned to match buttons)
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandPanel.setBorder(new EmptyBorder(0, 15, 0, 15)); // Match button 15px left padding
        
        JLabel lmsBrand = new JLabel("LMS");
        lmsBrand.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lmsBrand.setForeground(Color.WHITE);
        lmsBrand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandPanel.add(lmsBrand);
        
        String libName = AppConfig.getLibraryName();
        JLabel subBrand = new JLabel("<html><div style='text-align: left;'>" + libName + "</div></html>");
        subBrand.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subBrand.setForeground(UIUtils.TEXT_MUTED);
        subBrand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandPanel.add(subBrand);
        
        sidebar.add(brandPanel);
        
        // Add space under brand
        sidebar.add(Box.createVerticalStrut(25)); // Compact

        // Links
        if (!"guest".equals(currentRole)) {
            addNav("Dashboard");
        }
        
        if ("admin".equalsIgnoreCase(currentRole)) {
            addNav("Manage Books");
        }
        
        if (!"guest".equals(currentRole)) {
            addNav("Issue Book");
            addNav("Return Book");
        }
        
        addNav("Search Books");
        
        if (!"guest".equals(currentRole)) {
            addNav("Reports");
        }

        sidebar.add(Box.createVerticalGlue());

        // Bottom Links
        if ("admin".equalsIgnoreCase(currentRole)) {
            JButton settingsBtn = UIUtils.createNavButton("Settings");
            settingsBtn.setMaximumSize(new Dimension(200, 38));
            settingsBtn.addActionListener(e -> new SettingsDialog(this, currentUsername).setVisible(true));
            sidebar.add(settingsBtn);
        }

        JButton aboutBtn = UIUtils.createNavButton("About");
        aboutBtn.setMaximumSize(new Dimension(200, 38));
        aboutBtn.addActionListener(e -> new AboutDialog(this).setVisible(true));
        sidebar.add(aboutBtn);

        JButton logoutBtn = UIUtils.createNavButton("guest".equals(currentRole) ? "Back to Login" : "Logout");
        logoutBtn.setMaximumSize(new Dimension(200, 38));
        logoutBtn.setForeground(new Color(252, 165, 165)); // Light red
        logoutBtn.addActionListener(e -> {
            if ("guest".equals(currentRole)) {
                cardLayout.show(rootPanel, "LOGIN");
            } else {
                logout();
            }
        });
        sidebar.add(logoutBtn);

        sidebar.revalidate();
        sidebar.repaint();
    }

    private void addNav(String viewName) {
        JButton btn = UIUtils.createNavButton(viewName);
        btn.setMaximumSize(new Dimension(200, 38));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addActionListener(e -> navigateTo(viewName));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(UIUtils.SIDEBAR_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Only reset if it's not the active button
                boolean isActive = btn.getBorder() instanceof javax.swing.border.CompoundBorder;
                if (!isActive) {
                    btn.setBackground(UIUtils.SIDEBAR_BG);
                }
            }
        });
        
        sidebar.add(btn);
    }

    public void navigateTo(String viewName) {
        // Highlight active sidebar button
        for (Component c : sidebar.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                if (b.getText().trim().equals(viewName)) {
                    b.setBackground(UIUtils.SIDEBAR_HOVER);
                    b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, UIUtils.ACCENT_COLOR),
                        new EmptyBorder(10, 11, 10, 15)
                    ));
                } else if (!b.getText().trim().equals("Logout") && !b.getText().trim().equals("Back to Login")) {
                    b.setBackground(UIUtils.SIDEBAR_BG);
                    b.setBorder(new EmptyBorder(10, 15, 10, 15));
                }
            }
        }

        for (Component c : contentArea.getComponents()) {
            if (viewName.equals("Dashboard") && c instanceof Dashboard) ((Dashboard) c).onShow(currentUsername, currentRole);
            if (viewName.equals("Search Books") && c instanceof SearchBooks) ((SearchBooks) c).refreshData();
            if (viewName.equals("Manage Books") && c instanceof ManageBooks) ((ManageBooks) c).refreshData();
            if (viewName.equals("Issue Book") && c instanceof IssueBook) ((IssueBook) c).refreshData();
            if (viewName.equals("Return Book") && c instanceof ReturnBook) ((ReturnBook) c).refreshData();
            if (viewName.equals("Reports") && c instanceof Reports) ((Reports) c).refreshData();
        }
        contentLayout.show(contentArea, viewName);
    }
}
