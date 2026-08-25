/*
  Grupo: g_ts9
  Tarea: TP1 - U1
  Integrantes:
    - Angel Zacarias Portillo Sales   - CIC: 7259245 - Seccion: TS
    - Alex Giovanni Llamosas Maidana  - CIC: 5631704 - Seccion: TS
 */
import java.util.Random;
/* Prueba la traza, los limites, el iterador y el conteo de desplazamientos de BufferGap. */
public class TestBufferGap {
    private static final int CANTIDAD_ALEATORIA = 100000;
    private static final int CANTIDAD_INSERCIONES = 10000;

    public static void main(String[] args) throws BufferVacioException {
        probarTrazaObligatoria();
        probarLimitesYCrecimiento();
        probarIteracionAleatoria();
        imprimirTablaDesplazamientos();
        System.out.println("Todas las pruebas de BufferGap finalizaron correctamente.");
    }
    /* Reproduce paso a paso la traza obligatoria indicada en el trabajo. */
    private static void probarTrazaObligatoria() throws BufferVacioException {
        System.out.println("TRAZA OBLIGATORIA");
        BufferGap<Character> buffer = new BufferGap<Character>();
        imprimirEstado(buffer);
        verificarEstado(buffer, "|", 0, 16, 0);

        buffer.insertar('H');
        imprimirEstado(buffer);
        verificarEstado(buffer, "H|", 1, 16, 0);
        buffer.insertar('o');
        imprimirEstado(buffer);
        verificarEstado(buffer, "Ho|", 2, 16, 0);
        buffer.insertar('l');
        imprimirEstado(buffer);
        verificarEstado(buffer, "Hol|", 3, 16, 0);
        buffer.insertar('a');
        imprimirEstado(buffer);
        verificarEstado(buffer, "Hola|", 4, 16, 0);

        buffer.moverCursor(-2);
        imprimirEstado(buffer);
        verificarEstado(buffer, "Ho|la", 2, 14, 2);
        buffer.insertar('X');
        imprimirEstado(buffer);
        verificarEstado(buffer, "HoX|la", 3, 14, 2);

        verificar(buffer.get(4) == 'a', "get(4) debe retornar 'a'");
        imprimirEstado(buffer);
        verificarEstado(buffer, "HoX|la", 3, 14, 2);
        verificar(buffer.borrar() == 'X', "borrar debe retornar 'X'");
        imprimirEstado(buffer);
        verificarEstado(buffer, "Ho|la", 2, 14, 2);
        System.out.println("Traza obligatoria: correcta\n");
    }
    /* Verifica excepciones, limites, crecimiento y funcionamiento de get y set. */
    private static void probarLimitesYCrecimiento() throws BufferVacioException {
        BufferGap<Integer> buffer = new BufferGap<Integer>();
        boolean borrarFallo = false;
        try {
            buffer.borrar();
        } catch (BufferVacioException e) {
            borrarFallo = true;
        }
        verificar(borrarFallo, "Borrar al inicio debe lanzar BufferVacioException");

        for (int i = 0; i < 16; i++) {
            buffer.insertar(i);
        }
        verificar(buffer.capacidad() == 16, "La capacidad inicial debe ser 16");
        buffer.insertar(16);
        verificar(buffer.capacidad() == 32, "La capacidad debe duplicarse");
        verificar(buffer.posicionCursor() == 17, "El cursor debe conservar su posicion logica al crecer");
        verificar(buffer.desplazamientos() == 16, "El crecimiento debe contar 16 copias");

        int anterior = buffer.set(99, 8);
        verificar(anterior == 8 && buffer.get(8) == 99, "set debe reemplazar y retornar el valor anterior");

        boolean getFallo = false;
        boolean setFallo = false;
        boolean moverFallo = false;
        try {
            buffer.get(buffer.size());
        } catch (PosicionInvalidaException e) {
            getFallo = true;
        }
        try {
            buffer.set(0, -1);
        } catch (PosicionInvalidaException e) {
            setFallo = true;
        }
        try {
            buffer.moverCursor(1);
        } catch (PosicionInvalidaException e) {
            moverFallo = true;
        }
        verificar(getFallo && setFallo && moverFallo, "Las operaciones fuera de rango deben fallar");
        System.out.println("Limites y crecimiento: correctos");
    }
    /* Inserta 100000 caracteres aleatorios y verifica cantidad y orden mediante for-each. */
    private static void probarIteracionAleatoria() {
        BufferGap<Character> buffer = new BufferGap<Character>();
        Character[] esperados = new Character[CANTIDAD_ALEATORIA];
        Random random = new Random(2026);
        for (int i = 0; i < esperados.length; i++) {
            char valor = (char) ('a' + random.nextInt(26));
            esperados[i] = valor;
            buffer.insertar(valor);
        }

        int cantidad = 0;
        for (Character valor : buffer) {
            verificar(cantidad < esperados.length, "El iterador produjo elementos adicionales");
            verificar(valor.equals(esperados[cantidad]), "Orden incorrecto en la posicion " + cantidad);
            cantidad++;
        }
        verificar(cantidad == esperados.length, "Cantidad incorrecta durante la iteracion");
        System.out.println("Iteracion de 100000 caracteres: correcta");
    }
    /* Compara los desplazamientos del BufferGap con los de un arreglo de insercion ingenua. */
    private static void imprimirTablaDesplazamientos() {
        System.out.println("\nTABLA DE DESPLAZAMIENTOS");
        System.out.println("N | Desplazamientos BufferGap | Desplazamientos arreglo simple");
        for (int n = 100000; n <= 1000000; n += 100000) {
            BufferGap<Character> buffer = new BufferGap<Character>();
            for (int i = 0; i < n; i++) {
                buffer.insertar('a');
            }
            buffer.moverCursor(-(n - n / 2));
            buffer.reiniciarDesplazamientos();
            for (int i = 0; i < CANTIDAD_INSERCIONES; i++) {
                buffer.insertar('x');
            }

            ArregloSimple arreglo = new ArregloSimple(n + CANTIDAD_INSERCIONES);
            arreglo.cargar(n, 'a');
            arreglo.insertarConsecutivos('x', n / 2, CANTIDAD_INSERCIONES);
            System.out.println(n + " | " + buffer.desplazamientos() + " | "
                    + arreglo.desplazamientos());
        }
    /* BufferGap inserta directamente sobre el hueco ya ubicado. El arreglo
       simple desplaza los elementos posteriores en cada insercion; por eso
        sus desplazamientos aumentan cuando crece n. */
    }

    /* Imprime el estado interno requerido para comprobar la traza. */
    private static void imprimirEstado(BufferGap<Character> buffer) {
        int inicio = buffer.posicionCursor();
        int fin = inicio + buffer.capacidad() - buffer.size();

        System.out.println(buffer
                + "  inicioHueco=" + inicio
                + " finHueco=" + fin
                + " capacidad=" + buffer.capacidad()
                + " desplazamientos=" + buffer.desplazamientos());
    }
    /* Comprueba que el estado del buffer coincida con los valores esperados. */
    private static void verificarEstado(BufferGap<Character> buffer, String contenido,
            int inicio, int fin, long desplazamientos) {
        verificar(buffer.toString().equals(contenido), "Contenido esperado: " + contenido);
        verificar(buffer.posicionCursor() == inicio, "inicioHueco esperado: " + inicio);
        int finCalculado = inicio + buffer.capacidad() - buffer.size();
        verificar(finCalculado == fin, "finHueco esperado: " + fin);
        verificar(buffer.desplazamientos() == desplazamientos,
                "Desplazamientos esperados: " + desplazamientos);
    }
    /* Detiene la prueba si una condicion esperada no se cumple. */
    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
    /* Implementacion ingenua basada en arreglo para comparar desplazamientos. */
    private static class ArregloSimple {
        private final Character[] datos;
        private int cantidad;
        private long desplazamientos;

        ArregloSimple(int capacidad) {
            datos = new Character[capacidad];
        }

        void cargar(int n, char valor) {
            for (int i = 0; i < n; i++) {
                datos[i] = valor;
            }
            cantidad = n;
        }

        void insertar(char valor, int posicion) {
            for (int i = cantidad; i > posicion; i--) {
                datos[i] = datos[i - 1];
                desplazamientos++;
            }
            datos[posicion] = valor;
            cantidad++;
        }

        /* Realiza inserciones consecutivas usando la insercion ingenua del arreglo. */
        void insertarConsecutivos(char valor, int posicion, int repeticiones) {
            for (int i = 0; i < repeticiones; i++) {
                insertar(valor, posicion + i);
            }
        }
        long desplazamientos() {
            return desplazamientos;
        }
    }
}
