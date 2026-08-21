# Preguntas para la defensa oral

## BufferGap

### 1. ¿Que es un BufferGap?

Es una secuencia almacenada en un arreglo que mantiene un hueco en la posicion
del cursor. Las inserciones cercanas al cursor utilizan directamente ese hueco.

### 2. ¿Cual es el invariante principal?

El arreglo siempre esta dividido en tres zonas contiguas:

```text
[elementos anteriores][hueco][elementos posteriores]
```

La izquierda ocupa `0 .. inicioHueco-1`, el hueco ocupa
`inicioHueco .. finHueco-1` y la derecha ocupa `finHueco .. capacidad-1`.

### 3. ¿Por que no existe un campo cursor?

Porque la posicion logica del cursor siempre es `inicioHueco`. Otro campo
duplicaria el estado y podria quedar desincronizado.

### 4. ¿Como se calcula el tamaño?

Se resta el tamaño del hueco a la capacidad:

```java
datos.length - (finHueco - inicioHueco)
```

### 5. ¿Como funciona insertar?

Escribe el elemento en `datos[inicioHueco]` e incrementa `inicioHueco`. No
desplaza los elementos posteriores. Si el hueco esta agotado, primero duplica
la capacidad.

### 6. ¿Como funciona borrar?

Se comporta como Backspace: reduce `inicioHueco` y devuelve el elemento que
estaba inmediatamente antes del cursor. No desplaza otros elementos.

### 7. ¿Por que borrar no coloca null en la celda?

Porque el contenido fisico del hueco no tiene significado. Los limites
`inicioHueco` y `finHueco` determinan que celdas contienen elementos validos.

### 8. ¿Que sucede al mover el cursor hacia la izquierda?

Por cada posicion, el ultimo elemento anterior al hueco se copia al extremo
derecho del hueco. Luego disminuyen `inicioHueco` y `finHueco`.

### 9. ¿Que sucede al mover el cursor hacia la derecha?

Por cada posicion, el primer elemento posterior al hueco se copia al comienzo
del hueco. Luego aumentan `inicioHueco` y `finHueco`.

### 10. ¿Mover el cursor cambia la secuencia?

No. Cambia la distribucion fisica y la posicion del hueco, pero conserva el
orden logico y el tamaño.

### 11. ¿Como se traduce un indice logico a uno fisico?

Si el indice esta antes del cursor, ambos indices coinciden. En caso contrario,
se suma el tamaño del hueco:

```java
if (index < inicioHueco) {
    return index;
}
return index + (finHueco - inicioHueco);
```

### 12. ¿Por que get y set no recorren el arreglo?

Porque calculan directamente la celda fisica mediante la formula de
traduccion. `set` reemplaza el valor y devuelve el anterior sin mover el hueco.

### 13. ¿Como crece el BufferGap?

Duplica la capacidad, copia manualmente la parte izquierda al comienzo y la
parte derecha al final. El nuevo espacio queda entre ambas partes, manteniendo
el hueco en la posicion logica del cursor.

### 14. ¿Por que no se usa System.arraycopy o Arrays.copyOf?

Porque el enunciado los prohibe. Todas las copias se realizan manualmente con
ciclos.

### 15. ¿Que cuenta como desplazamiento?

Cada elemento existente que cambia de celda al mover el cursor y cada elemento
copiado durante un crecimiento.

### 16. ¿Que operaciones no aumentan el contador?

La insercion normal, el borrado, `get`, `set`, `toString`, la iteracion y las
consultas.

### 17. ¿Como funciona el iterador?

Mantiene un indice logico, consulta `hasNext()` y obtiene cada elemento con
`get(indiceLogico++)`. Por eso recorre la secuencia en orden y omite el hueco.

### 18. ¿Por que next no usa NoSuchElementException?

Porque el TP permite `Iterator`, pero no permite importar otras clases de
`java.util`. Si se usa `next()` fuera de rango, la validacion de `get` produce
`PosicionInvalidaException`.

## Excepciones

### 19. ¿Por que BufferVacioException es checked?

Porque intentar borrar con el cursor en cero puede suceder durante el uso
normal y el llamador puede prever y manejar esa situacion.

### 20. ¿Por que PosicionInvalidaException es unchecked?

Porque solicitar un indice o movimiento fuera de rango normalmente representa
un error de programacion del llamador.

### 21. ¿Por que PilaVaciaException es unchecked?

Porque llamar a `desapilar` o `tope` sin verificar si hay elementos es un uso
invalido de la pila.

## Comandos e historial

### 22. ¿Que ventaja ofrece la interfaz Comando?

Define un contrato comun para ejecutar, deshacer y describir acciones. El
historial puede trabajar con cualquier comando sin conocer sus detalles.

### 23. ¿Que guarda ComandoInsertar?

Guarda la referencia al buffer y el caracter. Ejecuta con `insertar` y deshace
con `borrar`.

### 24. ¿Que guarda ComandoBorrar?

Guarda la referencia al buffer y el caracter efectivamente eliminado. Necesita
ese caracter para poder reinsertarlo al deshacer.

### 25. ¿Que guarda ComandoMoverCursor?

Guarda la referencia al buffer y solamente `delta`. Ejecuta el movimiento con
`delta` y lo deshace con `-delta`.

### 26. ¿Por que los comandos convierten BufferVacioException en IllegalStateException?

La interfaz `Comando` no permite declarar excepciones checked. Ademas, si un
comando correctamente registrado no puede deshacerse, el historial esta en un
estado inconsistente y no es una situacion normal recuperable.

### 27. ¿Como esta implementada PilaES?

Como una lista simplemente enlazada generica. Tiene una clase interna privada
`Nodo`, una referencia al tope y un contador de elementos. Apilar y desapilar
trabajan sobre el frente.

### 28. ¿Por que Nodo es privado e interno?

Porque es un detalle de implementacion de la pila y no debe exponerse a sus
usuarios.

### 29. ¿Por que el historial usa dos pilas?

La pila `deshacer` contiene comandos ejecutados. La pila `rehacer` contiene
comandos que fueron deshechos y pueden ejecutarse nuevamente.

### 30. ¿Que ocurre al ejecutar una accion nueva?

Primero se ejecuta correctamente, luego se apila en `deshacer` y finalmente se
vacia `rehacer`.

### 31. ¿Por que una accion nueva vacia la pila de rehacer?

Porque crea una nueva rama de la historia. Los comandos deshechos pertenecen a
un futuro que ya no corresponde al estado actual.

### 32. ¿Por que no se vacia rehacer antes de ejecutar el comando?

Porque el comando podria fallar. En ese caso no se modifico el documento y la
historia anterior debe conservarse.

### 33. ¿Como funciona deshacer?

Retira el comando mas reciente de `deshacer`, llama a su metodo `deshacer`, lo
apila en `rehacer` y retorna `true`. Si no hay comandos, retorna `false`.

### 34. ¿Como funciona rehacer?

Retira el comando mas reciente de `rehacer`, vuelve a ejecutarlo, lo apila en
`deshacer` y retorna `true`. Si la pila esta vacia, retorna `false`.

### 35. ¿Por que rehacer no llama a HistorialEdicion.ejecutar?

Porque `ejecutar` vacia la pila de rehacer. Rehacer debe trasladar solamente el
comando actual y conservar los demas comandos que todavia pueden rehacerse.

## Pruebas y restricciones

### 36. ¿Que verifica TestBufferGap?

La traza obligatoria, limites, excepciones, crecimiento, contador, `get`, `set`,
iteracion de 100.000 caracteres y la tabla de desplazamientos.

### 37. ¿Que demuestra la tabla de desplazamientos?

Una vez ubicado el hueco, `BufferGap` inserta directamente sin mover la zona
derecha. El arreglo simple debe desplazar sus elementos posteriores en cada
insercion, por lo que su contador aumenta con `n`.

### 38. ¿Que verifica TestHistorial?

Prepara `HoX|la` sin registrar comandos y valida los 12 pasos obligatorios,
incluyendo contenido, resultados booleanos y tamaños de ambas pilas.

### 39. ¿Que demuestra el paso donde rehacer retorna false?

Demuestra que ejecutar un comando nuevo despues de deshacer elimina
correctamente la rama anterior de rehacer.

### 40. ¿Que demuestra deshacer un borrado?

Demuestra que `ComandoBorrar` guardo el caracter eliminado y puede restaurarlo
exactamente.

### 41. ¿Se utilizan colecciones del API de Java?

No. Se utilizan arreglos y una pila enlazada propia. Los unicos imports de
`java.util` son `Iterator` y `Random`, ambos permitidos.

### 42. ¿Como se ejecutan las pruebas sin dejar archivos class?

Con:

```text
probar.bat
```

El script compila dentro de `build`, ejecuta ambas pruebas y elimina los
archivos compilados al finalizar, incluso si ocurre un error.
