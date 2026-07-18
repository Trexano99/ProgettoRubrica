#!/usr/bin/env bash
# Compila src + test e lancia i test JUnit. Uso: ./run-tests.sh
set -e
JUNIT="lib/junit-platform-console-standalone.jar"
rm -rf build/classes build/test
mkdir -p build/classes build/test

# Windows JDK: separatore classpath = ';'
javac -encoding UTF-8 -d build/classes $(find src -name '*.java')
javac -encoding UTF-8 -cp "build/classes;$JUNIT" -d build/test $(find test -name '*.java')

java -jar "$JUNIT" execute -cp "build/classes;build/test" --scan-classpath --details=tree
