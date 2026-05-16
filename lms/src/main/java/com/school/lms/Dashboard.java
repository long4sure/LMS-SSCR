package com.school.lms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Dashboard extends JPanel {

    private JLabel availableBooksLabel, borrowedBooksLabel, overdueBooksLabel, totalBooksLabel;
    private JLabel updatedLabel, welcomeLabel;
    private JTable recentTable;
    private Connection conn;
    private MainFrame mainFrame;
    private String currentRole;

    public Dashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_MAIN);

        add(UIUtils.createHeader("Dashboard Overview"), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);

        connectDB();
        
        Timer t = new Timer(10000, e -> updateDashboard());
        t.start();
    }
    
    public void onShow(String username, String role) {
        this.currentRole = role;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome back, " + username + "!");
        }
        updateDashboard();
    }

    private JPanel createCenterPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        // Welcome text
        welcomeLabel = new JLabel("Welcome back!");
        welcomeLabel.setFont(UIUtils.FONT_H2);
        welcomeLabel.setForeground(UIUtils.TEXT_PRIMARY);
        welcomeLabel.setBorder(new EmptyBorder(0, 0, 15, 0)); // Tighter
        wrapper.add(welcomeLabel, BorderLayout.NORTH);

        // Cards grid
        JPanel grid = new JPanel(new GridLayout(1, 4, 15, 15)); // Compact spacing
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(10, 0, 10, 0));

        totalBooksLabel    = addSummaryCard(grid, "Total Books");
        availableBooksLabel= addSummaryCard(grid, "Available Copies");
        borrowedBooksLabel = addSummaryCard(grid, "Currently Borrowed");
        overdueBooksLabel  = addSummaryCard(grid, "Overdue Books");

        // Wrapper for grid and recent activity
        JPanel contentGrid = new JPanel(new BorderLayout(0, 20));
        contentGrid.setOpaque(false);
        contentGrid.add(grid, BorderLayout.NORTH);
        
        // Recent Activity Table
        JPanel recentPanel = new JPanel(new BorderLayout(0, 10));
        recentPanel.setOpaque(false);
        recentPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel recentTitle = new JLabel("Recent Activity");
        recentTitle.setFont(UIUtils.FONT_H2);
        recentTitle.setForeground(UIUtils.TEXT_PRIMARY);
        recentPanel.add(recentTitle, BorderLayout.NORTH);
        
        recentTable = new JTable(new javax.swing.table.DefaultTableModel(
                new Object[]{"Action", "Book Title", "Borrower", "Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        UIUtils.styleTable(recentTable);
        JScrollPane scroll = new JScrollPane(recentTable);
        scroll.setBorder(javax.swing.BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));
        scroll.setPreferredSize(new Dimension(0, 180)); // Compact height
        recentPanel.add(scroll, BorderLayout.CENTER);
        
        contentGrid.add(recentPanel, BorderLayout.CENTER);

        updatedLabel = new JLabel("Updated just now", SwingConstants.LEFT);
        updatedLabel.setForeground(UIUtils.TEXT_MUTED);
        updatedLabel.setFont(UIUtils.FONT_REGULAR);
        updatedLabel.setBorder(new EmptyBorder(10, 0, 0, 0));

        wrapper.add(contentGrid, BorderLayout.CENTER);
        wrapper.add(updatedLabel, BorderLayout.SOUTH);
        return wrapper;
    }

    private JLabel addSummaryCard(JPanel parent, String title) {
        JPanel card = new JPanel(new BorderLayout(0, 5)); // Tighter
        card.setBackground(UIUtils.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIUtils.BORDER_COLOR, 1, true),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(UIUtils.FONT_BOLD);
        titleLbl.setForeground(UIUtils.TEXT_MUTED);

        JLabel valueLbl = new JLabel("—", SwingConstants.CENTER);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 48));
        valueLbl.setForeground(title.contains("Overdue") ? new Color(220, 38, 38) : UIUtils.ACCENT_COLOR);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(UIUtils.ACCENT_HOVER, 2, true),
                        new EmptyBorder(14, 14, 14, 14)));
                card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(UIUtils.BORDER_COLOR, 1, true),
                        new EmptyBorder(15, 15, 15, 15)));
                card.setCursor(Cursor.getDefaultCursor());
            }
            public void mouseClicked(MouseEvent e) {
                if (title.contains("Total") || title.contains("Available")) {
                    if (!"staff".equalsIgnoreCase(currentRole) && !"guest".equals(currentRole)) 
                        mainFrame.navigateTo("Manage Books");
                } else if (title.contains("Borrowed")) {
                    if (!"guest".equals(currentRole)) mainFrame.navigateTo("Return Book");
                } else if (title.contains("Overdue")) {
                    if (!"guest".equals(currentRole)) mainFrame.navigateTo("Reports");
                }
            }
        });

        parent.add(card);
        return valueLbl;
    }

    private void connectDB() {
        try { conn = DatabaseConnection.getConnection(); }
        catch (Exception e) { System.out.println("DB err: " + e.getMessage()); }
    }

    private void updateDashboard() {
        if (conn == null) return;
        try (Statement s = conn.createStatement()) {
            totalBooksLabel.setText(    queryValue(s, "SELECT COUNT(*) FROM books"));
            availableBooksLabel.setText(queryValue(s, "SELECT COALESCE(SUM(available_copies),0) FROM books"));
            borrowedBooksLabel.setText( queryValue(s, "SELECT COUNT(*) FROM issued_books WHERE return_date IS NULL"));
            overdueBooksLabel.setText(  queryValue(s,
                    "SELECT COUNT(*) FROM issued_books WHERE return_date IS NULL AND due_date < CURRENT_DATE"));
            updatedLabel.setText("Last updated: " + new java.util.Date());
            
            loadRecentActivity();
        } catch (SQLException e) {
            System.out.println("Update error: " + e.getMessage());
        }
    }

    private void loadRecentActivity() {
        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) recentTable.getModel();
        m.setRowCount(0);
        String sql = "SELECT 'Issued' as action_type, b.title, ib.borrower_name, ib.issue_date as act_date "
                   + "FROM issued_books ib JOIN books b ON ib.book_id = b.id "
                   + "UNION ALL "
                   + "SELECT 'Returned' as action_type, b.title, ib.borrower_name, ib.return_date as act_date "
                   + "FROM issued_books ib JOIN books b ON ib.book_id = b.id WHERE ib.return_date IS NOT NULL "
                   + "ORDER BY act_date DESC LIMIT 5";
        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while(rs.next()) {
                m.addRow(new Object[]{
                    rs.getString("action_type"),
                    rs.getString("title"),
                    rs.getString("borrower_name"),
                    rs.getDate("act_date")
                });
            }
        } catch(SQLException ex) { ex.printStackTrace(); }
    }

    private String queryValue(Statement s, String q) throws SQLException {
        try (ResultSet rs = s.executeQuery(q)) {
            return rs.next() && rs.getString(1) != null ? rs.getString(1) : "0";
        }
    }
}
