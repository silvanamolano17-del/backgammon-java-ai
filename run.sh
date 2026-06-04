#!/bin/bash
# ────────────────────────────────────────────
#  Backgamón – Compilar y Ejecutar (GUI Swing)
# ────────────────────────────────────────────

echo "=== Compilando Backgamón ==="
mkdir -p out
javac -d out src/*.java

if [ $? -eq 0 ]; then
    echo "=== Compilación exitosa. Iniciando interfaz gráfica... ==="
    java -cp out Main
else
    echo "ERROR: Falló la compilación."
fi
