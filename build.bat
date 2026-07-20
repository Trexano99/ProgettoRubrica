@echo off
REM Compila i sorgenti e produce Rubrica.jar (runnable). Uso: build.bat
setlocal
if exist build\classes rmdir /s /q build\classes
mkdir build\classes
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d build\classes @sources.txt
del sources.txt
jar --create --file Rubrica.jar --main-class rubrica.app.Main -C build\classes .
echo Creato Rubrica.jar (Main-Class: rubrica.app.Main)
endlocal
