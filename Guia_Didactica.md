# Guia didactica completa del TP1

## 1. Proposito del trabajo

Este trabajo implementa dos ideas relacionadas:

1. Un editor elemental basado en un `BufferGap<E>`.
2. Un historial de edicion con operaciones de deshacer y rehacer.

El objetivo no es construir una aplicacion grafica. El objetivo es practicar
tipos abstractos de datos, encapsulamiento, genericos, arreglos, interfaces,
iteradores, excepciones, nodos enlazados y objetos que representan comandos.

La solucion evita las estructuras de datos del API de Java. No usa
`ArrayList`, `LinkedList`, `Stack`, `ArrayDeque`, streams, reflexion,
`Arrays.copyOf` ni `System.arraycopy`.

## 2. Vista general de la arquitectura

La relacion entre las clases puede resumirse asi:

```text
BufferGap<E>
    implementa Iterable<E>
    usa BufferVacioException
    usa PosicionInvalidaException

Comando
    implementado por ComandoInsertar
    implementado por ComandoBorrar
    implementado por ComandoMoverCursor
        los tres operan sobre BufferGap<Character>

PilaES<E>
    usa una clase interna privada Nodo
    usa PilaVaciaException

HistorialEdicion
    posee PilaES<Comando> deshacer
    posee PilaES<Comando> rehacer

TestBufferGap
    prueba BufferGap y compara desplazamientos

TestHistorial
    prueba comandos, pilas, deshacer y rehacer
```

## 3. Responsabilidad de cada archivo

| Archivo | Responsabilidad |
|---|---|
| `BufferGap.java` | Implementar la secuencia generica con hueco movil. |
| `BufferVacioException.java` | Informar que no hay elemento antes del cursor para borrar. |
| `PosicionInvalidaException.java` | Informar indices o movimientos fuera de rango. |
| `TestBufferGap.java` | Verificar la primera parte y producir la tabla. |
| `Comando.java` | Definir el contrato comun de las acciones editables. |
| `ComandoInsertar.java` | Representar una insercion reversible. |
| `ComandoBorrar.java` | Representar un borrado reversible. |
| `ComandoMoverCursor.java` | Representar un movimiento reversible. |
| `PilaES.java` | Implementar una pila generica enlazada propia. |
| `PilaVaciaException.java` | Informar un uso invalido de una pila vacia. |
| `HistorialEdicion.java` | Coordinar las pilas de deshacer y rehacer. |
| `TestHistorial.java` | Verificar la segunda parte y la traza de 12 pasos. |
| `README.md` | Resumir el diseño, ejecucion y resultados. |
| `Preguntas_Defensa.md` | Reunir preguntas breves para la defensa oral. |
| `Declaracion_de_Honor.txt` | Contener la declaracion exigida para la entrega. |
| `probar.bat` | Compilar, ejecutar y limpiar los `.class`. |

## 4. Conceptos de Java utilizados

### 4.1 Clases y objetos

Una clase define datos y operaciones. Un objeto es una instancia concreta de
esa clase. Por ejemplo:

```java
BufferGap<Character> buffer = new BufferGap<Character>();
```

`BufferGap<Character>` es el tipo y `buffer` referencia una instancia.

### 4.2 Encapsulamiento

Los campos importantes son `private`. El usuario no modifica directamente el
arreglo ni los limites del hueco. Solo puede operar mediante metodos publicos.

Esto protege el invariante. Si se permitiera cambiar `inicioHueco` desde fuera,
seria posible dejar la estructura en un estado incoherente.

### 4.3 Genericos

`BufferGap<E>` y `PilaES<E>` usan un parametro de tipo `E`. Esto permite usar
la misma implementacion con diferentes tipos:

```java
BufferGap<Character> texto = new BufferGap<Character>();
BufferGap<Integer> numeros = new BufferGap<Integer>();
PilaES<Comando> comandos = new PilaES<Comando>();
```

El compilador controla que no se mezclen tipos incompatibles.

### 4.4 Creacion de un arreglo generico

Java no permite escribir directamente `new E[16]`. Por eso se crea un arreglo
de `Object` y se hace un cast:

```java
datos = (E[]) new Object[TAM_INICIAL];
```

El compilador no puede comprobar completamente ese cast y produce una
advertencia. `@SuppressWarnings("unchecked")` se coloca solamente en los
lugares que crean el arreglo para suprimir esa advertencia de forma localizada.

### 4.5 Interfaces

Una interfaz define un contrato. `Comando` obliga a implementar:

```java
void ejecutar();
void deshacer();
String descripcion();
```

`Iterable<E>` indica que un objeto puede recorrerse con `for-each`.

### 4.6 Polimorfismo

`HistorialEdicion` trabaja con referencias de tipo `Comando`. Puede guardar un
`ComandoInsertar`, `ComandoBorrar` o `ComandoMoverCursor` sin preguntar de que
clase concreta es. Al llamar `ejecutar()` o `deshacer()`, Java selecciona el
metodo de la clase concreta.

### 4.7 Excepciones checked y unchecked

Una excepcion checked hereda de `Exception` y obliga a capturarla o declararla.
Una excepcion unchecked hereda de `RuntimeException` y no impone esa obligacion
al compilador.

La eleccion no significa que una sea mas importante. Expresa si la situacion
forma parte del uso normal recuperable o si normalmente representa un error de
programacion.

## 5. Idea fundamental de BufferGap

Un arreglo tradicional guarda todos los elementos juntos:

```text
[H][o][l][a][ ][ ][ ][ ]
```

Insertar en el medio normalmente exige mover los elementos posteriores. Un
BufferGap deja un espacio libre, llamado hueco, en el lugar donde se esta
editando:

```text
[H][o][ ][ ][ ][ ][l][a]
       ^ hueco
```

La posicion del cursor coincide con el comienzo del hueco. Insertar en esa
posicion consume una celda libre y no mueve la zona derecha.

## 6. Representacion interna e invariante

Los campos de `BufferGap<E>` son:

```java
private E[] datos;
private int inicioHueco;
private int finHueco;
private long desplazamientos;
```

El arreglo se divide siempre asi:

```text
0                                             datos.length - 1
| izquierda |              hueco              | derecha |
             ^                                 ^
       inicioHueco                         finHueco
```

Las reglas que siempre deben cumplirse son:

1. `datos[0 .. inicioHueco-1]` contiene los elementos anteriores al cursor.
2. En la izquierda, indice logico e indice fisico coinciden.
3. `datos[inicioHueco .. finHueco-1]` es el hueco.
4. Las referencias que queden dentro del hueco no representan elementos.
5. `datos[finHueco .. datos.length-1]` contiene la parte derecha.
6. La parte derecha conserva su orden logico.
7. La posicion del cursor es exactamente `inicioHueco`.
8. No existe un campo separado llamado `cursor`.

Las identidades fundamentales son:

```text
tamanoHueco = finHueco - inicioHueco
size        = capacidad - tamanoHueco
cursor      = inicioHueco
capacidad   = datos.length
```

## 7. Estado inicial

La capacidad inicial es 16:

```java
private static final int TAM_INICIAL = 16;
```

El constructor establece:

```text
datos.length     = 16
inicioHueco      = 0
finHueco         = 16
desplazamientos  = 0
size             = 16 - (16 - 0) = 0
```

Todo el arreglo es hueco. La representacion logica es:

```text
|
```

## 8. Insercion explicada paso a paso

El metodo `insertar(E obj)` hace dos cosas:

1. Si `inicioHueco == finHueco`, llama a `crecer()`.
2. Escribe en `datos[inicioHueco]` y aumenta `inicioHueco`.

Ejemplo inicial:

```text
Antes de insertar H:
inicioHueco = 0, finHueco = 16
|____________hueco____________|

Despues de insertar H:
datos[0] = H
inicioHueco = 1, finHueco = 16
H|___________hueco____________|
```

La insercion normal no incrementa `desplazamientos`, porque el objeto nuevo no
es un elemento existente que cambia de celda.

## 9. Borrado explicado paso a paso

`borrar()` representa Backspace, no Delete. Elimina el elemento inmediatamente
anterior al cursor.

Desde:

```text
HoX|la
```

el metodo disminuye `inicioHueco` de 3 a 2 y devuelve `datos[2]`, que es `X`.
El resultado logico es:

```text
Ho|la
```

`finHueco` no cambia. Por lo tanto, el hueco crece una celda hacia la izquierda.
No es necesario escribir `null`: esa celda ya pertenece al hueco y su contenido
fisico deja de tener significado.

Si `inicioHueco == 0`, no existe ningun elemento anterior y se lanza
`BufferVacioException`.

## 10. Movimiento del cursor

Antes de mover, se calcula:

```java
long nuevaPosicion = (long) inicioHueco + delta;
```

El cast a `long` evita que una suma extrema de enteros desborde silenciosamente
y produzca una validacion incorrecta. La posicion debe quedar entre cero y
`size()`, inclusive.

### 10.1 Movimiento hacia la izquierda

Cuando el hueco tiene tamaño mayor que cero, para cada posicion:

```java
datos[finHueco - 1] = datos[inicioHueco - 1];
inicioHueco--;
finHueco--;
desplazamientos++;
```

Partiendo de `Hola|`, con capacidad 16:

```text
Indices fisicos:  0   1   2   3   4 ... 15
Contenido valido: H   o   l   a   <hueco>
Limites: inicioHueco=4, finHueco=16
```

Primer paso a la izquierda:

```text
datos[15] = datos[3]  // mueve a
inicioHueco=3, finHueco=15
Resultado logico: Hol|a
```

Segundo paso:

```text
datos[14] = datos[2]  // mueve l
inicioHueco=2, finHueco=14
Resultado logico: Ho|la
```

Se contaron dos desplazamientos. Si el hueco tiene tamaño cero, la celda de
origen y la de destino coinciden; en ese caso solo cambian ambos limites y no se
cuenta una autoasignacion como desplazamiento fisico.

### 10.2 Movimiento hacia la derecha

Cuando el hueco tiene tamaño mayor que cero, para cada posicion:

```java
datos[inicioHueco] = datos[finHueco];
inicioHueco++;
finHueco++;
desplazamientos++;
```

Desde `Ho|la`, un paso copia `l` desde el comienzo de la zona derecha al
comienzo del hueco. El resultado es `Hol|a`. Con hueco de tamaño cero, solo se
ajustan los dos limites porque no existe una celda distinta a la cual copiar.

### 10.3 Delta igual a cero

Las dos condiciones de los ciclos son falsas. No cambia ningun campo y no se
cuentan desplazamientos.

### 10.4 Por que no cambia el tamaño

Ambos limites avanzan o retroceden juntos. La diferencia
`finHueco - inicioHueco` permanece igual, por lo que `size()` tampoco cambia.

## 11. Traduccion de indice logico a fisico

El usuario ve una secuencia sin hueco. Por eso `get` y `set` reciben indices
logicos.

La traduccion es:

```java
if (index < inicioHueco) {
    return index;
}
return index + (finHueco - inicioHueco);
```

Ejemplo con `HoX|la`, `inicioHueco=3` y `finHueco=14`:

```text
Tamano del hueco = 14 - 3 = 11

Indice logico 0 -> fisico 0  -> H
Indice logico 1 -> fisico 1  -> o
Indice logico 2 -> fisico 2  -> X
Indice logico 3 -> fisico 14 -> l
Indice logico 4 -> fisico 15 -> a
```

Por eso `get(4)` retorna `a` sin recorrer el arreglo.

## 12. get, set y validacion

`validarIndice` acepta solamente:

```text
0 <= index < size()
```

El indice `size()` no es valido para leer o reemplazar, aunque si es una
posicion valida para el cursor.

`get(index)` valida, traduce y devuelve el dato.

`set(obj, index)` valida, traduce, guarda el valor anterior, escribe el nuevo y
retorna el anterior. No modifica el tamaño, el cursor ni el contador.

## 13. Crecimiento por duplicacion

El crecimiento ocurre solamente cuando el hueco tiene tamaño cero:

```text
inicioHueco == finHueco
```

Se crea un arreglo con capacidad doble. Luego:

1. Se cuenta cuantos elementos hay a la derecha.
2. La izquierda se copia al comienzo.
3. La derecha se copia al final.
4. El espacio nuevo queda entre ambas zonas.

Ejemplo de un arreglo lleno de capacidad 16 con cursor en 8:

```text
Antes:
[A B C D E F G H][I J K L M N O P]
                 ^ cursor, hueco de tamaño 0

Despues, capacidad 32:
[A B C D E F G H][        hueco        ][I J K L M N O P]
                 ^ cursor
```

La izquierda continua en `0..7`. Los ocho elementos derechos quedan en
`24..31`. El nuevo hueco ocupa `8..23`.

Todos los elementos existentes se copiaron, por lo que todos cuentan como
desplazamientos. No se usan utilidades de copia del API.

## 14. Consultas simples

| Metodo | Resultado |
|---|---|
| `posicionCursor()` | `inicioHueco` |
| `size()` | Capacidad menos tamaño del hueco |
| `capacidad()` | `datos.length` |
| `desplazamientos()` | Valor acumulado del contador |
| `reiniciarDesplazamientos()` | Coloca solo el contador en cero |

Reiniciar desplazamientos no borra elementos ni devuelve el cursor al inicio.

## 15. Representacion con toString

`toString()` recorre indices logicos. Antes de imprimir el elemento cuyo indice
coincide con el cursor, agrega `|`.

Si el cursor esta al final, el ciclo termina antes de agregar la barra. Por eso
existe una comprobacion adicional:

```java
if (inicioHueco == size()) {
    resultado.append('|');
}
```

La barra solo representa visualmente el cursor. No se almacena en `datos`.

`StringBuilder` pertenece a `java.lang`, no es una coleccion y resulta adecuado
para construir texto sin concatenar repetidamente objetos `String` inmutables.

## 16. Iterable e Iterator

Implementar `Iterable<E>` permite escribir:

```java
for (Character valor : buffer) {
    // usar valor
}
```

`iterator()` crea un objeto anonimo que implementa `Iterator<E>`. Ese objeto
mantiene su propio `indiceLogico`.

`hasNext()` responde si quedan elementos:

```java
indiceLogico < size()
```

`next()` usa:

```java
return get(indiceLogico++);
```

Como usa `get`, aplica la misma traduccion y nunca devuelve celdas del hueco.
No se importa `NoSuchElementException`, porque no figura entre las clases de
`java.util` permitidas por el TP. Un uso incorrecto de `next()` fuera de rango
termina en `PosicionInvalidaException`.

## 17. Contador de desplazamientos

La definicion usada es: un desplazamiento ocurre cuando un elemento que ya
existia cambia de celda fisica.

| Operacion | ¿Cuenta? | Motivo |
|---|---:|---|
| Insertar normalmente | No | El elemento es nuevo. |
| Borrar | No | Solo cambia el limite del hueco. |
| Mover cursor | Si, con hueco disponible | Se traslada un elemento por posicion; con hueco de tamaño cero solo cambian los limites. |
| Crecer | Si | Cada elemento existente se copia. |
| `get` | No | Solo consulta. |
| `set` | No | Reemplaza en la misma celda. |
| `toString` | No | Solo consulta. |
| Iterar | No | Solo consulta. |

Se usa `long` porque la comparacion con el arreglo simple produce valores como
5.000.000.000, superiores al maximo de un `int`.

## 18. Excepciones propias

### 18.1 BufferVacioException

Hereda de `Exception`, por lo que es checked. Se usa cuando `borrar()` no tiene
un elemento anterior al cursor.

Es checked porque el usuario puede presionar Backspace al comienzo de un texto
durante el uso normal. El llamador puede manejar esa situacion.

### 18.2 PosicionInvalidaException

Hereda de `RuntimeException`, por lo que es unchecked. Se usa en `get`, `set` y
`moverCursor`.

Pedir un indice inexistente o mover el cursor fuera de los limites suele ser un
error del codigo llamador.

### 18.3 PilaVaciaException

Tambien hereda de `RuntimeException`. Se lanza al llamar `tope()` o
`desapilar()` en una pila vacia.

El historial evita esta excepcion durante su uso normal consultando primero
`estaVacia()`.

### 18.4 IllegalStateException

No es una clase creada para el TP: pertenece a Java. Los comandos la usan para
informar una inconsistencia interna.

La interfaz `Comando` no declara `throws`. Cuando un comando captura una
`BufferVacioException` que no deberia ocurrir en una historia consistente, la
convierte en `IllegalStateException` y conserva la causa original con el
segundo parametro del constructor.

## 19. Patron Comando

Un comando es un objeto que representa una accion. No guarda solamente una
descripcion: sabe ejecutarse y deshacerse.

Ventajas en este TP:

1. El historial no necesita condicionales para cada clase de accion.
2. Cada accion encapsula la informacion necesaria para revertirse.
3. El mismo objeto puede pasar entre las pilas de deshacer y rehacer.

## 20. ComandoInsertar

Guarda:

```java
private final BufferGap<Character> buffer;
private final char caracter;
```

`ejecutar()` llama a `buffer.insertar(caracter)`.

`deshacer()` llama a `buffer.borrar()`. Si el comando fue ejecutado y es el
ultimo comando vigente, el cursor debe encontrarse inmediatamente despues del
caracter insertado. Si no puede borrarlo, el historial esta inconsistente y se
lanza `IllegalStateException`.

No necesita guardar una posicion porque las operaciones se deshacen en orden
LIFO. Antes de deshacer una insercion, ya se deshicieron todas las acciones que
ocurrieron despues.

## 21. ComandoBorrar

Guarda:

```java
private final BufferGap<Character> buffer;
private Character caracterBorrado;
```

`ejecutar()` llama a `borrar()` y almacena exactamente el caracter retornado.
Sin ese dato seria imposible saber que debe restaurar.

`deshacer()` reinserta `caracterBorrado` en el cursor. Si el campo sigue en
`null`, considera que el comando no fue ejecutado.

El editor del TP trabaja con caracteres concretos creados como valores `char`,
por lo que sus comandos normales no borran un `Character` nulo.

Cuando se rehace el mismo comando, `ejecutar()` vuelve a borrar y vuelve a
guardar el caracter efectivamente eliminado.

## 22. ComandoMoverCursor

Guarda solamente:

```java
private final BufferGap<Character> buffer;
private final int delta;
```

Ejecutar significa `moverCursor(delta)`. Deshacer significa
`moverCursor(-delta)`. No hace falta guardar la posicion anterior porque el
movimiento inverso recupera esa posicion.

## 23. Metodo descripcion

Cada comando devuelve un texto breve, por ejemplo:

```text
Insertar '!'
Borrar caracter anterior al cursor
Mover cursor -4
```

El historial no interpreta estas cadenas. La logica esta en los metodos del
objeto comando. La descripcion solo sirve para presentar informacion.

## 24. PilaES como estructura enlazada

Una pila sigue la regla LIFO:

```text
ultimo en entrar = primero en salir
```

`PilaES<E>` mantiene:

```java
private Nodo tope;
private int cantidad;
```

`Nodo` es una clase interna privada:

```java
private class Nodo {
    private E dato;
    private Nodo next;
}
```

Es interna porque forma parte de la implementacion de la pila. Es privada para
que ningun usuario pueda manipular los enlaces y romper la estructura.

### 24.1 Apilar

```java
tope = new Nodo(dato, tope);
cantidad++;
```

Si la pila era:

```text
tope -> B -> A -> null
```

al apilar `C` queda:

```text
tope -> C -> B -> A -> null
```

### 24.2 Desapilar

Se guarda el dato del tope, se avanza al nodo siguiente y se reduce la cantidad:

```java
E dato = tope.dato;
tope = tope.next;
cantidad--;
return dato;
```

### 24.3 Consultas

`tope()` devuelve el dato sin eliminarlo. `estaVacia()` comprueba si
`cantidad == 0`. `size()` retorna `cantidad` directamente.

## 25. HistorialEdicion

El historial posee dos pilas propias:

```java
private final PilaES<Comando> deshacer;
private final PilaES<Comando> rehacer;
```

`final` significa que la referencia de cada campo no sera reemplazada. El
contenido de las pilas si puede cambiar.

### 25.1 Ejecutar una accion nueva

El orden es importante:

1. `comando.ejecutar()`.
2. `deshacer.apilar(comando)`.
3. Vaciar la pila `rehacer`.

Rehacer no se vacia antes de ejecutar. Si el comando falla, la historia previa
debe permanecer disponible.

Una accion nueva elimina rehacer porque crea una nueva rama temporal. Los
comandos deshechos describian otro futuro que ya no corresponde al documento.

### 25.2 Deshacer

Si la pila esta vacia, retorna `false`. En caso contrario:

1. Desapila el comando de `deshacer`.
2. Llama a `comando.deshacer()`.
3. Apila el mismo objeto en `rehacer`.
4. Retorna `true`.

### 25.3 Rehacer

Si no hay comandos, retorna `false`. En caso contrario:

1. Desapila de `rehacer`.
2. Llama a `comando.ejecutar()`.
3. Apila el mismo objeto en `deshacer`.
4. Retorna `true`.

No llama a `HistorialEdicion.ejecutar(comando)`, porque ese metodo vaciaria la
pila de rehacer y eliminaria otros comandos todavia disponibles.

## 26. Traza completa del historial

El estado inicial `HoX|la` se prepara directamente en el buffer. Esas acciones
no se registran, por lo que ambas pilas comienzan vacias.

| Paso | Accion | Buffer | Deshacer | Rehacer | Explicacion |
|---:|---|---|---:|---:|---|
| 0 | Estado inicial | `HoX|la` | 0 | 0 | Preparado fuera del historial. |
| 1 | Insertar `!` | `HoX!|la` | 1 | 0 | Se registra la insercion. |
| 2 | Insertar `?` | `HoX!?|la` | 2 | 0 | Queda como accion mas reciente. |
| 3 | Deshacer | `HoX!|la` | 1 | 1 | Se revierte `?`. |
| 4 | Deshacer | `HoX|la` | 0 | 2 | Se revierte `!`. |
| 5 | Rehacer | `HoX!|la` | 1 | 1 | Se vuelve a insertar `!`. |
| 6 | Mover -4 | `|HoX!la` | 2 | 0 | Accion nueva: invalida rehacer. |
| 7 | Rehacer | `|HoX!la` | 2 | 0 | Retorna false: no queda futuro. |
| 8 | Deshacer | `HoX!|la` | 1 | 1 | Movimiento inverso de +4. |
| 9 | Borrar | `HoX|la` | 2 | 0 | Borra `!` y vacia rehacer. |
| 10 | Deshacer | `HoX!|la` | 1 | 1 | Restaura el caracter guardado. |
| 11 | Deshacer | `HoX|la` | 0 | 2 | Deshace la insercion original. |
| 12 | Deshacer | `HoX|la` | 0 | 2 | Retorna false: pila vacia. |

El paso 7 demuestra la bifurcacion de la historia. El paso 10 demuestra por que
`ComandoBorrar` necesita guardar el caracter eliminado.

## 27. TestBufferGap

No se usa un framework de pruebas. El metodo privado `verificar` lanza
`AssertionError` cuando una condicion no se cumple:

```java
if (!condicion) {
    throw new AssertionError(mensaje);
}
```

Si el programa llega al mensaje final, todas las verificaciones anteriores
pasaron.

### 27.1 Traza obligatoria

Comprueba los estados:

```text
|
H|
Ho|
Hol|
Hola|
Ho|la
HoX|la
HoX|la
Ho|la
```

Tambien comprueba limites del hueco y desplazamientos. `finHueco` se calcula sin
romper encapsulamiento:

```text
finHueco = posicionCursor + capacidad - size
```

### 27.2 Limites y crecimiento

Verifica:

1. Borrar al inicio lanza `BufferVacioException`.
2. La capacidad inicial es 16.
3. La insercion numero 17 duplica la capacidad a 32.
4. El crecimiento cuenta las 16 copias existentes.
5. `set` retorna el valor anterior.
6. `get`, `set` y `moverCursor` rechazan limites invalidos.

### 27.3 Prueba aleatoria de 100.000 caracteres

Se usa un arreglo `Character[]` como resultado esperado, no una coleccion. La
semilla fija `2026` hace que la secuencia aleatoria sea reproducible.

Luego se recorre el buffer con `for-each` y se comprueban cantidad y orden. Esto
prueba conjuntamente insercion, varios crecimientos, traduccion de indices e
iterador.

### 27.4 Tabla de desplazamientos

Para cada `n` desde 100.000 hasta 1.000.000:

1. Se carga el BufferGap.
2. Se mueve el cursor a `n/2`.
3. Se reinicia el contador despues del movimiento.
4. Se insertan 10.000 caracteres en el hueco.
5. Se compara con un arreglo simple.

El hueco disponible supera las 10.000 posiciones en todos los tamaños medidos,
por lo que esas inserciones no requieren crecimiento y producen cero
desplazamientos en el BufferGap.

El helper `ArregloSimple` implementa la insercion ingenua desplazando con un
ciclo todos los elementos que estan a la derecha. `insertarConsecutivos` llama
10.000 veces a esa insercion individual y avanza la posicion despues de cada
caracter. Por eso la prueba realiza y cuenta realmente cada traslado.

Si hay `n/2` elementos a la derecha y se hacen 10.000 inserciones consecutivas,
el contador equivalente es:

```text
(n / 2) * 10.000
```

Los resultados reales fueron:

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

## 28. TestHistorial

Primero crea `HoX|la` directamente, antes de crear acciones del historial.
Despues ejecuta exactamente la traza de 12 pasos.

`verificarPaso` controla simultaneamente:

1. Contenido y posicion del cursor mediante `toString()`.
2. Tamaño de la pila deshacer.
3. Tamaño de la pila rehacer.

La prueba adicional de `PilaES<Integer>` verifica:

1. Estado vacio inicial.
2. Apilado de 1 y 2.
3. Tope igual a 2.
4. Orden de salida 2, 1.
5. Excepcion al consultar el tope vacio.

## 29. Compilacion y script de pruebas

El proyecto requiere JDK 21 y no usa `package`. Todos los `.java` se encuentran
en el mismo directorio.

La forma recomendada de ejecutar todo en Windows es:

```text
probar.bat
```

El script realiza este proceso:

```text
1. Elimina un build anterior y .class accidentales de la raiz.
2. Crea la carpeta build.
3. Ejecuta javac -d build *.java.
4. Ejecuta java -cp build TestBufferGap.
5. Ejecuta java -cp build TestHistorial.
6. Elimina build y cualquier .class de la raiz.
7. Devuelve codigo 0 si todo paso o un codigo de error si algo fallo.
```

La limpieza tambien ocurre cuando falla la compilacion o una prueba.

## 30. Restricciones y como se cumplen

| Restriccion | Cumplimiento |
|---|---|
| Sin paquetes | Ningun archivo declara `package`. |
| JDK 21 | Compilado y probado con JDK 21.0.11. |
| Sin colecciones | Se usan arreglos y `PilaES`. |
| Sin Stack | El historial usa la pila propia. |
| Sin copia automatica | El crecimiento usa ciclos. |
| Sin streams | Todos los recorridos usan ciclos. |
| Sin reflexion | No existe codigo reflectivo. |
| Imports limitados | Solo `Iterator` y `Random`. |
| Nodo encapsulado | `Nodo` es interno y privado. |
| Sin cursor duplicado | El cursor es `inicioHueco`. |
| Clases publicas separadas | Cada clase publica tiene su `.java`. |

## 31. Costos intuitivos de las operaciones

Sin necesidad de un analisis formal, se puede explicar:

| Operacion | Trabajo principal |
|---|---|
| Insercion con hueco disponible | Una escritura. |
| Borrado | Cambio de limite y retorno. |
| `get` o `set` | Calculo directo de indice. |
| Mover cursor | Un traslado por posicion recorrida, salvo que el hueco tenga tamaño cero. |
| Crecimiento | Copiar todos los elementos existentes. |
| Apilar o desapilar | Cambiar referencias en el frente. |
| Deshacer o rehacer | Operacion del comando mas cambios de pila. |

La ventaja del BufferGap aparece cuando se realizan muchas inserciones cerca
de un cursor que ya tiene el hueco colocado.

## 32. Diferencia entre estado logico y estado fisico

El estado logico es lo que ve el usuario:

```text
HoX|la
```

El estado fisico incluye capacidad, hueco y posibles referencias antiguas que
ya no significan nada. Dos estados fisicos distintos pueden representar el
mismo texto logico.

Los usuarios del TAD no deben depender del estado fisico. Por eso no existen
getters publicos para `datos`, `inicioHueco` o `finHueco`.

## 33. Por que la solucion mantiene el invariante

Cada operacion modifica los limites de manera controlada:

1. Insertar aumenta solo `inicioHueco`, reduciendo el hueco.
2. Borrar reduce solo `inicioHueco`, ampliando el hueco.
3. Mover reduce o aumenta ambos limites, conservando su tamaño.
4. Crecer conserva `inicioHueco` y recalcula `finHueco` segun la zona derecha.
5. `get`, `set`, consultas e iteracion no cambian limites.

Antes de una operacion peligrosa se validan sus precondiciones. De esa manera,
ningun metodo publico deberia dejar los limites fuera del arreglo.

## 34. Errores conceptuales que deben evitarse al explicarlo

1. No decir que el cursor es un campo separado.
2. No decir que las celdas del hueco deben contener `null`.
3. No decir que borrar elimina el elemento posterior; funciona como Backspace.
4. No decir que insertar siempre desplaza elementos.
5. No contar el elemento nuevo como desplazamiento.
6. No afirmar que `get` recorre el arreglo.
7. No decir que el historial guarda textos o descripciones; guarda comandos.
8. No decir que rehacer llama al metodo publico `ejecutar` del historial.
9. No decir que se usa `Stack` o `LinkedList`.
10. No confundir capacidad con cantidad de elementos.

## 35. Resumen para una exposicion corta

Una explicacion de aproximadamente un minuto puede ser:

> BufferGap guarda una secuencia en un arreglo dividido en zona izquierda,
> hueco y zona derecha. El cursor es el comienzo del hueco. Insertar consume
> una celda libre y borrar amplia el hueco; mover el cursor traslada un elemento
> por posicion cuando existe hueco. Los indices logicos posteriores al cursor saltan el tamaño del
> hueco. Cuando se llena, el arreglo duplica su capacidad y conserva el hueco
> en el cursor. Para deshacer y rehacer, cada accion es un objeto Comando con el
> estado minimo necesario. HistorialEdicion mueve esos objetos entre dos pilas
> enlazadas propias. Las pruebas validan la traza, excepciones, crecimiento,
> iteracion, desplazamientos y los 12 estados del historial.

## 36. Orden recomendado para estudiar

1. Memorizar el dibujo izquierda, hueco y derecha.
2. Comprender las formulas de tamaño y traduccion.
3. Simular a mano insertar, borrar y mover.
4. Entender por que crecer copia izquierda y derecha en lugares distintos.
5. Repasar la diferencia entre excepciones checked y unchecked.
6. Dibujar una pila enlazada de tres nodos.
7. Simular como un comando pasa entre deshacer y rehacer.
8. Recorrer la traza de 12 pasos sin mirar el codigo.
9. Ejecutar `probar.bat` y relacionar cada salida con su prueba.
10. Practicar las preguntas de `Preguntas_Defensa.md`.
