@echo off
ECHO Automatically-generated DDOWiki downloader script.
REM Invokes ./wget.exe
REM Saves output to ./out/*

REM Set working directory to directory in which the .bat file was double-clicked.
CD %~dp0

ECHO Preparing to download from https://ddowiki.com
ECHO Estimated time to download: 19s
TIMEOUT 10

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P out -E --no-check-certificate https://ddowiki.com/page/I:Celestial_Emerald_Ring
ECHO 13 of 16 downloaded. (81%%)



ECHO Downloads complete!
PAUSE
EXIT