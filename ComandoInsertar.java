/*
 * Grupo: <GRUPO>
 * Tarea: TP1 - U1
 * Integrantes:
 * - Portillo Sales, Angel Zacarias - CIC: <CIC_1> - Seccion: <SECCION_1>
 * - Llamosas Maidana, Alex Giovanni - CIC: <CIC_2> - Seccion: <SECCION_2>
 * - <APELLIDO_3>, <NOMBRE_3> - CIC: <CIC_3> - Seccion: <SECCION_3>
 */

public class ComandoInsertar implements Comando {
    private final BufferGap<Character> buffer;
    private final char caracter;

    public ComandoInsertar(BufferGap<Character> buffer, char caracter) {
        this.buffer = buffer;
        this.caracter = caracter;
    }

    @Override
    public void ejecutar() {
        buffer.insertar(caracter);
    }

    @Override
    public void deshacer() {
        try {
            buffer.borrar();
        } catch (BufferVacioException e) {
            throw new IllegalStateException("No se puede deshacer la insercion", e);
        }
    }

    @Override
    public String descripcion() {
        return "Insertar '" + caracter + "'";
    }
}
