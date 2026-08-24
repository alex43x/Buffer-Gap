/*
  Grupo: g_ts9
  Tarea: TP1 - U1
  Integrantes:
    - Angel Zacarias Portillo Sales   - CIC: 7259245 - Seccion: TS
    - Alex Giovanni Llamosas Maidana  - CIC: 5631704 - Seccion: TS
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
