/*
 * Grupo: <GRUPO>
 * Tarea: TP1 - U1
 * Integrantes:
 * - Portillo Sales, Angel Zacarias - CIC: <CIC_1> - Seccion: <SECCION_1>
 * - Llamosas Maidana, Alex Giovanni - CIC: <CIC_2> - Seccion: <SECCION_2>
 * - <APELLIDO_3>, <NOMBRE_3> - CIC: <CIC_3> - Seccion: <SECCION_3>
 */

public class ComandoBorrar implements Comando {
    private final BufferGap<Character> buffer;
    private Character caracterBorrado;

    public ComandoBorrar(BufferGap<Character> buffer) {
        this.buffer = buffer;
    }

    @Override
    public void ejecutar() {
        try {
            caracterBorrado = buffer.borrar();
        } catch (BufferVacioException e) {
            throw new IllegalStateException("No se puede ejecutar el borrado", e);
        }
    }

    @Override
    public void deshacer() {
        if (caracterBorrado == null) {
            throw new IllegalStateException("El comando de borrado no fue ejecutado");
        }
        buffer.insertar(caracterBorrado);
    }

    @Override
    public String descripcion() {
        return "Borrar caracter anterior al cursor";
    }
}
