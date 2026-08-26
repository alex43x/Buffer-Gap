/*
  Grupo: g_ts9
  Tarea: TP1 - U1
  Integrantes:
    - Angel Zacarias Portillo Sales   - CI Nº: 7259245 - Seccion: TS
    - Alex Giovanni Llamosas Maidana  - CI Nº: 5631704 - Seccion: TS
 */
public class ComandoMoverCursor implements Comando {
    private final BufferGap<Character> buffer;
    private final int delta;//Guarda porque su opuesto permite regresar a la posicion anterior.

    public ComandoMoverCursor(BufferGap<Character> buffer, int delta) {
        this.buffer = buffer;
        this.delta = delta;
    }

    @Override
    public void ejecutar() {
        buffer.moverCursor(delta);
    }

    @Override
    public void deshacer() {
        buffer.moverCursor(-delta);
    }

    @Override
    public String descripcion() {
        return "Mover cursor " + delta;
    }
}
