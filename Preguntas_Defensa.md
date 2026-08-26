# Preguntas para la defensa oral

## Como usar este documento

La defensa es individual. Cualquier integrante puede ser interrogado sobre
cualquier archivo entregado. Saber solamente que hace el codigo no alcanza:
hay que explicar por que fue diseñado asi.

Practica la primera parte sin abrir el codigo. Para obtener el puntaje completo,
la rubrica exige responder con fluidez y sin leer.

Orden recomendado:

1. Dominar todas las preguntas de **Parte I: obligatorias**.
2. Resolver las trazas escribiendo cada estado en papel.
3. Practicar las modificaciones propuestas en voz alta.
4. Estudiar las preguntas extra para demostrar dominio integral.

# Parte I: preguntas que debes saber si o si

Estas preguntas surgen directamente de la rubrica de defensa y concentran los
25 puntos.

## Bloque 1: invariante y traduccion logico-fisica (7 puntos)

### 1. ¿Cual es el invariante completo de BufferGap?

El arreglo esta siempre dividido en tres zonas contiguas:

```text
[ elementos antes del cursor ][ HUECO ][ elementos despues del cursor ]
0                            inicio   fin                         capacidad
```

- `datos[0 .. inicioHueco-1]` contiene la parte izquierda.
- `datos[inicioHueco .. finHueco-1]` es el hueco.
- `datos[finHueco .. capacidad-1]` contiene la parte derecha.
- El contenido fisico dentro del hueco no tiene significado.
- El cursor no es otro campo: siempre es `inicioHueco`.

### 2. ¿Cuales son las identidades que siempre deben cumplirse?

```text
tamanoHueco = finHueco - inicioHueco
size()      = capacidad() - tamanoHueco
cursor      = inicioHueco
```

Estas identidades deben ser validas despues de cualquier operacion.

### 3. ¿Por que el cursor no se guarda en un campo separado?

Porque `inicioHueco` ya expresa su posicion logica. Un segundo campo seria otra
fuente de verdad y ambos valores podrian desincronizarse.

### 4. ¿Que significa que el contenido del hueco no tenga valor?

Puede contener `null` o referencias antiguas. Esas celdas no forman parte de la
secuencia logica. La validez se determina por los limites del hueco, no por el
contenido de las celdas.

Por eso `borrar()` no necesita escribir `null`.

### 5. ¿Como se traduce un indice logico a uno fisico?

```text
si index < inicioHueco:
    fisico = index
si no:
    fisico = index + (finHueco - inicioHueco)
```

Antes del hueco, los indices coinciden. Desde el cursor en adelante hay que
saltar el tamaño del hueco.

### 6. Demuestra la traduccion con HoX|la.

En ese estado:

```text
inicioHueco = 3
finHueco = 14
tamanoHueco = 11
```

La traduccion es:

| Indice logico | Calculo | Indice fisico | Valor |
|---:|---:|---:|---|
| 0 | Antes del hueco | 0 | `H` |
| 1 | Antes del hueco | 1 | `o` |
| 2 | Antes del hueco | 2 | `X` |
| 3 | `3 + 11` | 14 | `l` |
| 4 | `4 + 11` | 15 | `a` |

Por eso `get(4)` retorna `a`.

### 7. ¿Que limites son validos para get/set y para moverCursor?

Para `get` y `set`:

```text
0 <= index < size()
```

Para la posicion final del cursor:

```text
0 <= inicioHueco + delta <= size()
```

El cursor puede estar despues del ultimo elemento, pero ese lugar no es un
indice valido para leer o reemplazar.

## Bloque 2: operaciones y crecimiento (5 puntos)

### 8. ¿Como funciona insertar y por que no desplaza elementos?

Si hay hueco, ejecuta conceptualmente:

```java
datos[inicioHueco] = obj;
inicioHueco++;
```

La celda ya estaba libre. El elemento nuevo ocupa esa celda y el hueco se
reduce por la izquierda. La parte derecha permanece donde estaba.

### 9. ¿Por que insertar normalmente no aumenta desplazamientos?

El objeto insertado es nuevo: no existia en otra celda del buffer. Ningun
elemento existente cambia de posicion fisica.

### 10. ¿Como funciona moverCursor hacia la izquierda?

Cuando existe hueco, por cada posicion:

1. Copia el ultimo elemento anterior al hueco a la ultima celda del hueco.
2. Reduce `inicioHueco`.
3. Reduce `finHueco`.
4. Suma un desplazamiento.

El texto conserva su orden logico; solamente se muda el hueco. Si el hueco
tiene tamaño cero, origen y destino serian la misma celda: solo cambian ambos
limites y no se cuenta una autoasignacion como desplazamiento fisico.

### 11. ¿Como funciona moverCursor hacia la derecha?

Cuando existe hueco, por cada posicion:

1. Copia el primer elemento posterior al hueco al comienzo del hueco.
2. Aumenta `inicioHueco`.
3. Aumenta `finHueco`.
4. Suma un desplazamiento.

Con hueco de tamaño cero se aplica la misma excepcion: cambian los limites sin
copiar ni contar una celda sobre si misma.

### 12. ¿Por que mover el cursor no cambia size()?

`inicioHueco` y `finHueco` avanzan o retroceden juntos. Por eso la diferencia
`finHueco - inicioHueco` no cambia y el tamaño logico tampoco.

### 13. ¿Como funciona get y por que no recorre el arreglo?

Valida el indice, calcula su indice fisico mediante una comparacion y una suma,
y retorna esa celda. No necesita visitar elementos anteriores.

### 14. ¿Cuando crece el arreglo?

Crece cuando:

```text
inicioHueco == finHueco
```

Esa igualdad significa que el tamaño del hueco es cero.

### 15. ¿Como se realiza el crecimiento?

1. Crea manualmente un arreglo del doble de capacidad.
2. Copia la parte izquierda al comienzo.
3. Copia la parte derecha al final.
4. Deja el espacio nuevo entre ambas partes.
5. Mantiene `inicioHueco`.
6. Calcula el nuevo `finHueco`.
7. Cuenta cada elemento copiado como desplazamiento.

No usa `Arrays.copyOf` ni `System.arraycopy`.

### 16. ¿Donde queda el hueco al duplicar y por que?

Queda en la posicion logica actual del cursor. La izquierda continua al
comienzo y la derecha se copia al final del nuevo arreglo.

Esta decision conserva simultaneamente:

- El contenido logico.
- La posicion logica del cursor.
- Un hueco disponible justo donde probablemente continuara la escritura.

Si el hueco se colocara al final, `inicioHueco` pasaria a representar el final
del texto y el cursor cambiaria de posicion.

### 17. Ejemplo de crecimiento que debes poder dibujar.

Supongamos capacidad 16, arreglo lleno, cursor en 6:

```text
Antes:
[0..5 izquierda][6..15 derecha]
inicioHueco = 6
finHueco = 6
```

Hay 10 elementos a la derecha. Al crecer a 32:

```text
izquierda: indices 0..5
hueco:     indices 6..21
derecha:   indices 22..31
```

Por lo tanto:

```text
inicioHueco = 6
finHueco = 22
size = 32 - (22 - 6) = 16
desplazamientos agregados = 16
```

## Bloque 3: traza en vivo (5 puntos)

El docente puede proponer operaciones diferentes a las del enunciado. En cada
paso debes escribir:

```text
contenido logico | inicioHueco | finHueco | desplazamientos
```

### 18. Reproduce la traza obligatoria del Ejercicio 1.

| Operacion | Contenido | inicio | fin | Desplazamientos acumulados |
|---|---|---:|---:|---:|
| Inicial | `|` | 0 | 16 | 0 |
| insertar `H` | `H|` | 1 | 16 | 0 |
| insertar `o` | `Ho|` | 2 | 16 | 0 |
| insertar `l` | `Hol|` | 3 | 16 | 0 |
| insertar `a` | `Hola|` | 4 | 16 | 0 |
| mover -2 | `Ho|la` | 2 | 14 | 2 |
| insertar `X` | `HoX|la` | 3 | 14 | 2 |
| get(4) | `HoX|la` | 3 | 14 | 2 |
| borrar | `Ho|la` | 2 | 14 | 2 |

### 19. ¿Por que finHueco no cambia al insertar ni al borrar?

Insertar consume hueco desde la izquierda y solo aumenta `inicioHueco`.
Borrar libera una celda hacia la izquierda y solo reduce `inicioHueco`.
`finHueco` cambia al mover el hueco o al crecer.

### 20. Traza de practica distinta a la obligatoria.

Partiendo de un buffer nuevo:

| Operacion | Contenido | inicio | fin | Desplazamientos |
|---|---|---:|---:|---:|
| Inicial | `|` | 0 | 16 | 0 |
| insertar `A` | `A|` | 1 | 16 | 0 |
| insertar `B` | `AB|` | 2 | 16 | 0 |
| insertar `C` | `ABC|` | 3 | 16 | 0 |
| mover -2 | `A|BC` | 1 | 14 | 2 |
| insertar `X` | `AX|BC` | 2 | 14 | 2 |
| mover +1 | `AXB|C` | 3 | 15 | 3 |
| borrar | `AX|C` | 2 | 15 | 3 |

### 21. Metodo para resolver cualquier traza en el pizarron.

1. Dibuja izquierda, hueco y derecha.
2. Anota `inicioHueco` y `finHueco` antes de operar.
3. Aplica una sola operacion.
4. Actualiza primero los limites.
5. Comprueba `size = capacidad - (fin - inicio)`.
6. Comprueba que el contenido logico conserva el orden esperado.
7. Cuenta solo elementos existentes que cambiaron de celda.

No intentes resolver varios pasos mentalmente de una vez.

### 22. Si moverCursor recibe -5, ¿cuantos desplazamientos produce?

Si existe un hueco real y el movimiento es valido, produce 5: cada posicion
traslada un elemento. Si el hueco tiene tamaño cero, produce 0 porque solo
cambian los limites y no hay cambio de celda fisica. Si el destino queda fuera
de `[0, size()]`, no mueve nada y lanza `PosicionInvalidaException`, porque
valida antes de modificar.

### 23. ¿Como obtienes finHueco desde la API sin un getter?

```text
tamanoHueco = capacidad() - size()
finHueco = posicionCursor() + tamanoHueco
```

No se agrega un getter porque el limite es un detalle interno.

## Bloque 4: deshacer y rehacer (3 puntos)

### 24. ¿Que es el patron Comando en este trabajo?

Cada accion se representa mediante un objeto que sabe ejecutarse, deshacerse y
describirse. `HistorialEdicion` trabaja con la interfaz `Comando`, sin preguntar
si la accion concreta es insertar, borrar o mover.

### 25. ¿Que guarda ComandoInsertar y como se deshace?

Guarda el buffer y el caracter insertado. Se ejecuta insertando el caracter y
se deshace mediante Backspace, es decir, llamando a `borrar()`.

### 26. ¿Que guarda ComandoBorrar y por que?

Guarda el buffer y el caracter efectivamente borrado. Debe almacenarlo porque,
una vez eliminado, ese dato ya no puede recuperarse del contenido logico.
Deshacer consiste en reinsertarlo.

### 27. ¿Que guarda ComandoMoverCursor y como se deshace?

Guarda el buffer y solamente `delta`. Ejecuta `moverCursor(delta)` y deshace
con `moverCursor(-delta)`.

### 28. ¿Por que el estado guardado por cada comando es minimo?

- Insertar ya conoce el caracter de la accion.
- Borrar agrega solo el dato que se perderia.
- Mover guarda solo el desplazamiento necesario para calcular el inverso.

No guardan copias completas del buffer ni posiciones innecesarias.

### 29. ¿Como funciona HistorialEdicion?

Usa dos `PilaES<Comando>`:

- `deshacer`: comandos ejecutados que pueden revertirse.
- `rehacer`: comandos deshechos que pueden volver a ejecutarse.

Deshacer mueve el ultimo comando de la primera pila a la segunda. Rehacer hace
el recorrido inverso.

### 30. ¿Por que ejecutar un comando nuevo descarta rehacer?

Porque despues de deshacer y ejecutar algo diferente, la historia se bifurca.
Los comandos de rehacer pertenecen a un futuro que ya no corresponde al estado
actual.

### 31. ¿Por que rehacer no llama a HistorialEdicion.ejecutar?

Porque ese metodo vacia toda la pila de rehacer. Rehacer debe ejecutar solo el
comando recuperado y conservar los demas comandos todavia disponibles.

### 32. ¿Que demuestran los pasos 7 y 10 de TestHistorial?

- Paso 7: un comando nuevo elimino correctamente la pila de rehacer.
- Paso 10: `ComandoBorrar` guardo el caracter borrado y pudo restaurarlo.

## Bloque 5: modificaciones propuestas en el momento (3 puntos)

El docente no espera necesariamente codigo completo. Debes identificar que
clases, campos, metodos, invariantes y pruebas cambiarian.

### 33. ¿Como agregarias Delete, que borra despues del cursor?

En `BufferGap` agregaria un metodo que:

1. Valide que `posicionCursor() < size()`.
2. Lea y retorne `datos[finHueco]`.
3. Aumente `finHueco` para incorporar esa celda al hueco.
4. No incremente desplazamientos, porque no mueve otros elementos.

Tambien agregaria `ComandoBorrarAdelante`, que guarde el caracter eliminado.
Para deshacerlo debe restaurar el caracter sin dejar el cursor adelantado; una
opcion con la API actual es insertar el caracter y mover el cursor una posicion
a la izquierda. Finalmente agregaria pruebas de cursor al medio y al final.

### 34. ¿Como agregarias moverCursorA(int posicion)?

Validaria `0 <= posicion <= size()` y reutilizaria:

```text
moverCursor(posicion - posicionCursor())
```

No duplicaria la logica de traslado.

### 35. ¿Como agregarias insertar una cadena?

Recorreria sus caracteres en orden y llamaria a `insertar` por cada uno. Para
un comando reversible guardaria la cadena o su longitud; deshacer borraria la
misma cantidad de caracteres en orden inverso. Agregaria pruebas con cadena
vacia, cadena normal y crecimiento durante la insercion.

### 36. ¿Como agregarias reemplazar un caracter como comando?

El comando guardaria:

- Buffer.
- Indice logico.
- Valor nuevo.
- Valor anterior obtenido al ejecutar `set`.

Deshacer llamaria a `set(valorAnterior, index)`. Rehacer volveria a guardar y
colocar el valor nuevo.

### 37. ¿Como permitirias elegir la capacidad inicial?

Agregaria un constructor que reciba capacidad, valide que sea positiva, cree el
arreglo de ese tamaño y establezca `inicioHueco=0` y `finHueco=capacidad`.
Mantendria el constructor actual delegando con valor 16.

### 38. ¿Como agregarias limpiar el historial?

Agregaria un metodo que desapile ambas pilas mientras no esten vacias. No usaria
colecciones del API. Probaria que ambos tamaños terminen en cero y que deshacer
y rehacer retornen `false`.

### 39. ¿Que cambiarias si ComandoBorrar debiera admitir Character null?

El campo `caracterBorrado == null` ya no serviria para saber si fue ejecutado.
Agregaria un booleano `ejecutado`, que se pondria en `true` despues de borrar.
Asi `null` podria ser un dato valido.

### 40. ¿Como preparas una respuesta ante cualquier modificacion?

Responde siempre en este orden:

1. Que comportamiento nuevo se pide.
2. Que clase es responsable.
3. Que estado adicional, si alguno, hace falta.
4. Como se conserva el invariante.
5. Que excepciones o limites aparecen.
6. Que pruebas agregarias.

## Bloque 6: dominio de cualquier parte entregada (2 puntos)

### 41. ¿Como esta implementada PilaES?

Como una lista simplemente enlazada generica. Mantiene un `Nodo tope` y una
cantidad. Apilar crea un nodo al frente; desapilar retira el frente. Por eso
cumple LIFO.

### 42. ¿Por que Nodo es una inner class privada?

Porque usa el tipo generico de la pila y es un detalle de implementacion que el
usuario no debe ver ni modificar.

### 43. ¿Que excepciones propias existen?

- `BufferVacioException`: checked; borrar con cursor en cero.
- `PosicionInvalidaException`: unchecked; indice o movimiento invalido.
- `PilaVaciaException`: unchecked; `tope` o `desapilar` en una pila vacia.

`IllegalStateException` pertenece a Java y se usa cuando un comando encuentra
un estado incompatible con una historia correcta.

### 44. ¿Como funciona el iterador?

Mantiene un indice logico y obtiene cada elemento mediante `get`. De ese modo
salta el hueco. `BufferGap` implementa `Iterable<E>`, requisito para `for-each`.

### 45. ¿Que comprueba TestBufferGap?

La traza, limites, excepciones, crecimiento, `set`, iteracion de 100.000
caracteres y la tabla comparativa de desplazamientos.

### 46. ¿Que comprueba TestHistorial?

La traza obligatoria de 12 pasos, contenido del buffer, tamaños de ambas pilas,
resultados booleanos y funcionamiento LIFO de `PilaES`.

### 47. ¿Como se ejecuta el proyecto?

```text
probar.bat
```

Compila en `build`, ejecuta ambos tests y elimina los `.class` al finalizar.

# Parte II: preguntas extra que conviene manejar

Estas preguntas no aparecen como items independientes de la rubrica de defensa,
pero ayudan a demostrar comprension real y a responder repreguntas.

## Extras sobre BufferGap

### 48. ¿Por que se usa un arreglo generico con cast?

Java no permite `new E[]`. Se crea `(E[]) new Object[...]` y se limita
`@SuppressWarnings("unchecked")` al constructor y al crecimiento.

### 49. ¿Por que desplazamientos es long?

Porque la tabla alcanza valores superiores al maximo de `int`. Por ejemplo, el
arreglo simple llega a 5.000.000.000 desplazamientos.

### 50. ¿Por que la validacion del movimiento calcula una posicion long?

Para evitar que una suma extrema entre `inicioHueco` y `delta` desborde un
`int` y aparente ser una posicion valida.

### 51. ¿Que operaciones no cuentan desplazamientos?

Insertar normalmente, borrar, `get`, `set`, `toString`, iterar y las consultas.
Solo cuentan traslados de elementos existentes al mover o crecer.

### 52. ¿Como funciona toString?

Recorre indices logicos con `get` e intercala `|` en la posicion del cursor. Si
el cursor esta al final, agrega la barra despues del ciclo. La barra no esta
almacenada.

### 53. ¿Que ocurre con moverCursor(0)?

Valida la posicion actual, no entra en ningun ciclo, no cambia el estado y no
cuenta desplazamientos.

### 54. ¿Que revela la tabla comparativa?

Con el hueco ya ubicado, BufferGap inserta sin mover la derecha. El arreglo
simple desplaza los elementos posteriores por cada insercion. Por eso la
columna del BufferGap queda en cero y la otra crece con `n`.

### 55. ¿Por que se cuentan movimientos y no milisegundos?

Los movimientos dependen del algoritmo y son reproducibles. El tiempo depende
de la computadora, JVM y carga del sistema.

### 56. ¿Que es estado logico y que es estado fisico?

El estado logico es la secuencia visible sin hueco. El fisico incluye capacidad,
limites y celdas sin significado. Distintos estados fisicos pueden representar
la misma secuencia logica.

## Extras sobre excepciones y comandos

### 57. ¿Por que BufferVacioException es checked?

Backspace al comienzo puede ocurrir durante el uso normal y el llamador puede
manejarlo razonablemente.

### 58. ¿Por que PosicionInvalidaException es unchecked?

Pedir una posicion inexistente normalmente es un error de programacion, no una
situacion esperable que deba recuperarse.

### 59. ¿Por que los comandos convierten BufferVacioException?

La interfaz no permite declarar `throws`. Si un comando correctamente
registrado no puede deshacerse, la historia esta inconsistente; por eso se
convierte en `IllegalStateException` conservando la causa.

### 60. ¿Por que ComandoInsertar no guarda el cursor anterior?

El orden LIFO garantiza que, al llegar a deshacer esa insercion, ya se
revirtieron las acciones posteriores y el cursor esta donde corresponde.

### 61. ¿Por que ejecutar vacia rehacer despues y no antes del comando?

Si el comando falla, el documento no cambia y el futuro anterior debe seguir
disponible. Solo se invalida rehacer despues de una ejecucion exitosa.

## Extras sobre pruebas y restricciones

### 62. ¿Por que Random usa una semilla fija?

La semilla `2026` hace reproducible la secuencia de 100.000 caracteres. Un error
puede repetirse con exactamente los mismos datos.

### 63. ¿Por que la prueba guarda esperados en un arreglo?

Las colecciones del API estan prohibidas. El arreglo normal permite validar
cantidad y orden sin violar esa restriccion.

### 64. ¿Como funciona verificar en los tests?

Si la condicion es falsa, lanza `AssertionError`. Si se imprime el mensaje
final, todas las verificaciones anteriores pasaron.

### 65. ¿Que clases de java.util se usan realmente?

Solo `Iterator` y `Random`, ambas permitidas. `Iterable` pertenece a `java.lang`
y no necesita import.

### 66. ¿Por que BufferGap no usa nodos?

Su representacion exigida es un arreglo con hueco. Los nodos aparecen solamente
en `PilaES`, donde implementan la pila enlazada del Ejercicio 2.

### 67. ¿Que archivos debes poder explicar?

Todos los `.java`, las decisiones del `README`, los resultados de la tabla y el
script de ejecucion. La defensa evalua dominio individual sobre cualquier parte
entregada.

# Lista de prioridad para estudiar

Si tienes poco tiempo, estudia en este orden:

1. Preguntas 1 a 7: invariante y traduccion.
2. Preguntas 8 a 17: insertar, mover, get y crecimiento.
3. Preguntas 18 a 23: trazas en papel.
4. Preguntas 24 a 32: comandos e historial.
5. Preguntas 33 a 40: modificaciones propuestas.
6. Preguntas 41 a 47: dominio del resto del codigo.
7. Preguntas 48 a 67: repreguntas extra.

# Simulacro de 10 minutos

Una practica alineada con el tiempo real de defensa:

1. Minuto 0-2: dibujar el invariante y explicar las formulas.
2. Minuto 2-3: traducir indices del estado `HoX|la`.
3. Minuto 3-5: explicar insertar, mover y crecimiento.
4. Minuto 5-7: resolver la traza de practica de la pregunta 20.
5. Minuto 7-8: explicar el estado minimo de los comandos.
6. Minuto 8-9: explicar la bifurcacion de rehacer.
7. Minuto 9-10: responder una modificacion de las preguntas 33 a 39.

Repite el simulacro hasta poder completarlo sin leer el codigo.
