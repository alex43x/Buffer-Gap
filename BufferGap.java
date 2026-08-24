/*
  Grupo: g_ts9
  Tarea: TP1 - U1
  Integrantes:
    - Angel Zacarias Portillo Sales   - CIC: 7259245 - Seccion: TS
    - Alex Giovanni Llamosas Maidana  - CIC: 5631704 - Seccion: TS
 */

import java.util.Iterator;

/* Secuencia generica almacenada en un arreglo con un hueco movil. */
public class BufferGap<E> implements Iterable<E> {
    private static final int TAM_INICIAL = 16;

    private E[] datos;
    private int inicioHueco;
    private int finHueco;
    private long desplazamientos;

    @SuppressWarnings("unchecked")
    /*Inicializa un buffer vacio donde todo el arreglo es el hueco */
    public BufferGap() {
        datos = (E[]) new Object[TAM_INICIAL];
        inicioHueco = 0;
        finHueco = TAM_INICIAL;
        desplazamientos = 0;
    }
    /*Inserta un elemento en el cursor ocupando la primera celda del hueco. */
    public void insertar(E obj) {
        if (inicioHueco == finHueco) {
            crecer();
        }
        datos[inicioHueco] = obj;
        inicioHueco++;
    }
    /* Duplica la capacidad y conserva el hueco en la posicion actual del cursor.*/
    @SuppressWarnings("unchecked")
    private void crecer() {
        E[] nuevosDatos = (E[]) new Object[datos.length * 2];
        int elementosDerecha = datos.length - finHueco;
        int nuevoFinHueco = nuevosDatos.length - elementosDerecha;
    /*Cada elemento copiado cuenta como desplazamiento*/
        for (int i = 0; i < inicioHueco; i++) {
            nuevosDatos[i] = datos[i];
            desplazamientos++;
        }
        for (int i = 0; i < elementosDerecha; i++) {
            nuevosDatos[nuevoFinHueco + i] = datos[finHueco + i];
            desplazamientos++;
        }
        datos = nuevosDatos;
        finHueco = nuevoFinHueco;
    }
    /*Elimina y retorna el elemento inmediatamente anterior al cursor*/
    public E borrar() throws BufferVacioException {
        if (inicioHueco == 0) {
            throw new BufferVacioException("No hay un elemento antes del cursor");
        }
        inicioHueco--;
        return datos[inicioHueco];
    }
    /*Mueve el cursor trasladando los elementos necesarios a traves del hueco*/
    public void moverCursor(int delta) {
        long nuevaPosicion = (long) inicioHueco + delta;
        if (nuevaPosicion < 0 || nuevaPosicion > size()) {
            throw new PosicionInvalidaException("Movimiento de cursor fuera de rango: " + delta);
        }

        while (delta < 0) {
            datos[finHueco - 1] = datos[inicioHueco - 1];
            inicioHueco--;
            finHueco--;
            desplazamientos++;
            delta++;
        }
        while (delta > 0) {
            datos[inicioHueco] = datos[finHueco];
            inicioHueco++;
            finHueco++;
            desplazamientos++;
            delta--;
        }
    }
    /*Retorna el elemento de la posicion logica indicada sin recorrer el arreglo.*/
    public E get(int index) {
        validarIndice(index);
        return datos[indiceFisico(index)];
    }
    /*Reemplaza el elemento de la posicion logica indicada y retorna el valor anterior.*/
    public E set(E obj, int index) {
        validarIndice(index);
        int fisico = indiceFisico(index);
        E anterior = datos[fisico];
        datos[fisico] = obj;
        return anterior;
    }
    /*Traduce un indice logico al indice fisico real, teniendo en cuenta el hueco.*/
    private void validarIndice(int index) {
        if (index < 0 || index >= size()) {
            throw new PosicionInvalidaException("Indice fuera de rango: " + index);
        }
    }
    /*Traduce un indice logico al indice fisico real, teniendo en cuenta el hueco.*/
    private int indiceFisico(int index) {
        if (index < inicioHueco) {
            return index;
        }
        return index + (finHueco - inicioHueco);
    }
    /*Retorna la posicion logica actual del cursor.*/
    public int posicionCursor() {
        return inicioHueco;
    }
    /*Retorna la cantidad de elementos almacenados.*/
    public int size() {
        return datos.length - (finHueco - inicioHueco);
    }
    /*Retorna la capacidad actual del arreglo interno.*/
    public int capacidad() {
        return datos.length;
    }
    /*Retorna la cantidad acumulada de desplazamientos fisicos.*/
    public long desplazamientos() {
        return desplazamientos;
    }
    /*Reinicia el contador de desplazamientos. */
    public void reiniciarDesplazamientos() {
        desplazamientos = 0;
    }
    /*Retorna el contenido en orden logico usando '|' para marcar el cursor.*/
    @Override
    public String toString() {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            if (i == inicioHueco) {
                resultado.append('|');
            }
            resultado.append(get(i));
        }
        if (inicioHueco == size()) {
            resultado.append('|');
        }
        return resultado.toString();
    }
    /*Retorna un iterador que recorre los elementos en orden logico, omitiendo el hueco. */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int indiceLogico = 0;

            @Override
            public boolean hasNext() {
                return indiceLogico < size();
            }

            @Override
            public E next() {
                return get(indiceLogico++);
            }
        };
    }
}
