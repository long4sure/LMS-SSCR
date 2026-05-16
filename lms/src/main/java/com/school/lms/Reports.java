package com.school.lms;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.sql.*;

public class Reports extends JPanel {

    private JTable historyTable, overdueTable;
    private JLabel statIssuedLbl, statBorrowedLbl, statReturnedLbl, statOverdueLbl;

    public Reports() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_MAIN);

        add(UIUtils.createHeader("Reports & History"), BorderLayout.NORTH);

        JPanel mainCard = UIUtils.createMainCard();

        // ── Summary stats bar ─────────────────────────────────────────────
        JPanel statsBar = buildStatsBar();
        mainCard.add(statsBar, BorderLayout.NORTH);

        // ── Tabs ──────────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIUtils.FONT_BOLD);
        tabs.setBackground(UIUtils.SIDEBAR_BG);
        tabs.setForeground(UIUtils.TEXT_PRIMARY);

        // All Transactions tab
        historyTable = new JTable(new DefaultTableModel(
                new Object[]{"Txn ID","Book ID","Call No.","Title","Borrower","Student ID","Issued","Due","Returned"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        UIUtils.styleTable(historyTable);
        JButton refreshHistory  = UIUtils.createButton("Refresh");
        JButton exportHistoryCsv = UIUtils.createButton("Export CSV");
        refreshHistory.addActionListener(e   -> { loadHistory(); loadStats(); });
        exportHistoryCsv.addActionListener(e -> exportCsv(historyTable, "transactions_export.csv"));
        tabs.addTab("All Transactions", buildTabPanel(historyTable, refreshHistory, exportHistoryCsv));

        // Overdue tab
        overdueTable = new JTable(new DefaultTableModel(
                new Object[]{"Txn ID","Book ID","Call No.","Title","Borrower","Student ID","Due","Days Overdue","Fine (₱)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        UIUtils.styleTable(overdueTable);
        // Red highlight for overdue table
        overdueTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, isSel, hasFocus, row, col);
                ((JComponent) c).setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!isSel) {
                    c.setBackground(row % 2 == 0 ? new Color(255, 240, 240) : UIUtils.ROW_OVERDUE);
                    c.setForeground(new Color(180, 0, 0));
                }
                return c;
            }
        });

        JButton refreshOverdue  = UIUtils.createButton("Refresh");
        JButton exportOverdueCsv = UIUtils.createButton("Export CSV");
        refreshOverdue.addActionListener(e   -> { loadOverdue(); loadStats(); });
        exportOverdueCsv.addActionListener(e -> exportCsv(overdueTable, "overdue_export.csv"));
        tabs.addTab("Overdue Books", buildTabPanel(overdueTable, refreshOverdue, exportOverdueCsv));

        mainCard.add(tabs, BorderLayout.CENTER);
        add(mainCard, BorderLayout.CENTER);

        loadHistory();
        loadOverdue();
        loadStats();
    }
    
    public void refreshData() {
        loadHistory();
        loadOverdue();
        loadStats();
    }

    private JPanel buildStatsBar() {
        JPanel bar = new JPanel(new GridLayout(1, 4, 15, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 20, 0));

        statIssuedLbl   = statCard(bar, "Total Issued");
        statBorrowedLbl = statCard(bar, "Currently Out");
        statReturnedLbl = statCard(bar, "Returned");
        statOverdueLbl  = statCard(bar, "Overdue");
        return bar;
    }

    private JLabel statCard(JPanel parent, String title) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(UIUtils.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIUtils.BORDER_COLOR, 1, true),
                new EmptyBorder(15, 16, 15, 16)));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(UIUtils.FONT_BOLD);
        titleLbl.setForeground(UIUtils.TEXT_MUTED);

        JLabel valueLbl = new JLabel("—", SwingConstants.CENTER);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLbl.setForeground(title.contains("Overdue") ? new Color(220, 38, 38) : UIUtils.ACCENT_COLOR);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        parent.add(card);
        return valueLbl;
    }

    private void loadStats() {
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            statIssuedLbl.setText(  query(s, "SELECT COUNT(*) FROM issued_books"));
            statBorrowedLbl.setText(query(s, "SELECT COUNT(*) FROM issued_books WHERE return_date IS NULL"));
            statReturnedLbl.setText(query(s, "SELECT COUNT(*) FROM issued_books WHERE return_date IS NOT NULL"));
            statOverdueLbl.setText( query(s, "SELECT COUNT(*) FROM issued_books WHERE return_date IS NULL AND due_date < CURRENT_DATE"));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private String query(Statement s, String sql) throws SQLException {
        try (ResultSet rs = s.executeQuery(sql)) {
            return rs.next() ? rs.getString(1) : "0";
        }
    }

    private JPanel buildTabPanel(JTable t, JButton refreshBtn, JButton exportBtn) {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(UIUtils.CARD_BG);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 10, 0));
        top.add(exportBtn);
        top.add(refreshBtn);
        p.add(top, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(t);
        scroll.setBorder(new LineBorder(UIUtils.BORDER_COLOR));
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private void loadHistory() {
        DefaultTableModel m = (DefaultTableModel) historyTable.getModel();
        m.setRowCount(0);
        String sql = "SELECT ib.id, b.id AS book_id, b.call_no, b.title, "
                + "ib.borrower_name, ib.borrower_id, ib.issue_date, ib.due_date, ib.return_date "
                + "FROM issued_books ib JOIN books b ON ib.book_id=b.id ORDER BY ib.issue_date DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                m.addRow(new Object[]{rs.getInt("id"), rs.getInt("book_id"),
                        rs.getString("call_no"), rs.getString("title"),
                        rs.getString("borrower_name"), rs.getString("borrower_id"),
                        rs.getDate("issue_date"), rs.getDate("due_date"), rs.getDate("return_date")});
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadOverdue() {
        DefaultTableModel m = (DefaultTableModel) overdueTable.getModel();
        m.setRowCount(0);
        String sql = "SELECT ib.id, b.id AS book_id, b.call_no, b.title, "
                + "ib.borrower_name, ib.borrower_id, ib.due_date, "
                + "DATEDIFF('DAY', ib.due_date, CURRENT_DATE) AS days_over "
                + "FROM issued_books ib JOIN books b ON ib.book_id=b.id "
                + "WHERE ib.return_date IS NULL AND ib.due_date < CURRENT_DATE ORDER BY ib.due_date ASC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            double rate = AppConfig.getFineRatePerDay();
            while (rs.next()) {
                int daysOver = rs.getInt("days_over");
                double fine  = daysOver * rate;
                m.addRow(new Object[]{rs.getInt("id"), rs.getInt("book_id"),
                        rs.getString("call_no"), rs.getString("title"),
                        rs.getString("borrower_name"), rs.getString("borrower_id"),
                        rs.getDate("due_date"),
                        daysOver, String.format("₱ %.2f", fine)});
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void exportCsv(JTable t, String defaultName) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(defaultName));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fc.getSelectedFile();
        if (!file.getName().endsWith(".csv")) file = new File(file.getAbsolutePath() + ".csv");

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            TableModel m = t.getModel();
            StringBuilder header = new StringBuilder();
            for (int col = 0; col < m.getColumnCount(); col++) {
                if (col > 0) header.append(",");
                header.append(quoted(m.getColumnName(col)));
            }
            pw.println(header);

            for (int row = 0; row < m.getRowCount(); row++) {
                StringBuilder sb = new StringBuilder();
                for (int col = 0; col < m.getColumnCount(); col++) {
                    if (col > 0) sb.append(",");
                    Object val = m.getValueAt(row, col);
                    sb.append(quoted(val == null ? "" : val.toString()));
                }
                pw.println(sb);
            }

            JOptionPane.showMessageDialog(this, "Exported " + m.getRowCount() + " row(s) successfully!");
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private String quoted(String s) {
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
}
