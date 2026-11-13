@echo off
setlocal
cd /d "%~dp0"
set "JAVAC_CMD=javac"
set "JAVAW_CMD=javaw"
if defined JAVA_HOME (
  set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
  set "JAVAW_CMD=%JAVA_HOME%\bin\javaw.exe"
)
if not exist build mkdir build
"%JAVAC_CMD%" --release 11 -d build src\*.java
if errorlevel 1 (
  echo Could not compile. Install a full JDK 11 or newer.
  echo To use the precompiled GUI instead, open open_gui.bat.
  pause
  exit /b 1
)
start "" "%JAVAW_CMD%" -cp build GuiMain %*
if errorlevel 1 (
  echo Could not launch the window. Check JAVA_HOME and the Java installation.
  pause
  exit /b 1
)
exit /b 0
