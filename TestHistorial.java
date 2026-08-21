/*
 * Grupo: <GRUPO>
 * Tarea: TP1 - U1
 * Integrantes:
 * - Portillo Sales, Angel Zacarias - CIC: <CIC_1> - Seccion: <SECCION_1>
 * - Llamosas Maidana, Alex Giovanni - CIC: <CIC_2> - Seccion: <SECCION_2>
 * - <APELLIDO_3>, <NOMBRE_3> - CIC: <CIC_3> - Seccion: <SECCION_3>
 */

public class TestHistorial {
    public static void main(String[] args) {
        BufferGap<Character> buffer = prepararEstadoInicial();
        HistorialEdicion historial = new HistorialEdicion();

        System.out.println("TRAZA DEL HISTORIAL");
        System.out.println("Inicial: " + buffer + " | desh.=0 reh.=0");

        historial.ejecutar(new ComandoInsertar(buffer, '!'));
        verificarPaso(1, "Insertar '!'", buffer, historial, "HoX!|la", 1, 0);
        historial.ejecutar(new ComandoInsertar(buffer, '?'));
        verificarPaso(2, "Insertar '?'", buffer, historial, "HoX!?|la", 2, 0);

        boolean resultado = historial.deshacer();
        verificar(resultado, "El paso 3 debe retornar true");
        verificarPaso(3, "deshacer() -> " + resultado, buffer, historial, "HoX!|la", 1, 1);
        resultado = historial.deshacer();
        verificar(resultado, "El paso 4 debe retornar true");
        verificarPaso(4, "deshacer() -> " + resultado, buffer, historial, "HoX|la", 0, 2);
        resultado = historial.rehacer();
        verificar(resultado, "El paso 5 debe retornar true");
        verificarPaso(5, "rehacer() -> " + resultado, buffer, historial, "HoX!|la", 1, 1);

        historial.ejecutar(new ComandoMoverCursor(buffer, -4));
        verificarPaso(6, "MoverCursor -4", buffer, historial, "|HoX!la", 2, 0);
        resultado = historial.rehacer();
        verificar(!resultado, "El paso 7 debe retornar false");
        verificarPaso(7, "rehacer() -> " + resultado, buffer, historial, "|HoX!la", 2, 0);
        resultado = historial.deshacer();
        verificar(resultado, "El paso 8 debe retornar true");
        verificarPaso(8, "deshacer() -> " + resultado, buffer, historial, "HoX!|la", 1, 1);

        historial.ejecutar(new ComandoBorrar(buffer));
        verificarPaso(9, "Borrar", buffer, historial, "HoX|la", 2, 0);
        resultado = historial.deshacer();
        verificar(resultado, "El paso 10 debe retornar true");
        verificarPaso(10, "deshacer() -> " + resultado, buffer, historial, "HoX!|la", 1, 1);
        resultado = historial.deshacer();
        verificar(resultado, "El paso 11 debe retornar true");
        verificarPaso(11, "deshacer() -> " + resultado, buffer, historial, "HoX|la", 0, 2);
        resultado = historial.deshacer();
        verificar(!resultado, "El paso 12 debe retornar false");
        verificarPaso(12, "deshacer() -> " + resultado, buffer, historial, "HoX|la", 0, 2);

        probarPila();
        System.out.println("Todas las pruebas del historial finalizaron correctamente.");
    }

    private static BufferGap<Character> prepararEstadoInicial() {
        BufferGap<Character> buffer = new BufferGap<Character>();
        buffer.insertar('H');
        buffer.insertar('o');
        buffer.insertar('l');
        buffer.insertar('a');
        buffer.moverCursor(-2);
        buffer.insertar('X');
        verificar(buffer.toString().equals("HoX|la"), "Estado inicial incorrecto");
        return buffer;
    }

    private static void verificarPaso(int numero, String operacion, BufferGap<Character> buffer,
            HistorialEdicion historial, String esperado, int deshacer, int rehacer) {
        verificar(buffer.toString().equals(esperado), "Contenido incorrecto en el paso " + numero);
        verificar(historial.sizeDeshacer() == deshacer,
                "Pila deshacer incorrecta en el paso " + numero);
        verificar(historial.sizeRehacer() == rehacer,
                "Pila rehacer incorrecta en el paso " + numero);
        System.out.println(numero + ". " + operacion + " | " + buffer + " | desh.="
                + historial.sizeDeshacer() + " reh.=" + historial.sizeRehacer());
    }

    private static void probarPila() {
        PilaES<Integer> pila = new PilaES<Integer>();
        verificar(pila.estaVacia() && pila.size() == 0, "La pila debe iniciar vacia");
        pila.apilar(1);
        pila.apilar(2);
        verificar(pila.tope() == 2, "El tope debe ser el ultimo elemento apilado");
        verificar(pila.desapilar() == 2 && pila.desapilar() == 1, "La pila debe ser LIFO");
        boolean fallo = false;
        try {
            pila.tope();
        } catch (PilaVaciaException e) {
            fallo = true;
        }
        verificar(fallo, "Consultar el tope vacio debe lanzar PilaVaciaException");
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}
