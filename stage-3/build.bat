@echo off
setlocal
cd /d "%~dp0"
set "JAVAC_CMD=javac"
set "JAR_CMD=jar"
if defined JAVA_HOME (
  set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
  set "JAR_CMD=%JAVA_HOME%\bin\jar.exe"
)
if not exist build mkdir build
if exist build\*.class del /q build\*.class
"%JAVAC_CMD%" --release 11 -d build src\*.java
if errorlevel 1 exit /b 1
"%JAR_CMD%" --create --file RestaurantRush_GUI.jar --main-class GuiMain -C build .
if errorlevel 1 exit /b 1
"%JAR_CMD%" --create --file RestaurantRush_CLI.jar --main-class Main -C build .
exit /b %errorlevel%
