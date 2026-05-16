package com.school.lms;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * About dialog - Comprehensive credits and technology stack.
 */
public class AboutDialog extends JDialog {

    public AboutDialog(Frame parent) {
        super(parent, "About - " + AppConfig.getLibraryName(), true);
        setSize(550, 520); // Increased height for comprehensive credits
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

        JLabel title = new JLabel("About System");
        title.setFont(UIUtils.FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        // Info content Scrollable
        JPanel info = new JPanel();
        info.setBackground(UIUtils.CARD_BG);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(20, 30, 20, 30));

        info.add(centeredLabel(AppConfig.getLibraryName(), Font.BOLD, 18, UIUtils.TEXT_PRIMARY));
        info.add(Box.createVerticalStrut(4));
        info.add(centeredLabel("Library Management System  v" + AppConfig.getVersion(), Font.PLAIN, 13, UIUtils.TEXT_MUTED));
        info.add(Box.createVerticalStrut(15));
        
        info.add(centeredLabel("Developed as a Capstone Project by Jerome Misa", Font.BOLD, 14, UIUtils.TEXT_PRIMARY));
        info.add(centeredLabel("San Sebastian College Recoletos - Canlubang", Font.ITALIC, 13, UIUtils.TEXT_MUTED));
        
        info.add(Box.createVerticalStrut(20));
        info.add(centeredLabel("Technology Stack:", Font.BOLD, 13, UIUtils.TEXT_PRIMARY));
        info.add(centeredLabel("• Language: Java 25 (OpenJDK/Adoptium)", Font.PLAIN, 13, UIUtils.TEXT_PRIMARY));
        info.add(centeredLabel("• Framework: Java Swing (Desktop GUI)", Font.PLAIN, 13, UIUtils.TEXT_PRIMARY));
        info.add(centeredLabel("• Database: H2 Embedded Engine (SQL)", Font.PLAIN, 13, UIUtils.TEXT_PRIMARY));
        info.add(centeredLabel("• Build Tool: Apache Maven", Font.PLAIN, 13, UIUtils.TEXT_PRIMARY));
        info.add(centeredLabel("• Packaging: jpackage tool", Font.PLAIN, 13, UIUtils.TEXT_PRIMARY));
        
        info.add(Box.createVerticalStrut(15));
        info.add(centeredLabel("Tools & Frameworks:", Font.BOLD, 13, UIUtils.TEXT_PRIMARY));
        info.add(centeredLabel("• UI Theme: Custom 'Professional Light' Theme", Font.PLAIN, 13, UIUtils.TEXT_PRIMARY));
        info.add(centeredLabel("• Version Control: Git & GitHub", Font.PLAIN, 13, UIUtils.TEXT_PRIMARY));
        info.add(centeredLabel("• IDE: JetBrains PyCharm with Java Support", Font.PLAIN, 13, UIUtils.TEXT_PRIMARY));

        info.add(Box.createVerticalStrut(15));
        info.add(centeredLabel("References:", Font.BOLD, 13, UIUtils.TEXT_PRIMARY));
        info.add(centeredLabel("• Oracle Java Documentation", Font.PLAIN, 12, UIUtils.TEXT_MUTED));
        info.add(centeredLabel("• H2 Database Engine Documentation", Font.PLAIN, 12, UIUtils.TEXT_MUTED));

        info.add(Box.createVerticalStrut(20));
        JSeparator sep = new JSeparator();
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        sep.setMaximumSize(new Dimension(340, 1));
        sep.setForeground(UIUtils.BORDER_COLOR);
        info.add(sep);

        info.add(Box.createVerticalStrut(10));
        info.add(centeredLabel("Fully offline - no server or internet required.", Font.ITALIC, 12, new Color(40, 167, 69)));

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
