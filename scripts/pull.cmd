@echo off
git pull
taskkill /f /t /im java.exe >> NUL
taskkill /f /t /im git.exe >> NUL
rd /s /q Plazma-API >> NUL
rd /s /q Plazma-Server >> NUL
rd /s /q .gradle >> NUL
call gradlew.bat applyPatches
