@echo off
setlocal
cd /d "%~dp0"
set "JAVA_CMD=java"
set "JAVAC_CMD=javac"
if defined JAVA_HOME (
  set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
  set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
)
if not exist build mkdir build
"%JAVAC_CMD%" --release 11 -d build src\*.java
if errorlevel 1 exit /b 1
"%JAVA_CMD%" -cp build Main %*
exit /b %errorlevel%
