# TP1 - BufferGap e HistorialEdicion

**Grupo:** `<GRUPO>`  
**Integrantes:** Angel Zacarias Portillo Sales (`CIC: <CIC_1>`, `Seccion: <SECCION_1>`), Alex Giovanni Llamosas Maidana (`CIC: <CIC_2>`, `Seccion: <SECCION_2>`) y `<APELLIDO_3>, <NOMBRE_3>` (`CIC: <CIC_3>`, `Seccion: <SECCION_3>`).

`BufferGap<E>` representa una secuencia generica mediante un arreglo dividido en elementos anteriores al cursor, un hueco y elementos posteriores. El cursor es siempre `inicioHueco`; no existe un campo separado. Al crecer, la capacidad se duplica y el hueco permanece en la posicion logica actual del cursor.

Para traducir un indice logico, si es menor que `inicioHueco` se usa directamente; en caso contrario se suma el tamano del hueco (`finHueco - inicioHueco`). Un desplazamiento se cuenta solamente cuando un elemento existente cambia de celda al mover el cursor o se copia durante el crecimiento.

`BufferVacioException` es checked porque borrar al inicio puede ocurrir durante el uso normal y puede manejarse. `PosicionInvalidaException` y `PilaVaciaException` son unchecked porque indican uso invalido de los TAD.

Los comandos guardan el estado minimo: `ComandoInsertar` conserva el caracter, `ComandoBorrar` el caracter eliminado y `ComandoMoverCursor` el delta. `HistorialEdicion` usa dos `PilaES<Comando>` propias. Una accion nueva descarta rehacer porque crea una rama de historia distinta.

Compilacion y ejecucion con JDK 21:

```text
probar.bat
```

El script compila los `.class` dentro de `build`, ejecuta ambas pruebas y
elimina los archivos compilados al finalizar, incluso cuando ocurre un error.

## Tabla de desplazamientos

| N | BufferGap | Arreglo simple |
|---:|---:|---:|
| 100000 | 0 | 500000000 |
| 200000 | 0 | 1000000000 |
| 300000 | 0 | 1500000000 |
| 400000 | 0 | 2000000000 |
| 500000 | 0 | 2500000000 |
| 600000 | 0 | 3000000000 |
| 700000 | 0 | 3500000000 |
| 800000 | 0 | 4000000000 |
| 900000 | 0 | 4500000000 |
| 1000000 | 0 | 5000000000 |

Valores obtenidos al ejecutar `TestBufferGap` con JDK 21.0.11.
