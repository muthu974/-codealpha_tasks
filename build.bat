@echo off
setlocal
echo ============================================
echo   Building Student Grade Tracker
echo ============================================

if not exist bin mkdir bin

echo Compiling Java source files...
javac -d bin src\com\gradetracker\model\*.java src\com\gradetracker\service\*.java src\com\gradetracker\ui\*.java src\com\gradetracker\*.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)

echo Packaging executable JAR...
set JAR_BIN=jar
where jar >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    if exist "C:\Program Files\Java\jdk-24\bin\jar.exe" (
        set JAR_BIN="C:\Program Files\Java\jdk-24\bin\jar.exe"
    )
)

%JAR_BIN% --create --file StudentGradeTracker.jar --main-class com.gradetracker.Main -C bin .
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] StudentGradeTracker.jar created successfully!
) else (
    echo [WARNING] Could not package JAR. Class files are ready in 'bin' folder.
)

echo Done.
pause
