/*
  Grupo: g_ts9
  Tarea: TP1 - U1
  Integrantes:
    - Angel Zacarias Portillo Sales   - CIC: 7259245 - Seccion: TS
    - Alex Giovanni Llamosas Maidana  - CIC: 5631704 - Seccion: TS
 */
/* Es unchecked porque operar sin elementos es un uso invalido de la pila. */
public class PilaVaciaException extends RuntimeException {
    public PilaVaciaException(String mensaje) {
        super(mensaje);
    }
}
