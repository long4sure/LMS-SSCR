package com.school.lms;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ManageBooks extends JPanel {

    private JTable table;
    private JTextField idField, callNoField, accessionNoField, titleField,
                       authorField, totalField, availableField, searchField;

    public ManageBooks() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_MAIN);

        add(UIUtils.createHeader("Manage Books Catalog"), BorderLayout.NORTH);

        JPanel mainCard = UIUtils.createMainCard();

        // ── Search bar ────────────────────────────────────────────────────
        searchField = UIUtils.createField(true);
        JButton searchBtn = UIUtils.createButton("Search");
        JButton importCsvBtn = UIUtils.createButton("Import CSV");
        JButton templateBtn  = UIUtils.createButton("Download Template");

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel searchLbl = new JLabel("Search: ");
        searchLbl.setFont(UIUtils.FONT_BOLD);
        searchPanel.add(searchLbl, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        JPanel searchRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchRight.setOpaque(false);
        searchRight.add(templateBtn);
        searchRight.add(importCsvBtn);
        searchRight.add(searchBtn);
        searchPanel.add(searchRight, BorderLayout.EAST);
        mainCard.add(searchPanel, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────────────
        table = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Call No.", "Accession No.", "Title", "Author/Editor", "Total", "Available"}, 0){
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
        UIUtils.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(UIUtils.BORDER_COLOR));
        mainCard.add(scroll, BorderLayout.CENTER);

        // ── Form ──────────────────────────────────────────────────────────
        idField         = UIUtils.createField(false);
        callNoField     = UIUtils.createField(true);
        accessionNoField= UIUtils.createField(true);
        titleField      = UIUtils.createField(true);
        authorField     = UIUtils.createField(true);
        totalField      = UIUtils.createField(true);
        availableField  = UIUtils.createField(true);

        JPanel formPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        formPanel.add(lbl("ID:"));               formPanel.add(idField);
        formPanel.add(lbl("Call No:"));          formPanel.add(callNoField);
        formPanel.add(lbl("Accession No:"));     formPanel.add(accessionNoField);
        formPanel.add(lbl("Title:"));            formPanel.add(titleField);
        formPanel.add(lbl("Author/Editor:"));    formPanel.add(authorField);
        formPanel.add(lbl("Total Copies:"));     formPanel.add(totalField);
        formPanel.add(lbl("Available Copies:")); formPanel.add(availableField);

        // ── Buttons ───────────────────────────────────────────────────────
        JButton addBtn     = UIUtils.createButton("Add");
        JButton updateBtn  = UIUtils.createButton("Update");
        JButton deleteBtn  = UIUtils.createDangerButton("Delete");
        JButton clearBtn   = UIUtils.createButton("Clear");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(clearBtn); 
        btnPanel.add(deleteBtn); 
        btnPanel.add(updateBtn); 
        btnPanel.add(addBtn);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(btnPanel, BorderLayout.SOUTH);
        mainCard.add(southPanel, BorderLayout.SOUTH);

        add(mainCard, BorderLayout.CENTER);

        // ── Listeners ─────────────────────────────────────────────────────
        addBtn.addActionListener(e    -> upsertBook(false));
        updateBtn.addActionListener(e -> { if (!idField.getText().isEmpty()) upsertBook(true); });
        deleteBtn.addActionListener(e -> deleteBook());
        clearBtn.addActionListener(e  -> clearForm());
        searchBtn.addActionListener(e -> loadBooks(searchField.getText().trim()));
        searchField.addActionListener(e -> loadBooks(searchField.getText().trim()));
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelection());
        importCsvBtn.addActionListener(e -> importCsv());
        templateBtn.addActionListener(e -> downloadTemplate());

        loadBooks(null);
    }
    
    public void refreshData() {
        loadBooks(searchField.getText().trim());
    }

    private void loadBooks(String keyword) {
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        m.setRowCount(0);
        String sql = "SELECT * FROM books"
                + (keyword != null && !keyword.isEmpty()
                    ? " WHERE title LIKE ? OR author LIKE ? OR call_no LIKE ? OR accession_no LIKE ?"
                    : "")
                + " ORDER BY title ASC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (keyword != null && !keyword.isEmpty()) {
                String like = "%" + keyword + "%";
                ps.setString(1, like); ps.setString(2, like);
                ps.setString(3, like); ps.setString(4, like);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                m.addRow(new Object[]{rs.getInt("id"), rs.getString("call_no"), rs.getString("accession_no"),
                        rs.getString("title"), rs.getString("author"),
                        rs.getInt("total_copies"), rs.getInt("available_copies")});
        } catch (Exception ex) { showErr(ex); }
    }

    private void fillFormFromSelection() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        idField.setText(String.valueOf(table.getValueAt(r, 0)));
        callNoField.setText(String.valueOf(table.getValueAt(r, 1)));
        accessionNoField.setText(String.valueOf(table.getValueAt(r, 2)));
        titleField.setText(String.valueOf(table.getValueAt(r, 3)));
        authorField.setText(String.valueOf(table.getValueAt(r, 4)));
        totalField.setText(String.valueOf(table.getValueAt(r, 5)));
        availableField.setText(String.valueOf(table.getValueAt(r, 6)));
    }

    private void upsertBook(boolean update) {
        String sql = update
                ? "UPDATE books SET call_no=?,accession_no=?,title=?,author=?,total_copies=?,available_copies=? WHERE id=?"
                : "INSERT INTO books (call_no,accession_no,title,author,total_copies,available_copies) VALUES (?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int total = parseInt(totalField.getText());
            int avail = parseInt(availableField.getText());
            if (avail > total) { JOptionPane.showMessageDialog(this, "Available cannot exceed Total."); return; }
            ps.setString(1, callNoField.getText().trim());
            ps.setString(2, accessionNoField.getText().trim());
            ps.setString(3, titleField.getText().trim());
            ps.setString(4, authorField.getText().trim());
            ps.setInt(5, total); ps.setInt(6, avail);
            if (update) ps.setInt(7, parseInt(idField.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, update ? "Book Updated!" : "Book Added!");
            clearForm(); loadBooks(null);
        } catch (Exception ex) { showErr(ex); }
    }

    private void deleteBook() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        int id = (int) table.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete book ID " + id + "?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM books WHERE id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
            clearForm(); loadBooks(null);
        } catch (Exception ex) { showErr(ex); }
    }

    private void clearForm() {
        idField.setText(""); callNoField.setText(""); accessionNoField.setText("");
        titleField.setText(""); authorField.setText(""); totalField.setText(""); availableField.setText("");
        table.clearSelection();
    }

    private void importCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Select CSV File");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fc.getSelectedFile();
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.trim().isEmpty()) continue;
                String[] cols = parseCsvLine(line);
                if (cols.length < 4) continue;
                rows.add(cols);
            }
        } catch (IOException ex) { showErr(ex); return; }

        if (rows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data rows found in the CSV file.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Found " + rows.size() + " book record(s) to import.\nProceed with import?",
                "Confirm CSV Import", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int imported = 0, skipped = 0;
        try (Connection c = DatabaseConnection.getConnection()) {
            for (String[] cols : rows) {
                try {
                    String callNo   = get(cols, 0);
                    String accNo    = get(cols, 1);
                    String title    = get(cols, 2);
                    String author   = get(cols, 3);
                    int total       = parseInt(get(cols, 4));
                    int avail       = parseInt(get(cols, 5));
                    if (total == 0) total = 1;
                    if (avail > total) avail = total;

                    PreparedStatement ps = c.prepareStatement(
                            "INSERT INTO books (call_no,accession_no,title,author,total_copies,available_copies) VALUES (?,?,?,?,?,?)");
                    ps.setString(1, callNo); ps.setString(2, accNo);
                    ps.setString(3, title);  ps.setString(4, author);
                    ps.setInt(5, total);     ps.setInt(6, avail);
                    ps.executeUpdate();
                    imported++;
                } catch (Exception rowEx) { skipped++; }
            }
        } catch (Exception ex) { showErr(ex); return; }

        JOptionPane.showMessageDialog(this, "Import complete!\nImported: " + imported + "\nSkipped: " + skipped);
        loadBooks(null);
    }

    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') { inQuotes = !inQuotes; }
            else if (c == ',' && !inQuotes) { fields.add(sb.toString().trim()); sb.setLength(0); }
            else { sb.append(c); }
        }
        fields.add(sb.toString().trim());
        return fields.toArray(new String[0]);
    }

    private String get(String[] arr, int i) {
        return (arr != null && i < arr.length && arr[i] != null) ? arr[i].trim() : "";
    }

    private void downloadTemplate() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save CSV Template");
        fc.setSelectedFile(new File("books_template.csv"));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fc.getSelectedFile();
        if (!file.getName().endsWith(".csv")) file = new File(file.getAbsolutePath() + ".csv");

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("Call No.,Accession No.,Title,Author/Editor,Total Copies,Available Copies");
            pw.println("REF-001,ACC-001,\"Introduction to Computing\",John Doe,3,3");
            JOptionPane.showMessageDialog(this, "Template saved!");
        } catch (IOException ex) { showErr(ex); }
    }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text, SwingConstants.RIGHT);
        l.setFont(UIUtils.FONT_BOLD);
        l.setForeground(UIUtils.TEXT_PRIMARY);
        l.setBorder(new EmptyBorder(0, 0, 0, 10));
        return l;
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s == null ? "0" : s.trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private void showErr(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
}
