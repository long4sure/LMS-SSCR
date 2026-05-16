package com.school.lms;

import java.security.MessageDigest;
import java.sql.*;

/**
 * Handles all database connectivity for the LMS.
 * Uses H2 embedded database — no server installation required.
 * Database file: ./data/library_db.mv.db (created automatically next to the JAR)
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:h2:./data/library_db;AUTO_SERVER=FALSE";
    private static final String DB_USER  = "sa";
    private static final String DB_PASS  = "";

    /** Returns a new JDBC connection to the H2 database. */
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, DB_USER, DB_PASS);
    }

    /**
     * Called once at startup — creates all tables if they don't exist
     * and seeds a default admin account if the users table is empty.
     */
    public static void initializeSchema() {
        try (Connection conn = getConnection();
             Statement s = conn.createStatement()) {

            // ── Books table ───────────────────────────────────────────────
            s.execute("""
                CREATE TABLE IF NOT EXISTS books (
                    id              INT AUTO_INCREMENT PRIMARY KEY,
                    call_no         VARCHAR(100),
                    accession_no    VARCHAR(100),
                    title           VARCHAR(500) NOT NULL,
                    author          VARCHAR(300),
                    total_copies    INT DEFAULT 1,
                    available_copies INT DEFAULT 1
                )
            """);

            // ── Issued books (transactions) ───────────────────────────────
            s.execute("""
                CREATE TABLE IF NOT EXISTS issued_books (
                    id              INT AUTO_INCREMENT PRIMARY KEY,
                    book_id         INT NOT NULL,
                    borrower_name   VARCHAR(200) NOT NULL,
                    borrower_id     VARCHAR(100),
                    issue_date      DATE NOT NULL,
                    due_date        DATE NOT NULL,
                    return_date     DATE,
                    FOREIGN KEY (book_id) REFERENCES books(id)
                )
            """);

            // ── Users / staff accounts ────────────────────────────────────
            s.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id            INT AUTO_INCREMENT PRIMARY KEY,
                    username      VARCHAR(100) UNIQUE NOT NULL,
                    password_hash VARCHAR(64)  NOT NULL,
                    role          VARCHAR(20)  NOT NULL DEFAULT 'staff'
                )
            """);

            // Seed default admin if table is empty
            try (ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)")) {
                        // Admin (Librarian)
                        ps.setString(1, "admin");
                        ps.setString(2, hashPassword("admin123"));
                        ps.setString(3, "admin");
                        ps.executeUpdate();
                        
                        // Staff (School Assistant)
                        ps.setString(1, "staff");
                        ps.setString(2, hashPassword("staff123"));
                        ps.setString(3, "staff");
                        ps.executeUpdate();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns SHA-256 hex digest of the given raw string.
     * Used for storing and verifying passwords without plaintext exposure.
     */
    public static String hashPassword(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(raw.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
