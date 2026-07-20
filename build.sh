#!/usr/bin/env bash
# Compila i sorgenti e produce Rubrica.jar (runnable). Uso: ./build.sh
set -e
rm -rf build/classes
mkdir -p build/classes
javac -encoding UTF-8 -d build/classes $(find src -name '*.java')
jar --create --file Rubrica.jar --main-class rubrica.app.Main -C build/classes .
echo "Creato Rubrica.jar (Main-Class: rubrica.app.Main)"
