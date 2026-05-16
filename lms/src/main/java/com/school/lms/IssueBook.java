package com.school.lms;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class IssueBook extends JPanel {

    private JTable table;
    private JTextField bookIdField, borrowerNameField, borrowerIdField, dueDateField, searchField;

    public IssueBook() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_MAIN);

        add(UIUtils.createHeader("Issue Book to Borrower"), BorderLayout.NORTH);

        JPanel mainCard = UIUtils.createMainCard();

        // ── Search ────────────────────────────────────────────────────────
        searchField = UIUtils.createField(true);
        JButton searchBtn  = UIUtils.createButton("Search");

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel lbl = new JLabel("Search Available Books: ");
        lbl.setFont(UIUtils.FONT_BOLD);
        searchPanel.add(lbl, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        JPanel sRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        sRight.setOpaque(false);
        sRight.add(searchBtn);
        searchPanel.add(sRight, BorderLayout.EAST);
        mainCard.add(searchPanel, BorderLayout.NORTH);

        // ── Table (available books only) ──────────────────────────────────
        table = new JTable(new DefaultTableModel(
                new Object[]{"Book ID", "Call No.", "Accession No.", "Title", "Author/Editor", "Available"}, 0){
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        UIUtils.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(UIUtils.BORDER_COLOR));
        mainCard.add(scroll, BorderLayout.CENTER);

        // ── Form ──────────────────────────────────────────────────────────
        bookIdField      = UIUtils.createField(false);
        borrowerNameField= UIUtils.createField(true);
        borrowerIdField  = UIUtils.createField(true);
        dueDateField     = UIUtils.createField(true);
        dueDateField.setText(LocalDate.now().plusDays(7).toString());

        JPanel form = new JPanel(new GridLayout(2, 4, 10, 10));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(15, 0, 15, 0));
        form.add(lbl("Book ID (auto):"));           form.add(bookIdField);
        form.add(lbl("Borrower Name:"));            form.add(borrowerNameField);
        form.add(lbl("Student/ID No:"));            form.add(borrowerIdField);
        form.add(lbl("Due (YYYY-MM-DD):"));         form.add(dueDateField);

        JButton issueBtn = UIUtils.createButton("Issue Book");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(issueBtn);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(form, BorderLayout.CENTER);
        southPanel.add(btnPanel, BorderLayout.SOUTH);
        mainCard.add(southPanel, BorderLayout.SOUTH);

        add(mainCard, BorderLayout.CENTER);

        // ── Listeners ─────────────────────────────────────────────────────
        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) bookIdField.setText(String.valueOf(table.getValueAt(r, 0)));
        });
        searchBtn.addActionListener(e  -> loadAvailableBooks(searchField.getText().trim()));
        searchField.addActionListener(e -> loadAvailableBooks(searchField.getText().trim()));
        issueBtn.addActionListener(e   -> issueBook());

        loadAvailableBooks(null);
    }
    
    public void refreshData() {
        loadAvailableBooks(searchField.getText().trim());
    }

    private void loadAvailableBooks(String keyword) {
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        m.setRowCount(0);
        String sql = "SELECT id, call_no, accession_no, title, author, available_copies "
                + "FROM books WHERE available_copies > 0";
        if (keyword != null && !keyword.isEmpty())
            sql += " AND (title LIKE ? OR author LIKE ? OR call_no LIKE ? OR accession_no LIKE ?)";
        sql += " ORDER BY id DESC";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (keyword != null && !keyword.isEmpty()) {
                String like = "%" + keyword + "%";
                ps.setString(1, like); ps.setString(2, like);
                ps.setString(3, like); ps.setString(4, like);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                m.addRow(new Object[]{rs.getInt("id"), rs.getString("call_no"),
                        rs.getString("accession_no"), rs.getString("title"),
                        rs.getString("author"), rs.getInt("available_copies")});
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void issueBook() {
        String borrower = borrowerNameField.getText().trim();
        String borrowerId = borrowerIdField.getText().trim();
        String bookIdStr  = bookIdField.getText().trim();
        String dueDateStr = dueDateField.getText().trim();

        if (bookIdStr.isEmpty())  { JOptionPane.showMessageDialog(this, "Select a book from the table."); return; }
        if (borrower.isEmpty())   { JOptionPane.showMessageDialog(this, "Borrower name cannot be empty."); return; }

        LocalDate due;
        try {
            due = LocalDate.parse(dueDateStr);
            if (due.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "Due date cannot be in the past.");
                return;
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD."); return; }

        int bookId = Integer.parseInt(bookIdStr);
        String bookTitle = "";
        int r = table.getSelectedRow();
        if (r >= 0) bookTitle = String.valueOf(table.getValueAt(r, 3));

        if (JOptionPane.showConfirmDialog(this,
                "Issue this book to " + borrower + "?\n" + bookTitle,
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        try (Connection c = DatabaseConnection.getConnection()) {
            PreparedStatement chk = c.prepareStatement("SELECT available_copies FROM books WHERE id=?");
            chk.setInt(1, bookId);
            ResultSet rs = chk.executeQuery();
            if (!rs.next() || rs.getInt(1) <= 0) { JOptionPane.showMessageDialog(this, "No copies available."); return; }

            PreparedStatement ins = c.prepareStatement(
                    "INSERT INTO issued_books (book_id, borrower_name, borrower_id, issue_date, due_date) VALUES (?,?,?,CURRENT_DATE,?)");
            ins.setInt(1, bookId);
            ins.setString(2, borrower);
            ins.setString(3, borrowerId.isEmpty() ? null : borrowerId);
            ins.setDate(4, java.sql.Date.valueOf(due));
            ins.executeUpdate();

            PreparedStatement upd = c.prepareStatement("UPDATE books SET available_copies = available_copies - 1 WHERE id=?");
            upd.setInt(1, bookId);
            upd.executeUpdate();

            JOptionPane.showMessageDialog(this, "Book issued successfully!");
            loadAvailableBooks(null);
            borrowerNameField.setText("");
            borrowerIdField.setText("");
            bookIdField.setText("");
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text, SwingConstants.RIGHT);
        l.setFont(UIUtils.FONT_BOLD);
        l.setForeground(UIUtils.TEXT_PRIMARY);
        l.setBorder(new EmptyBorder(0, 0, 0, 10));
        return l;
    }
}
