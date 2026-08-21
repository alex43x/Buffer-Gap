/*
 * Grupo: <GRUPO>
 * Tarea: TP1 - U1
 * Integrantes:
 * - Portillo Sales, Angel Zacarias - CIC: <CIC_1> - Seccion: <SECCION_1>
 * - Llamosas Maidana, Alex Giovanni - CIC: <CIC_2> - Seccion: <SECCION_2>
 * - <APELLIDO_3>, <NOMBRE_3> - CIC: <CIC_3> - Seccion: <SECCION_3>
 */

import java.util.Iterator;

/** Secuencia generica almacenada en un arreglo con un hueco movil. */
public class BufferGap<E> implements Iterable<E> {
    private static final int TAM_INICIAL = 16;

    private E[] datos;
    private int inicioHueco;
    private int finHueco;
    private long desplazamientos;

    @SuppressWarnings("unchecked")
    public BufferGap() {
        datos = (E[]) new Object[TAM_INICIAL];
        inicioHueco = 0;
        finHueco = TAM_INICIAL;
        desplazamientos = 0;
    }

    public void insertar(E obj) {
        if (inicioHueco == finHueco) {
            crecer();
        }
        datos[inicioHueco] = obj;
        inicioHueco++;
    }

    @SuppressWarnings("unchecked")
    private void crecer() {
        E[] nuevosDatos = (E[]) new Object[datos.length * 2];
        int elementosDerecha = datos.length - finHueco;
        int nuevoFinHueco = nuevosDatos.length - elementosDerecha;

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

    public E borrar() throws BufferVacioException {
        if (inicioHueco == 0) {
            throw new BufferVacioException("No hay un elemento antes del cursor");
        }
        inicioHueco--;
        return datos[inicioHueco];
    }

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

    public E get(int index) {
        validarIndice(index);
        return datos[indiceFisico(index)];
    }

    public E set(E obj, int index) {
        validarIndice(index);
        int fisico = indiceFisico(index);
        E anterior = datos[fisico];
        datos[fisico] = obj;
        return anterior;
    }

    private void validarIndice(int index) {
        if (index < 0 || index >= size()) {
            throw new PosicionInvalidaException("Indice fuera de rango: " + index);
        }
    }

    private int indiceFisico(int index) {
        if (index < inicioHueco) {
            return index;
        }
        return index + (finHueco - inicioHueco);
    }

    public int posicionCursor() {
        return inicioHueco;
    }

    public int size() {
        return datos.length - (finHueco - inicioHueco);
    }

    public int capacidad() {
        return datos.length;
    }

    public long desplazamientos() {
        return desplazamientos;
    }

    public void reiniciarDesplazamientos() {
        desplazamientos = 0;
    }

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
