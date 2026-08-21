/*
 * Grupo: <GRUPO>
 * Tarea: TP1 - U1
 * Integrantes:
 * - Portillo Sales, Angel Zacarias - CIC: <CIC_1> - Seccion: <SECCION_1>
 * - Llamosas Maidana, Alex Giovanni - CIC: <CIC_2> - Seccion: <SECCION_2>
 * - <APELLIDO_3>, <NOMBRE_3> - CIC: <CIC_3> - Seccion: <SECCION_3>
 */

/** Pila generica propia implementada mediante nodos enlazados. */
public class PilaES<E> {
    private class Nodo {
        private E dato;
        private Nodo next;

        Nodo(E dato, Nodo next) {
            this.dato = dato;
            this.next = next;
        }
    }

    private Nodo tope;
    private int cantidad;

    public void apilar(E dato) {
        tope = new Nodo(dato, tope);
        cantidad++;
    }

    public E desapilar() {
        if (estaVacia()) {
            throw new PilaVaciaException("No se puede desapilar una pila vacia");
        }
        E dato = tope.dato;
        tope = tope.next;
        cantidad--;
        return dato;
    }

    public E tope() {
        if (estaVacia()) {
            throw new PilaVaciaException("Una pila vacia no tiene tope");
        }
        return tope.dato;
    }

    public boolean estaVacia() {
        return cantidad == 0;
    }

    public int size() {
        return cantidad;
    }
}
