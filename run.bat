@echo off
title Library Management System - Developer Runner
echo Starting LMS...

:: Check if Java is available in the system PATH
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo ========================================================
    echo  ERROR: Java not found.
    echo  Please ensure Java 21 or newer is installed and in PATH.
    echo  Download: https://adoptium.net
    echo ========================================================
    pause
    exit /b
)

:: Run the shaded JAR
if exist "LMS.jar" (
    java -jar LMS.jar
) else (
    echo.
    echo ========================================================
    echo  ERROR: LMS.jar not found. 
    echo  Please run 'mvn clean package' inside the lms folder first.
    echo ========================================================
    pause
)
