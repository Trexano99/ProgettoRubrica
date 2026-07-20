@echo off
REM Compila i sorgenti e produce Rubrica.jar (runnable). Uso: build.bat
setlocal
if exist build\classes rmdir /s /q build\classes
mkdir build\classes
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d build\classes @sources.txt
del sources.txt

REM Manifest con Main-Class e Class-Path verso il connector MySQL: se il file
REM lib\mysql-connector-j.jar e' presente accanto al jar, il backend "mysql"
REM funziona anche col doppio click. Se manca, i backend su file restano usabili.
(
  echo Main-Class: rubrica.app.Main
  echo Class-Path: lib/mysql-connector-j.jar
) > build\MANIFEST.MF

jar --create --file Rubrica.jar --manifest build\MANIFEST.MF -C build\classes .
echo Creato Rubrica.jar (Main-Class: rubrica.app.Main)
endlocal
