/*
  Grupo: g_ts9
  Tarea: TP1 - U1
  Integrantes:
    - Angel Zacarias Portillo Sales   - CIC: 7259245 - Seccion: TS
    - Alex Giovanni Llamosas Maidana  - CIC: 5631704 - Seccion: TS
 */
public class ComandoBorrar implements Comando {
    private final BufferGap<Character> buffer;
    private Character caracterBorrado;
    public ComandoBorrar(BufferGap<Character> buffer) {
        this.buffer = buffer;
    }
    /* Borrado y conserva el caracter eliminado para poder restaurarlo. */
    @Override
    public void ejecutar() {
        try {
            caracterBorrado = buffer.borrar();//Guarda en caso de restaurar
        } catch (BufferVacioException e) {
            throw new IllegalStateException("No se puede ejecutar el borrado", e);
        }
    }
    /* Restaura exactamente el caracter que fue eliminado. */
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
