/*
  Grupo: g_ts9
  Tarea: TP1 - U1
  Integrantes:
    - Angel Zacarias Portillo Sales   - CIC: 7259245 - Seccion: TS
    - Alex Giovanni Llamosas Maidana  - CIC: 5631704 - Seccion: TS
 */
public class ComandoMoverCursor implements Comando {
    private final BufferGap<Character> buffer;
    private final int delta;

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
