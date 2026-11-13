@echo off
setlocal
cd /d "%~dp0"
title Restaurant Rush - CLI
echo Restaurant Rush - CLI
echo.
echo 1. Stage 1
echo 2. Stage 2
echo 3. Stage 3
echo Q. Quit
echo.
choice /c 123Q /n /m "Choose a stage [1/2/3] or Q to quit: "
if errorlevel 4 exit /b 0
if errorlevel 3 (
  set "STAGE=3"
) else if errorlevel 2 (
  set "STAGE=2"
) else if errorlevel 1 (
  set "STAGE=1"
) else (
  exit /b 1
)
set "JAVA_CMD=java"
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
set "GAME_JAR=%~dp0stage-%STAGE%\RestaurantRush_CLI.jar"
if not exist "%GAME_JAR%" (
  echo.
  echo Cannot find "%GAME_JAR%".
  echo Put this launcher beside the stage-1, stage-2 and stage-3 folders.
  echo Extract the whole game ZIP before running it.
  pause
  exit /b 1
)
echo.
echo Starting Stage %STAGE% in CLI mode...
"%JAVA_CMD%" -jar "%GAME_JAR%" %*
if errorlevel 1 (
  echo.
  echo The game could not start or stopped with an error. See the message above.
  echo Check that Java 11 or newer is installed and available on PATH.
  echo If JAVA_HOME is set, check that it points to the correct Java installation.
  pause
  exit /b 1
)
echo.
pause
exit /b 0
