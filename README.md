# 📚 San Sebastian College Recoletos - Canlubang Library System
> Version 2.0 — Capstone Project Donation Edition

A fully **offline**, standalone library management system built with Java Swing and H2 embedded database.
No internet connection, no MySQL server, no admin setup required — just Java + the JAR file.

---

## 🖥️ System Requirements & Installation Guide

To prevent errors like **"'java' is not recognized as an internal or external command"**, the computer running this system **MUST** have Java installed and properly added to the Windows PATH.

| Requirement | Details |
|---|---|
| **Java** | Version 25 (LTS) or newer (JRE is enough) |
| **OS** | Windows 7 / 10 / 11 |
| **RAM** | 512 MB minimum |
| **Disk** | ~50 MB (JAR + database) |

### 🛠️ How to Install Java properly:
1. Go to **[https://adoptium.net](https://adoptium.net)** and download the latest **Java 25 (LTS)** for Windows.
2. Run the `.msi` installer you downloaded.
3. **CRITICAL STEP:** During the installation wizard, when you see "Custom Setup", make sure to click the red X next to **"Set JAVA_HOME variable"** and select **"Entire feature will be installed on local hard drive"**.
4. Finish the installation. The `run.bat` file will now work flawlessly.

---

## 🚀 How to Run

1. Copy `LMS.jar` and `run.bat` to the same folder (e.g., `C:\Library\`)
2. Double-click `run.bat` — that's it!

> **Or** right-click `LMS.jar` → "Open with" → Java Platform

---

## 🔐 First-Time Setup

On the very first launch, a **Setup Wizard** will appear:

1. Enter your **Library Name** (e.g., "San Sebastian College Recoletos - Canlubang Library")
2. Set an **Admin Password** (min. 6 characters — change the default!)
3. Set a **Fine Rate** (₱ per overdue day, default ₱5.00)
4. Click **Finish Setup**

---

## 👥 Default User Accounts & Roles

This system comes with **2 default accounts** built-in:

| Role / Username | Default Password | Can Do |
|---|---|---|
| **Librarian** (`admin`) | `admin123` | Everything — Manage Books, Issue, Return, Search, Reports, Settings |
| **Assistant** (`staff`) | `staff123` | Issue Book, Return Book, Search Books, Reports. (Cannot manage books or access settings) |

> ⚠️ **IMPORTANT:** Change these default passwords immediately upon logging in!

---

## 📂 File Structure After First Run

```
Your folder/
├── LMS.jar            ← The application
├── run.bat            ← Windows launcher
├── lms_config.properties  ← Settings (library name, fine rate)
└── data/
    └── library_db.mv.db   ← All your library data (H2 database)
```

**To back up your data:** Copy the `data/` folder anywhere safe.
**To restore:** Replace the `data/` folder with your backup.

---

## 📥 Importing Books from Excel

1. In the app, log in as `admin` and go to **Manage Books**
2. Click **📄 Download Template** — saves a sample CSV file
3. Open the CSV in Excel and fill in your books
4. Save as **CSV** from Excel (File → Save As → CSV UTF-8)
5. Back in the app, click **📥 Import CSV** and select your file

**Expected CSV columns (first row is the header, will be skipped):**

| Column | Example |
|---|---|
| Call No. | `REF-001` |
| Accession No. | `ACC-001` |
| Title | `Introduction to Computing` |
| Author/Editor | `John Doe` |
| Total Copies | `3` |
| Available Copies | `3` |

---

## ⚙️ Changing Settings

Go to **Dashboard → Settings** (Admin only):
- Change library name
- Change fine rate per overdue day
- Change your password

---

## 📊 Reports & Export

- **All Transactions** — full borrowing history
- **Overdue Books** — currently unreturned + past due date with days overdue and fine amount
- Both tabs have an **Export CSV** button — opens a save dialog to download the data as CSV

---

## 🔒 Security Notes

- Passwords are stored as **SHA-256 hashes** (not plaintext)
- All SQL queries use **PreparedStatements** (safe from SQL injection)
- The H2 database is a local file — only accessible from this machine

---

## 🛠️ Developer Notes (For IT Staff)

The database is H2 (`./data/library_db.mv.db`). If you need to inspect or edit data directly:
1. Download H2 Console from https://h2database.com
2. JDBC URL: `jdbc:h2:./data/library_db`
3. User: `sa`, Password: *(leave blank)*

---

## 👨‍💻 Credits

Developed as a Capstone Project by **Jerome Misa**  
**San Sebastian College Recoletos - Canlubang**  
Built with: Java 21 · H2 Database · Java Swing
