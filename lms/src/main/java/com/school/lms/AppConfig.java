package com.school.lms;

import java.io.*;
import java.util.Properties;

/**
 * Manages persistent app configuration stored in lms_config.properties
 * next to the running JAR / in the working directory.
 */
public class AppConfig {

    private static final String CONFIG_FILE = "lms_config.properties";
    private static final Properties props = new Properties();

    static { load(); }

    // ── Load / Save ──────────────────────────────────────────────────────────

    public static void load() {
        File f = new File(CONFIG_FILE);
        if (f.exists()) {
            try (FileInputStream in = new FileInputStream(f)) {
                props.load(in);
            } catch (IOException ignored) {}
        }
    }

    public static void save() {
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "San Sebastian LMS Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Library Info ─────────────────────────────────────────────────────────

    public static String getLibraryName() {
        return props.getProperty("library.name", "San Sebastian College Recoletos - Canlubang Library");
    }

    public static void setLibraryName(String name) {
        props.setProperty("library.name", name.trim());
        save();
    }

    // ── Fine System ──────────────────────────────────────────────────────────

    /** Returns fine rate per overdue day in PHP (default ₱5.00) */
    public static double getFineRatePerDay() {
        try {
            return Double.parseDouble(props.getProperty("fine.rate", "5.0"));
        } catch (NumberFormatException e) {
            return 5.0;
        }
    }

    public static void setFineRatePerDay(double rate) {
        props.setProperty("fine.rate", String.format("%.2f", rate));
        save();
    }

    // ── Setup Flag ───────────────────────────────────────────────────────────

    public static boolean isSetupComplete() {
        return "true".equals(props.getProperty("setup.complete", "false"));
    }

    public static void markSetupComplete() {
        props.setProperty("setup.complete", "true");
        save();
    }

    // ── App Version ──────────────────────────────────────────────────────────

    public static String getVersion() {
        return "2.0";
    }
}
