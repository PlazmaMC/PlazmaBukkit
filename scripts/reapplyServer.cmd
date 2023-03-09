@echo off
taskkill /f /t /im java.exe >> NUL
taskkill /f /t /im git.exe >> NUL
rd /s /q Plazma-Server >> NUL
call gradlew.bat applyServerPatches
