# Backgamón – Taller 4 – Introducción a la IA
## Algoritmo MIN-MAX (profundidad 2) en Java

---

## Estructura del Proyecto

```
Backgamon/
├── src/
│   ├── Player.java           # Enum: WHITE (IA) / BLACK (Humano)
│   ├── Move.java             # Representa un movimiento (from → to)
│   ├── GameState.java        # Estado completo del juego
│   ├── BackgammonRules.java  # Reglas: sucesores, terminal, bear-off
│   ├── Heuristic.java        # Función heurística Hb
│   ├── MinMax.java           # Algoritmo MIN-MAX profundidad 2
│   ├── BoardDisplay.java     # Interfaz visual en consola
│   └── Main.java             # Bucle principal del juego
├── run.sh                    # Script para compilar y ejecutar
└── README.md
```

---

## Compilar y Ejecutar

```bash
# Con Java JDK instalado:
chmod +x run.sh
./run.sh

# O manualmente:
mkdir -p out
javac -d out src/*.java
java -cp out Main
```

Requiere: **Java JDK 11** o superior.

---

## Representación del Estado

```
EstJuego = [ board[24], barWhite, barBlack, offWhite, offBlack, turno, dado ]
```

| Campo | Descripción |
|---|---|
| `board[24]` | Tablero de 24 puntos. `+N` = N fichas blancas, `-N` = N fichas negras |
| `barWhite/barBlack` | Fichas capturadas en la barra esperando reentrar |
| `offWhite/offBlack` | Fichas ya fuera del tablero (bear off) |
| `turn` | Jugador activo (WHITE o BLACK) |
| `die` | Valor del dado lanzado (1–6) |

---

## Dirección de Movimiento

```
Punto: 24  23  22  21  20  19  [BAR]  18  17  ...  2   1
       ←←←←←←←←←←← WHITE (IA) ←←←←←←←←←←←←←←←←←←←←
       →→→→→→→→→→→ BLACK (Humano) →→→→→→→→→→→→→→→→→→→
Punto:  1   2   3   4   5   6  [BAR]   7   8  ...  23  24
```

---

## Función Heurística

```
Hb = 10×Ind1  –  1×Ind2  +  2×Ind3  +  3×Ind4
```

| Indicador | Descripción | Peso |
|---|---|---|
| `Ind1` | Diferencia de fichas en bear off (propias – rival) | w1 = +10 |
| `Ind2` | Diferencia de distancia total al home | w2 = -1 |
| `Ind3` | Puntos bloqueados propios (2+ fichas en un punto) | w3 = +2 |
| `Ind4` | Diferencia de fichas del rival en la barra | w4 = +3 |

---

## Algoritmo MIN-MAX (profundidad 2)

```
          [Estado actual] dado=d
                 │
        ┌────────┴────────┐
       MAX: Blanco (IA) elige la mejor jugada con dado=d
                 │
          [Sucesor elegido]
                 │
        ┌────────┴────────┐
       MIN: Negro (Humano) – se evalúan todos los dados (1-6)
        y se toma el peor resultado para Blanco
                 │
          [Evaluación: Heurística.evaluate()]
```

---

## Reglas implementadas

- ✅ Movimiento normal (avanzar `dado` posiciones)
- ✅ Captura (punto con exactamente 1 ficha rival → va a la barra)
- ✅ Reentrada desde la barra (cuadrante del rival)
- ✅ Bear off (sacar fichas cuando todas están en el home board)
- ✅ Pasar turno si no hay movimientos válidos
- ✅ Detección de fin de juego

---

## Cómo jugar

1. El juego muestra el tablero y el dado lanzado.
2. En el turno **Negro (Humano)**: se muestran los movimientos numerados, ingresa el número.
3. En el turno **Blanco (IA)**: el algoritmo MIN-MAX calcula y juega automáticamente.
4. Gana quien saque las 15 fichas primero.
