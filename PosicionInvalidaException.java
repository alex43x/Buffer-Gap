/*
  Grupo: g_ts9
  Tarea: TP1 - U1
  Integrantes:
    - Angel Zacarias Portillo Sales   - CIC: 7259245 - Seccion: TS
    - Alex Giovanni Llamosas Maidana  - CIC: 5631704 - Seccion: TS
 */
/* Es unchecked porque solicitar un indice o movimiento fuera del rango valido
   representa normalmente un error de programacion del codigo llamador. */
public class PosicionInvalidaException extends RuntimeException {
    public PosicionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
