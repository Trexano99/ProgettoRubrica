#!/usr/bin/env bash
# Compila src + test e lancia i test JUnit. Uso: ./run-tests.sh
set -e
JUNIT="lib/junit-platform-console-standalone.jar"
# H2 (database in-memory, modalita' MySQL) serve solo ai test di integrazione
# dei repository JDBC: sta a classpath di test, non entra nel jar dell'app.
H2="lib/h2.jar"
rm -rf build/classes build/test
mkdir -p build/classes build/test

# Windows JDK: separatore classpath = ';'
javac -encoding UTF-8 -d build/classes $(find src -name '*.java')
javac -encoding UTF-8 -cp "build/classes;$JUNIT;$H2" -d build/test $(find test -name '*.java')

java -jar "$JUNIT" execute -cp "build/classes;build/test;$H2" --scan-classpath --details=tree
