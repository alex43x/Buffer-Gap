/*
  Grupo: g_ts9
  Tarea: TP1 - U1
  Integrantes:
    - Angel Zacarias Portillo Sales   - CIC: 7259245 - Seccion: TS
    - Alex Giovanni Llamosas Maidana  - CIC: 5631704 - Seccion: TS
 */
/* Pila generica propia implementada mediante nodos enlazados. */
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
