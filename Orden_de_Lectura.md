# Instructivo para leer el proyecto en orden

## Objetivo

Este documento indica en que orden conviene revisar los archivos para entender
el trabajo sin saltar conceptos. No se recomienda comenzar por los tests ni por
`HistorialEdicion`, porque ambos dependen de estructuras explicadas antes.

## Mapa del recorrido

```text
1. README.md
2. BufferVacioException.java
3. PosicionInvalidaException.java
4. BufferGap.java
5. TestBufferGap.java
6. Comando.java
7. ComandoInsertar.java
8. ComandoBorrar.java
9. ComandoMoverCursor.java
10. PilaVaciaException.java
11. PilaES.java
12. HistorialEdicion.java
13. TestHistorial.java
14. probar.bat
15. Guia_Didactica.md
16. Preguntas_Defensa.md
17. Declaracion_de_Honor.txt
```

## Etapa 1: obtener la idea general

### 1. Leer README.md

Empieza por el README porque presenta el objetivo sin entrar todavia en todos
los detalles del codigo.

Lee en este orden:

1. Que representa `BufferGap<E>`.
2. Cual es su invariante.
3. Como se traducen indices logicos a fisicos.
4. Que cuenta como desplazamiento.
5. Que guarda cada comando.
6. Por que existen dos pilas.
7. Que resultados produjo la tabla.

Al terminar deberias poder responder:

- ¿Cual es el problema que resuelve el proyecto?
- ¿Que significa el caracter `|`?
- ¿Por que se usa un hueco?
- ¿Para que sirven las pilas deshacer y rehacer?

No intentes memorizar todavia los numeros de la tabla.

## Etapa 2: conocer las excepciones del buffer

### 2. Leer BufferVacioException.java

Observa que hereda de `Exception`:

```java
public class BufferVacioException extends Exception
```

Esto significa que es checked. El compilador obliga a capturarla o declararla.

Se utiliza cuando se intenta borrar y no existe un elemento antes del cursor.

### 3. Leer PosicionInvalidaException.java

Observa que hereda de `RuntimeException`:

```java
public class PosicionInvalidaException extends RuntimeException
```

Esto significa que es unchecked. Representa normalmente un error del codigo
llamador, como pedir un indice inexistente.

Antes de continuar debes distinguir:

```text
BufferVacioException       -> checked, situacion normal manejable
PosicionInvalidaException  -> unchecked, uso incorrecto del TAD
```

## Etapa 3: estudiar BufferGap

### 4. Leer BufferGap.java

Este es el archivo principal. No conviene leerlo de arriba abajo sin pausas.
Dividelo en los siguientes bloques.

### 4.1 Leer la declaracion de la clase

Busca:

```java
public class BufferGap<E> implements Iterable<E>
```

Identifica dos ideas:

- `<E>` indica que la clase es generica.
- `implements Iterable<E>` permite recorrerla con `for-each`.

### 4.2 Leer los campos

Revisa:

```java
private E[] datos;
private int inicioHueco;
private int finHueco;
private long desplazamientos;
```

Dibuja en papel:

```text
[ izquierda ][ hueco ][ derecha ]
              ^         ^
        inicioHueco  finHueco
```

Recuerda que el cursor es `inicioHueco`. No existe otro campo para el cursor.

Antes de avanzar debes comprender estas formulas:

```text
tamanoHueco = finHueco - inicioHueco
size        = datos.length - tamanoHueco
cursor      = inicioHueco
```

### 4.3 Leer el constructor

Busca `public BufferGap()`.

Comprueba el estado inicial:

```text
capacidad         = 16
inicioHueco       = 0
finHueco          = 16
desplazamientos   = 0
contenido logico  = |
```

Observa el cast desde `Object[]`. Es necesario porque Java no permite crear
directamente un arreglo con `new E[16]`.

### 4.4 Leer insertar

Busca `public void insertar(E obj)`.

Sigue las instrucciones en este orden:

1. Comprueba si el hueco esta agotado.
2. Si esta agotado, llama a `crecer()`.
3. Escribe en `datos[inicioHueco]`.
4. Incrementa `inicioHueco`.

Simula en papel:

```text
| -> H| -> Ho| -> Hol| -> Hola|
```

Nota que una insercion normal no aumenta el contador.

### 4.5 Leer crecer

Busca `private void crecer()`.

Revisalo lentamente:

1. Crea un arreglo del doble de capacidad.
2. Calcula cuantos elementos existen a la derecha.
3. Calcula donde empezara la derecha en el arreglo nuevo.
4. Copia la izquierda al comienzo mediante un ciclo.
5. Copia la derecha al final mediante otro ciclo.
6. Cuenta cada elemento copiado.
7. Reemplaza el arreglo viejo.
8. Actualiza `finHueco`.

Comprueba que `inicioHueco` no cambia. Esto mantiene el cursor en la misma
posicion logica.

### 4.6 Leer borrar

Busca `public E borrar()`.

Observa este orden:

1. Si `inicioHueco == 0`, lanza `BufferVacioException`.
2. Reduce `inicioHueco`.
3. Retorna el elemento de esa posicion.

Simula:

```text
HoX|la -> Ho|la
```

El metodo funciona como Backspace. No elimina el elemento posterior al cursor.

### 4.7 Leer moverCursor

Busca `public void moverCursor(int delta)`.

Primero estudia solamente la validacion:

```text
0 <= inicioHueco + delta <= size()
```

Despues estudia por separado los dos ciclos:

- Primer `while`: movimiento hacia la izquierda.
- Segundo `while`: movimiento hacia la derecha.

En ambos casos se mueve un elemento, cambian ambos limites y aumenta el
contador.

Simula obligatoriamente:

```text
Hola| -- mover -2 --> Ho|la
Ho|la -- mover +1 --> Hol|a
```

### 4.8 Leer get, set y la traduccion

Lee juntos estos metodos:

```text
get
set
validarIndice
indiceFisico
```

Primero entiende la validacion:

```text
0 <= index < size()
```

Luego memoriza la traduccion:

```text
si index < inicioHueco:
    fisico = index
si no:
    fisico = index + tamanoHueco
```

Usa el ejemplo `HoX|la`:

```text
get(0) -> H
get(2) -> X
get(3) -> l
get(4) -> a
```

### 4.9 Leer las consultas

Revisa:

```text
posicionCursor
size
capacidad
desplazamientos
reiniciarDesplazamientos
```

Son metodos cortos. Comprueba especialmente que reiniciar el contador no
reinicia el buffer.

### 4.10 Leer toString

Observa que recorre indices logicos y utiliza `get`. La barra se agrega cuando
el indice coincide con el cursor.

La condicion final agrega `|` cuando el cursor esta despues del ultimo
elemento.

### 4.11 Leer iterator al final

Deja esta parte para el final porque depende de comprender `get`.

El iterador guarda `indiceLogico`, comprueba `hasNext()` y llama a
`get(indiceLogico++)`. Por eso omite automaticamente el hueco.

### Control de comprension de BufferGap

No avances hasta poder explicar sin mirar:

- Que representan `inicioHueco` y `finHueco`.
- Como se calcula `size()`.
- Que diferencia existe entre indice logico y fisico.
- Por que insertar normalmente no mueve elementos.
- Que operaciones aumentan el contador.
- Como se conserva el cursor durante el crecimiento.

## Etapa 4: verificar BufferGap con sus pruebas

### 5. Leer TestBufferGap.java

Ahora que conoces el TAD, revisa como se demuestra su funcionamiento.

### 5.1 Leer main

El orden de ejecucion es:

```text
probarTrazaObligatoria
probarLimitesYCrecimiento
probarIteracionAleatoria
imprimirTablaDesplazamientos
```

### 5.2 Leer probarTrazaObligatoria

Sigue cada operacion junto con su estado esperado. Compara los resultados con
la simulacion que hiciste al leer `BufferGap`.

Presta atencion a:

```text
Hola|   -> inicio=4, fin=16, desplazamientos=0
Ho|la   -> inicio=2, fin=14, desplazamientos=2
HoX|la  -> inicio=3, fin=14, desplazamientos=2
Ho|la   -> inicio=2, fin=14, desplazamientos=2
```

### 5.3 Leer probarLimitesYCrecimiento

Identifica los bloques `try/catch` y que excepcion espera cada uno. Observa
tambien la insercion numero 17, que provoca el crecimiento de 16 a 32.

### 5.4 Leer probarIteracionAleatoria

Comprueba que los valores esperados se guardan en un arreglo normal. La semilla
`2026` permite repetir la misma secuencia aleatoria.

Luego identifica el `for-each`, que prueba el iterador.

### 5.5 Leer imprimirTablaDesplazamientos

Sigue este orden:

1. Carga `n` caracteres.
2. Mueve el cursor al centro.
3. Reinicia el contador despues del movimiento.
4. Inserta 10.000 caracteres.
5. Compara con `ArregloSimple`.

Lee `ArregloSimple` solamente despues de entender el ciclo principal. La
insercion individual desplaza realmente la zona derecha. El metodo de
inserciones consecutivas la llama 10.000 veces y avanza la posicion despues de
cada caracter, por lo que la prueba puede tardar mas al realizar todos los
traslados del arreglo ingenuo.

### 5.6 Leer verificar al final

`verificar` reemplaza un framework de pruebas. Si una condicion es falsa, lanza
`AssertionError` y detiene la ejecucion.

## Etapa 5: estudiar el contrato de los comandos

### 6. Leer Comando.java

Es una interfaz pequeña:

```java
void ejecutar();
void deshacer();
String descripcion();
```

Comprende primero este contrato antes de leer las implementaciones.

El historial puede trabajar con cualquier clase que cumpla la interfaz.

### 7. Leer ComandoInsertar.java

Revisa en orden:

1. Campos: referencia al buffer y caracter.
2. Constructor.
3. `ejecutar`: inserta el caracter.
4. `deshacer`: borra la insercion.
5. Conversion de `BufferVacioException` a `IllegalStateException`.
6. Descripcion.

Pregunta clave: ¿por que no guarda una posicion? Porque los comandos se
deshacen en orden inverso y el estado anterior ya fue restaurado.

### 8. Leer ComandoBorrar.java

Este es el comando que guarda mas estado. Revisa:

1. Referencia al buffer.
2. Campo `caracterBorrado`.
3. `ejecutar`, que guarda el valor retornado por `borrar`.
4. `deshacer`, que reinserta ese valor.

Pregunta clave: ¿por que debe guardar el caracter? Porque despues de borrarlo
ya no puede obtenerlo del contenido logico del buffer.

### 9. Leer ComandoMoverCursor.java

Es el mas simple:

```text
ejecutar  -> moverCursor(delta)
deshacer  -> moverCursor(-delta)
```

Guarda solo `delta`, porque el movimiento contrario restaura la posicion.

### Control de comprension de comandos

Debes poder completar esta tabla sin mirar:

| Comando | Estado que guarda | Como deshace |
|---|---|---|
| Insertar | Caracter | Borra |
| Borrar | Caracter eliminado | Reinserta |
| Mover | Delta | Mueve con delta contrario |

## Etapa 6: estudiar la pila propia

### 10. Leer PilaVaciaException.java

Es unchecked. Se usa cuando se intenta consultar o quitar un elemento que no
existe.

### 11. Leer PilaES.java

Lee el archivo en este orden.

### 11.1 Leer Nodo

Observa que es una clase interna y privada:

```java
private class Nodo
```

Cada nodo guarda un dato y una referencia al siguiente. Dibuja:

```text
tope -> [dato|next] -> [dato|next] -> null
```

### 11.2 Leer los campos de PilaES

```java
private Nodo tope;
private int cantidad;
```

`tope` permite acceder al primer nodo. `cantidad` evita recorrer la lista para
calcular `size()`.

### 11.3 Leer apilar

El nuevo nodo apunta al tope anterior y despues se convierte en el nuevo tope.

### 11.4 Leer desapilar

Primero comprueba si esta vacia. Luego guarda el dato, avanza `tope` al nodo
siguiente, reduce la cantidad y devuelve el dato.

### 11.5 Leer tope, estaVacia y size

`tope()` consulta sin eliminar. `estaVacia()` comprueba `cantidad == 0`.
`size()` devuelve el contador.

Antes de avanzar debes poder demostrar con los valores 1, 2 y 3 que la pila
devuelve primero el 3.

## Etapa 7: estudiar el historial

### 12. Leer HistorialEdicion.java

### 12.1 Leer los campos

```java
PilaES<Comando> deshacer
PilaES<Comando> rehacer
```

Ambas pilas contienen referencias a comandos, no caracteres ni descripciones.

### 12.2 Leer ejecutar

Respeta exactamente este orden:

1. Ejecutar el comando.
2. Apilarlo en deshacer.
3. Vaciar rehacer.

La pila rehacer se vacia porque una accion nueva crea una rama distinta de la
historia. No se vacia antes de ejecutar porque el comando podria fallar.

### 12.3 Leer deshacer

Si no hay comandos, retorna `false`. Si hay uno:

```text
deshacer --desapilar--> comando --deshacer--> rehacer
```

El mismo objeto comando cambia de pila.

### 12.4 Leer rehacer

El movimiento es contrario:

```text
rehacer --desapilar--> comando --ejecutar--> deshacer
```

Observa que no llama a `HistorialEdicion.ejecutar`, porque ese metodo vaciaria
la pila de rehacer.

### 12.5 Leer las consultas

`sizeDeshacer()` y `sizeRehacer()` delegan el calculo a las pilas propias.

## Etapa 8: comprobar el historial

### 13. Leer TestHistorial.java

### 13.1 Leer prepararEstadoInicial

Comprueba como se construye `HoX|la`. Estas operaciones se realizan antes de
registrar comandos, por lo que el historial comienza vacio.

### 13.2 Leer main paso a paso

No intentes leer los 12 pasos de una sola vez. Para cada paso anota:

```text
contenido | cantidad deshacer | cantidad rehacer
```

Presta especial atencion a:

- Paso 7: rehacer retorna `false` porque una accion nueva vacio esa pila.
- Paso 10: el borrado se deshace restaurando el caracter guardado.
- Paso 12: deshacer retorna `false` porque no quedan comandos.

### 13.3 Leer verificarPaso

Comprueba contenido y tamaños de ambas pilas despues de cada operacion.

### 13.4 Leer probarPila

Verifica por separado el comportamiento LIFO y la excepcion de pila vacia.

## Etapa 9: ejecutar el proyecto

### 14. Leer y ejecutar probar.bat

No necesitas ejecutar manualmente `javac` para el estudio normal. Usa:

```powershell
.\probar.bat
```

El script:

1. Crea `build`.
2. Compila alli todos los `.java`.
3. Ejecuta `TestBufferGap`.
4. Ejecuta `TestHistorial`.
5. Elimina los `.class` y la carpeta temporal.

Mientras observas la salida, relaciona cada bloque con los metodos de prueba que
acabaste de leer.

## Etapa 10: profundizar y preparar la defensa

### 15. Leer Guia_Didactica.md

Utiliza esta guia despues de haber recorrido el codigo. Sirve para reforzar:

- Conceptos de Java.
- Representacion fisica y logica.
- Simulaciones detalladas.
- Excepciones.
- Tabla de desplazamientos.
- Restricciones cumplidas.

Si una parte del codigo no quedo clara, busca su seccion correspondiente en la
guia y vuelve despues al archivo `.java`.

### 16. Leer Preguntas_Defensa.md

No memorices las respuestas palabra por palabra. Intenta responder cada
pregunta sin mirar y luego compara tu explicacion.

Marca cada pregunta asi:

```text
[ ] No puedo responderla
[~] Puedo responderla con ayuda
[x] Puedo explicarla y dar un ejemplo
```

### 17. Revisar Declaracion_de_Honor.txt

Este archivo no contiene logica de programacion. Revisalo al final para
completar correctamente integrantes y datos pendientes antes de entregar.

## Rutina recomendada de estudio

### Primera lectura

```text
README
Excepciones del buffer
BufferGap
TestBufferGap
```

Objetivo: comprender completamente la primera parte.

### Segunda lectura

```text
Comando
Tres comandos concretos
Excepcion de pila
PilaES
HistorialEdicion
TestHistorial
```

Objetivo: comprender deshacer y rehacer.

### Tercera lectura

```text
Ejecutar probar.bat
Leer Guia_Didactica
Practicar Preguntas_Defensa
```

Objetivo: relacionar teoria, codigo y resultados.

## Lista final de comprobacion

Antes de considerar que comprendes el TP, deberias poder hacer todo esto:

- Dibujar las tres zonas de `BufferGap`.
- Calcular `size()` con valores concretos.
- Traducir cinco indices logicos a fisicos.
- Simular insertar, borrar y mover en papel.
- Explicar cuando crece el arreglo.
- Indicar que operaciones cuentan desplazamientos.
- Diferenciar las tres excepciones propias.
- Explicar que guarda cada comando.
- Dibujar una `PilaES` con tres nodos.
- Simular un comando pasando entre ambas pilas.
- Explicar por que una accion nueva elimina rehacer.
- Recorrer los 12 pasos del historial.
- Ejecutar las pruebas y reconocer cada salida.
- Explicar por que no se usan colecciones del API.
