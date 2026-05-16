# 👨‍💻 Developer Guide for LMS

This guide is for developers who want to maintain, update, or build the Library Management System from its raw source code.

## 🛠️ Prerequisites
To build the application, your development environment must have:
1. **Java 25 (JDK)** - The Java Development Kit is required to compile the code (JRE is not enough).
2. **Apache Maven** - The build automation tool used to manage dependencies and package the JAR.

---

## 🏗️ Building the Application

If you make any changes to the Java source code inside `lms/src/main/java/...`, you need to recompile the project.

1. Open your terminal (Command Prompt / PowerShell).
2. Navigate into the `lms` directory:
   ```bash
   cd lms
   ```
3. Run the Maven clean and package command:
   ```bash
   mvn clean package
   ```
4. Maven will download any required dependencies, compile your code, and generate a new JAR file located at `lms/target/lms-2.0-shaded.jar`.
5. Copy this newly generated JAR back into the main root folder and rename it to `LMS.jar` for testing.

---

## 📦 Creating a Distribution (App Image)

If you want to package the app for users who *do not* have Java installed, you can use the `jpackage` tool to bundle a mini-version of Java directly with the app.

1. Ensure your compiled `LMS.jar` is in the root directory.
2. Open PowerShell in the root directory.
3. Create a temporary `Release` folder and copy your JAR inside it:
   ```powershell
   New-Item -ItemType Directory -Force -Path "Release"
   Copy-Item "LMS.jar" "Release\LMS.jar" -Force
   ```
4. Run the `jpackage` command. Make sure you point it to the exact location of your JDK's `jpackage.exe` file:
   ```powershell
   & "C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot\bin\jpackage.exe" --type app-image --name LMS --input "Release" --main-jar LMS.jar --dest "Output"
   ```
5. This will generate an `Output/LMS` folder. Compress this entire folder into a ZIP file (e.g., `LMS_Portable.zip`).
6. Distribute the ZIP file! Users simply extract it and double-click `LMS.exe`.

---

## 🧹 Cleaning the Workspace

To save space and remove generated files before pushing to GitHub or archiving the code, delete the following:
- `LMS.jar`
- `LMS_Portable.zip`
- The `lms/target/` folder
- The `Release/` and `Output/` folders
