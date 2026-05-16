package com.school.lms;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReturnBook extends JPanel {

    private JTable table;
    private JTextField searchField;

    public ReturnBook() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_MAIN);

        add(UIUtils.createHeader("Return Book"), BorderLayout.NORTH);

        JPanel mainCard = UIUtils.createMainCard();

        // ── Search bar ────────────────────────────────────────────────────
        searchField = UIUtils.createField(true);
        JButton searchBtn  = UIUtils.createButton("Search");

        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel lbl = new JLabel("Search (Borrower/Title): ");
        lbl.setFont(UIUtils.FONT_BOLD);
        top.add(lbl, BorderLayout.WEST);
        top.add(searchField, BorderLayout.CENTER);
        
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topRight.setOpaque(false);
        topRight.add(searchBtn);
        top.add(topRight, BorderLayout.EAST);
        mainCard.add(top, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────────────
        table = new JTable(new DefaultTableModel(
                new Object[]{"Txn ID", "Book ID", "Call No.", "Accession No.",
                        "Title", "Author", "Borrower", "Student ID",
                        "Issue Date", "Due Date", "Fine (₱)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        UIUtils.styleTable(table);

        // Override renderer to highlight overdue rows in red
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                
                if (col == 10 && val instanceof Double d) {
                    val = d > 0 ? String.format("₱ %.2f", d) : "—";
                }
                
                Component c = super.getTableCellRendererComponent(tbl, val, isSel, hasFocus, row, col);
                ((JComponent) c).setBorder(new EmptyBorder(0, 10, 0, 10));
                
                if (!isSel) {
                    Object fineObj = tbl.getValueAt(row, 10);
                    double fine = (fineObj instanceof Double d2) ? d2 : 0.0;
                    c.setBackground(fine > 0 ? UIUtils.ROW_OVERDUE
                            : (row % 2 == 0 ? UIUtils.ROW_EVEN : UIUtils.ROW_ODD));
                    if (fine > 0) c.setForeground(new Color(180, 0, 0));
                    else c.setForeground(UIUtils.TEXT_PRIMARY);
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(UIUtils.BORDER_COLOR));
        mainCard.add(scroll, BorderLayout.CENTER);

        // ── Bottom buttons ────────────────────────────────────────────────
        JLabel legendLabel = new JLabel("Red rows indicate overdue books with fines.");
        legendLabel.setFont(UIUtils.FONT_REGULAR);
        legendLabel.setForeground(UIUtils.ACCENT_COLOR);

        JButton returnBtn = UIUtils.createButton("Mark as Returned");

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.setBorder(new EmptyBorder(15, 0, 0, 0));
        south.add(legendLabel, BorderLayout.WEST);
        
        JPanel sRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        sRight.setOpaque(false);
        sRight.add(returnBtn);
        south.add(sRight, BorderLayout.EAST);
        mainCard.add(south, BorderLayout.SOUTH);

        add(mainCard, BorderLayout.CENTER);

        // ── Listeners ─────────────────────────────────────────────────────
        searchBtn.addActionListener(e  -> loadOutstanding(searchField.getText().trim()));
        searchField.addActionListener(e -> loadOutstanding(searchField.getText().trim()));
        returnBtn.addActionListener(e  -> markReturned());

        loadOutstanding(null);
    }
    
    public void refreshData() {
        loadOutstanding(searchField.getText().trim());
    }

    private void loadOutstanding(String q) {
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        m.setRowCount(0);

        String sql = "SELECT ib.id, b.id AS book_id, b.call_no, b.accession_no, b.title, b.author, "
                + "ib.borrower_name, ib.borrower_id, ib.issue_date, ib.due_date "
                + "FROM issued_books ib JOIN books b ON ib.book_id=b.id "
                + "WHERE ib.return_date IS NULL ";
        if (q != null && !q.isEmpty())
            sql += "AND (ib.borrower_name LIKE ? OR b.title LIKE ? OR b.call_no LIKE ? OR b.accession_no LIKE ?) ";
        sql += "ORDER BY ib.issue_date DESC";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (q != null && !q.isEmpty()) {
                String like = "%" + q + "%";
                ps.setString(1, like); ps.setString(2, like);
                ps.setString(3, like); ps.setString(4, like);
            }
            ResultSet rs = ps.executeQuery();
            LocalDate today = LocalDate.now();
            double ratePerDay = AppConfig.getFineRatePerDay();

            while (rs.next()) {
                LocalDate due = rs.getDate("due_date").toLocalDate();
                long daysOver = ChronoUnit.DAYS.between(due, today);
                double fine   = daysOver > 0 ? daysOver * ratePerDay : 0.0;

                m.addRow(new Object[]{
                        rs.getInt("id"), rs.getInt("book_id"),
                        rs.getString("call_no"), rs.getString("accession_no"),
                        rs.getString("title"), rs.getString("author"),
                        rs.getString("borrower_name"), rs.getString("borrower_id"),
                        rs.getDate("issue_date"), rs.getDate("due_date"),
                        fine
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void markReturned() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }

        int txnId  = (int)    table.getValueAt(r, 0);
        int bookId = (int)    table.getValueAt(r, 1);
        String borrower = (String) table.getValueAt(r, 6);
        Object fineObj  = table.getValueAt(r, 10);
        double fine = (fineObj instanceof Double d) ? d : 0.0;

        String msg = "Confirm return from " + borrower + "?";
        if (fine > 0) msg += "\n\nOverdue fine: ₱" + String.format("%.2f", fine);

        if (JOptionPane.showConfirmDialog(this, msg, "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        try (Connection c = DatabaseConnection.getConnection()) {
            PreparedStatement upTxn  = c.prepareStatement("UPDATE issued_books SET return_date = CURRENT_DATE WHERE id=?");
            upTxn.setInt(1, txnId); upTxn.executeUpdate();

            PreparedStatement upBook = c.prepareStatement("UPDATE books SET available_copies = available_copies + 1 WHERE id=?");
            upBook.setInt(1, bookId); upBook.executeUpdate();

            String doneMsg = "Book returned!";
            if (fine > 0) doneMsg += "\nFine collected: ₱" + String.format("%.2f", fine);
            JOptionPane.showMessageDialog(this, doneMsg);
            loadOutstanding(null);
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
