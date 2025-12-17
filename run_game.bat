@echo off
cd /d "%~dp0"
java -cp "dist\SpaceShipArena_Final.jar;dist\lib\*" launcher.Launcher
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Hai bisogno di java 21 installato sulla macchina
    pause
)
