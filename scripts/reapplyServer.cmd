@echo off
taskkill /f /t /im java.exe >> NUL
taskkill /f /t /im git.exe >> NUL
rd /s /q Andromeda-Server >> NUL
call gradlew.bat applyServerPatches
