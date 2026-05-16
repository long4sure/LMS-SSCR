@echo off
title San Sebastian Library Management System
echo Starting LMS...

:: Try to find global Java first
java -version >nul 2>&1
if %errorlevel% equ 0 (
    set JAVA_CMD=java
    goto run
)

:: Fallback for local testing (PyCharm bundled JDK)
if exist "C:\Program Files\JetBrains\PyCharm 2025.3.4\jbr\bin\java.exe" (
    set JAVA_CMD="C:\Program Files\JetBrains\PyCharm 2025.3.4\jbr\bin\java.exe"
    goto run
)

:: If neither is found, show error
echo.
echo =========================================
echo  ERROR: Could not start the application.
echo  Make sure Java 21 or newer is installed.
echo  Download from: https://adoptium.net
echo =========================================
pause
exit /b

:run
%JAVA_CMD% -jar LMS.jar
if %errorlevel% neq 0 (
    echo.
    echo =========================================
    echo  ERROR: Application crashed.
    echo =========================================
    pause
)
