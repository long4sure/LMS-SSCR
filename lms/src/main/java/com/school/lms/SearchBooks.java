package com.school.lms;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class SearchBooks extends JPanel {

    private JTextField searchField;
    private JTable resultsTable;

    public SearchBooks() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_MAIN);

        add(UIUtils.createHeader("Search Catalog"), BorderLayout.NORTH);

        JPanel mainCard = UIUtils.createMainCard();

        // ── Search panel ──────────────────────────────────────────────────
        searchField = UIUtils.createField(true);
        JButton searchBtn  = UIUtils.createButton("Search");
        JButton showAllBtn = UIUtils.createButton("Show All");

        JPanel searchInner = new JPanel(new BorderLayout(8, 0));
        searchInner.setOpaque(false);
        searchInner.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel lbl = new JLabel("Keyword: ");
        lbl.setFont(UIUtils.FONT_BOLD);
        searchInner.add(lbl, BorderLayout.WEST);
        searchInner.add(searchField, BorderLayout.CENTER);
        
        JPanel sRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        sRight.setOpaque(false);
        sRight.add(showAllBtn); sRight.add(searchBtn);
        searchInner.add(sRight, BorderLayout.EAST);
        mainCard.add(searchInner, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────────────
        resultsTable = new JTable(new DefaultTableModel(
                new String[]{"Book ID", "Call No.", "Accession No.", "Title", "Author/Editor", "Available"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        UIUtils.styleTable(resultsTable);

        // Color code availability
        resultsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, isSel, hasFocus, row, col);
                ((JComponent) c).setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!isSel) {
                    Object avail = tbl.getValueAt(row, 5);
                    int copies = avail instanceof Integer i ? i : 0;
                    c.setBackground(copies == 0 ? new Color(245, 245, 245)
                            : (row % 2 == 0 ? UIUtils.ROW_EVEN : UIUtils.ROW_ODD));
                    c.setForeground(copies == 0 ? UIUtils.TEXT_MUTED : UIUtils.TEXT_PRIMARY);
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(resultsTable);
        scroll.setBorder(new LineBorder(UIUtils.BORDER_COLOR));
        mainCard.add(scroll, BorderLayout.CENTER);

        JLabel hint = new JLabel("Grayed-out rows have no available copies.");
        hint.setFont(UIUtils.FONT_REGULAR);
        hint.setForeground(UIUtils.TEXT_MUTED);
        hint.setBorder(new EmptyBorder(10, 0, 0, 0));
        mainCard.add(hint, BorderLayout.SOUTH);

        add(mainCard, BorderLayout.CENTER);

        // ── Listeners ─────────────────────────────────────────────────────
        searchBtn.addActionListener(e  -> doSearch(searchField.getText().trim()));
        showAllBtn.addActionListener(e -> loadAllBooks());
        searchField.addActionListener(e -> doSearch(searchField.getText().trim()));

        loadAllBooks();
    }
    
    public void refreshData() {
        doSearch(searchField.getText().trim());
    }

    private void loadAllBooks() {
        DefaultTableModel m = (DefaultTableModel) resultsTable.getModel();
        m.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id, call_no, accession_no, title, author, available_copies FROM books ORDER BY title ASC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                m.addRow(new Object[]{rs.getInt("id"), rs.getString("call_no"), rs.getString("accession_no"),
                        rs.getString("title"), rs.getString("author"), rs.getInt("available_copies")});
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void doSearch(String keyword) {
        if (keyword.isEmpty()) { loadAllBooks(); return; }
        DefaultTableModel m = (DefaultTableModel) resultsTable.getModel();
        m.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id, call_no, accession_no, title, author, available_copies FROM books "
                     + "WHERE title LIKE ? OR author LIKE ? OR call_no LIKE ? OR accession_no LIKE ? ORDER BY title ASC")) {
            String like = "%" + keyword + "%";
            for (int i = 1; i <= 4; i++) ps.setString(i, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                m.addRow(new Object[]{rs.getInt("id"), rs.getString("call_no"), rs.getString("accession_no"),
                        rs.getString("title"), rs.getString("author"), rs.getInt("available_copies")});
        } catch (Exception e) { e.printStackTrace(); }
    }
}
