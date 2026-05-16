package com.school.lms;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * About dialog - Clean typography and layout without broken images.
 */
public class AboutDialog extends JDialog {

    public AboutDialog(Frame parent) {
        super(parent, "About - " + AppConfig.getLibraryName(), true);
        setSize(550, 400); // Increased size slightly to fit the new features text
        setLocationRelativeTo(parent);
        setResizable(false);
        UIUtils.registerEscClose(this);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.CARD_BG);

        // Simple text header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtils.SIDEBAR_BG);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("About");
        title.setFont(UIUtils.FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        // Info content
        JPanel info = new JPanel();
        info.setBackground(UIUtils.CARD_BG);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(25, 30, 20, 30));

        info.add(centeredLabel(AppConfig.getLibraryName(), Font.BOLD, 18, UIUtils.TEXT_PRIMARY));
        info.add(Box.createVerticalStrut(8));
        info.add(centeredLabel("Library Management System  v" + AppConfig.getVersion(), Font.PLAIN, 14, UIUtils.TEXT_MUTED));
        info.add(Box.createVerticalStrut(25));
        info.add(centeredLabel("Developed as a Capstone Project by Jerome Misa", Font.PLAIN, 14, UIUtils.TEXT_PRIMARY));
        info.add(centeredLabel("San Sebastian College Recoletos - Canlubang", Font.ITALIC, 13, UIUtils.TEXT_MUTED));
        
        info.add(Box.createVerticalStrut(25));
        info.add(centeredLabel("Key Features:", Font.BOLD, 13, UIUtils.TEXT_PRIMARY));
        info.add(centeredLabel("• Book Management & Circulation", Font.PLAIN, 13, UIUtils.TEXT_PRIMARY));
        info.add(centeredLabel("• Real-time Dashboard Analytics", Font.PLAIN, 13, UIUtils.TEXT_PRIMARY));
        info.add(centeredLabel("• Overdue Fine Tracking & Reporting", Font.PLAIN, 13, UIUtils.TEXT_PRIMARY));
        info.add(Box.createVerticalStrut(25));

        JSeparator sep = new JSeparator();
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        sep.setMaximumSize(new Dimension(340, 1));
        sep.setForeground(UIUtils.BORDER_COLOR);
        info.add(sep);

        info.add(Box.createVerticalStrut(15));
        info.add(centeredLabel("Built with Java 25 | H2 Database | Swing", Font.PLAIN, 13, UIUtils.TEXT_MUTED));
        info.add(centeredLabel("Fully offline - no server or internet required.", Font.ITALIC, 12, new Color(40, 167, 69))); // Success green

        // Close button
        JButton closeBtn = UIUtils.createButton("Close");
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(UIUtils.CARD_BG);
        btnPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        btnPanel.add(closeBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(info, BorderLayout.CENTER);
        root.add(btnPanel, BorderLayout.SOUTH);
        add(root);
    }

    private JLabel centeredLabel(String text, int style, int size, Color c) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(c);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }
}
