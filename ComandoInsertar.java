/*
  Grupo: g_ts9
  Tarea: TP1 - U1
  Integrantes:
    - Angel Zacarias Portillo Sales   - CIC: 7259245 - Seccion: TS
    - Alex Giovanni Llamosas Maidana  - CIC: 5631704 - Seccion: TS
 */
/*Ingresa un caracter y permite revertir con un borrado. */
public class ComandoInsertar implements Comando {
    private final BufferGap<Character> buffer;
    private final char caracter;

    public ComandoInsertar(BufferGap<Character> buffer, char caracter) {
        this.buffer = buffer;
        this.caracter = caracter;
    }
    /* Inserta el caracter almacenado en la posicion actual del cursor. */
    @Override
    public void ejecutar() {
        buffer.insertar(caracter);
    }
    /* Revierte la insercion eliminando el caracter previamente insertado. */
    @Override
    public void deshacer() {
        try {
            buffer.borrar();
        } catch (BufferVacioException e) {
            throw new IllegalStateException("No se puede deshacer la insercion", e);
        }
    }
    /* Retorna la descripcion de la operacion realizada. */
    @Override
    public String descripcion() {
        return "Insertar '" + caracter + "'";
    }
}
