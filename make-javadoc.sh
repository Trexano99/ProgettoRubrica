#!/usr/bin/env bash
# Genera la documentazione Javadoc HTML in build/javadoc. Uso: ./make-javadoc.sh
set -e
rm -rf build/javadoc
mkdir -p build/javadoc
javadoc -encoding UTF-8 -charset UTF-8 -d build/javadoc -sourcepath src -subpackages rubrica -quiet
echo "Javadoc generata in build/javadoc/index.html"
