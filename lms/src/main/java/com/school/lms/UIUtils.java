package com.school.lms;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Shared UI utilities — Professional Clean Light Theme
 */
public class UIUtils {

    // ── Brand Colors (Clean Light Theme with Dark Sidebar) ──────────────────
    public static final Color BG_MAIN      = new Color(241, 245, 249); // Slate 100 (Soft background, not blinding)
    public static final Color SIDEBAR_BG   = new Color(15, 23, 42);    // Slate 900 (Dark Sidebar)
    public static final Color SIDEBAR_HOVER= new Color(30, 41, 59);    // Slate 800
    public static final Color CARD_BG      = Color.WHITE;              // White Cards
    
    public static final Color ACCENT_COLOR = new Color(220, 38, 38);   // Red 600
    public static final Color ACCENT_HOVER = new Color(185, 28, 28);   // Red 700
    
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);    // Slate 900 (Dark text)
    public static final Color TEXT_MUTED   = new Color(100, 116, 139); // Slate 500 (Gray text)
    
    public static final Color BORDER_COLOR = new Color(226, 232, 240); // Slate 200
    
    public static final Color ROW_EVEN     = Color.WHITE;
    public static final Color ROW_ODD      = new Color(248, 250, 252); // Slate 50
    public static final Color ROW_OVERDUE  = new Color(254, 226, 226); // Red 100

    // ── Typography (Compact) ─────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_H2      = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BOLD    = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);

    // ── Buttons ──────────────────────────────────────────────────────────────

    public static JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(FONT_BOLD);
        b.setBackground(ACCENT_COLOR);
        b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(6, 16, 6, 16)); // Compact padding
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if(b.isEnabled()) b.setBackground(ACCENT_HOVER); }
            public void mouseExited(MouseEvent e)  { if(b.isEnabled()) b.setBackground(ACCENT_COLOR); }
        });
        return b;
    }

    public static JButton createSecondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(FONT_BOLD);
        b.setBackground(CARD_BG);
        b.setForeground(TEXT_PRIMARY);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(5, 15, 5, 15)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if(b.isEnabled()) b.setBackground(BG_MAIN); }
            public void mouseExited(MouseEvent e)  { if(b.isEnabled()) b.setBackground(CARD_BG); }
        });
        return b;
    }

    public static JButton createDangerButton(String text) {
        return createButton(text); // Already red in this theme
    }
    
    public static JButton createNavButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setBackground(SIDEBAR_BG);
        b.setForeground(Color.WHITE);
        // Fully left aligned with compact padding
        b.setBorder(new EmptyBorder(10, 15, 10, 15)); 
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Form Fields ──────────────────────────────────────────────────────────

    public static JTextField createField(boolean editable) {
        JTextField t = new JTextField();
        t.setEnabled(editable);
        t.setFont(FONT_REGULAR);
        t.setForeground(TEXT_PRIMARY);
        t.setCaretColor(TEXT_PRIMARY);
        t.setBackground(editable ? CARD_BG : BG_MAIN);
        t.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(5, 8, 5, 8))); // Compact
                
        // Subtle focus animation
        t.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { t.setBorder(BorderFactory.createCompoundBorder(new LineBorder(ACCENT_COLOR, 1), new EmptyBorder(5, 8, 5, 8))); }
            public void focusLost(FocusEvent e)   { t.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER_COLOR, 1), new EmptyBorder(5, 8, 5, 8))); }
        });
        return t;
    }

    // ── Table Styling (Compact) ──────────────────────────────────────────────

    public static void styleTable(JTable t) {
        t.setFont(FONT_REGULAR);
        t.setForeground(TEXT_PRIMARY);
        t.setRowHeight(26); // Compact to show more data
        t.setGridColor(BORDER_COLOR);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(new Color(224, 242, 254)); // Light Blue
        t.setSelectionForeground(TEXT_PRIMARY);
        t.setFillsViewportHeight(true);
        t.setBackground(CARD_BG);
        
        t.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = t.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(SIDEBAR_BG);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(0, 30)); // Compact
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setBorder(new EmptyBorder(0, 8, 0, 8));

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, isSel, hasFocus, row, col);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                if (!isSel) c.setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                return c;
            }
        });
    }

    // ── Headers & Cards ──────────────────────────────────────────────────────

    public static JPanel createHeader(String titleText) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_MAIN);
        header.setBorder(new EmptyBorder(0, 0, 10, 0)); // Compact
        
        JLabel title = new JLabel(titleText);
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        
        return header;
    }

    public static JPanel createMainCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10)); // Tighter gap
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(15, 15, 15, 15))); // Compact padding
        return card;
    }

    public static void registerEscClose(JDialog dialog) {
        dialog.getRootPane().registerKeyboardAction(
                e -> dialog.dispose(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
}
