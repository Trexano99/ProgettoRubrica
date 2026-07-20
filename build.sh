#!/usr/bin/env bash
# Compila i sorgenti e produce Rubrica.jar (runnable). Uso: ./build.sh
set -e
rm -rf build/classes
mkdir -p build/classes
javac -encoding UTF-8 -d build/classes $(find src -name '*.java')

# Manifest con Main-Class e Class-Path verso il connector MySQL: se il file
# lib/mysql-connector-j.jar e' presente accanto al jar, il backend "mysql"
# funziona anche col doppio click. Se manca, i backend su file restano
# comunque utilizzabili (il connector serve solo a chi usa persistence.type=mysql).
mkdir -p build
cat > build/MANIFEST.MF <<'EOF'
Main-Class: rubrica.app.Main
Class-Path: lib/mysql-connector-j.jar
EOF

jar --create --file Rubrica.jar --manifest build/MANIFEST.MF -C build/classes .
echo "Creato Rubrica.jar (Main-Class: rubrica.app.Main)"
