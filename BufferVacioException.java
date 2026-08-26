/*
  Grupo: g_ts9
  Tarea: TP1 - U1
  Integrantes:
    - Angel Zacarias Portillo Sales   - CI Nº: 7259245 - Seccion: TS
    - Alex Giovanni Llamosas Maidana  - CI Nº: 5631704 - Seccion: TS
 */
/* Es checked porque borrar con el cursor en 0 puede ocurrir durante el uso normal
   del TAD y el llamador puede prever y manejar esa situacion. */
public class BufferVacioException extends Exception {
    public BufferVacioException(String mensaje) {
        super(mensaje);
    }
}
