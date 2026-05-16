# 📚 Library Management System
> Version 2.0 — Capstone Project Donation Edition

A fully **offline**, standalone library management system built with Java Swing and an H2 embedded database.
No internet connection, no MySQL server, no admin setup required. It just works!

---

## ✨ Key Features
- **Professional Light Theme**: Clean, high-contrast, modern UI designed to prevent eye strain.
- **Dynamic Branding**: The login screen and sidebar dynamically reflect your library's name.
- **Real-time Dashboard**: Track total books, active issues, and view a live feed of the 5 most recent transactions.
- **Book Management & Circulation**: Issue, return, and search books instantly.
- **Overdue Tracking**: Automatically calculates days overdue and fine amounts in the Reports panel.
- **Offline Reliability**: Everything is stored locally on the computer.

---

## 🖥️ System Requirements

| Requirement | Details |
|---|---|
| **OS** | Windows 7 / 10 / 11 |
| **Java** | Version 25 (LTS) or newer |
| **RAM** | 512 MB minimum |
| **Disk** | ~50 MB |

---

## 🚀 How to Run & Distribute

There are two ways you might receive this application:

### Method 1: The App Image (Recommended)
If you downloaded the `LMS_Portable.zip` from GitHub:
1. Right-click the `.zip` file and select **Extract All**.
2. Open the extracted folder.
3. Double-click **`LMS.exe`**. 
> *Note: This method includes a bundled version of Java. You do not need to install Java on your computer to run it!*

### Method 2: The JAR File
If you were given the `LMS.jar` and `run.bat` files:
1. You **MUST** install Java 25 (LTS) from [Adoptium.net](https://adoptium.net).
2. *Important:* During Java installation, click the red X next to "Set JAVA_HOME variable" and change it to "Entire feature will be installed on local hard drive".
3. Once Java is installed, double-click **`run.bat`** to launch the system.

---

## 🔐 Default User Accounts

This system comes with **2 default accounts** built-in:

| Role | Username | Password | Permissions |
|---|---|---|---|
| **Admin** | `admin` | `admin123` | Full Access: Manage Books, Issue, Return, Search, Reports, Settings |
| **Staff** | `staff` | `staff123` | Daily Operations: Issue, Return, Search, Reports |

> ⚠️ **IMPORTANT:** Change these default passwords in the Settings menu immediately upon logging in!

---

## 📂 File Structure

If you run the application using the JAR file, it will automatically generate the following files:
```text
Your folder/
├── LMS.jar                ← The application
├── run.bat                ← Windows launcher
├── lms_config.properties  ← Settings (library name, fine rate)
└── data/
    └── library_db.mv.db   ← All your library data (H2 database)
```

**To back up your data:** Simply copy the `data/` folder and put it on a flash drive.
**To restore data:** Replace the `data/` folder on a new computer with your backup.

---

## 📥 Importing Books from Excel

1. Log in as `admin` and go to **Manage Books**
2. Click **📄 Download Template** to get a sample CSV file
3. Open the CSV in Excel, fill in your books, and **Save As CSV UTF-8**
4. Click **📥 Import CSV** in the app and select your file

---

## 🛠️ Technology Stack & Credits

### Core Technologies
- **Language:** Java 25 (OpenJDK / Adoptium)
- **GUI Framework:** Java Swing
- **Database:** H2 Embedded Database Engine (SQL)
- **Build System:** Apache Maven
- **Distribution Tool:** jpackage

### Frameworks & Tools
- **UI Theme:** Custom "Professional Light" Theme (Vanilla CSS/Swing Graphics)
- **Version Control:** Git & GitHub
- **IDE Support:** JetBrains PyCharm with Java Environment
- **Packaging:** Native Windows App Bundling (App Image)

### References & Documentation
- **Oracle Java SE Documentation** (Language specifications)
- **H2 Database Engine User Manual** (SQL Syntax and Embedded Logic)
- **FlatLaf Inspiration** (For clean typography and UI layout best practices)

---

## 👨‍💻 Credits

Developed as a Capstone Project by **Jerome Misa**  
**San Sebastian College Recoletos - Canlubang**  
*Built with: Java 25 · H2 Database · Java Swing*
