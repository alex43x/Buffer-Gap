/*
  Grupo: g_ts9
  Tarea: TP1 - U1
  Integrantes:
    - Angel Zacarias Portillo Sales   - CI Nº: 7259245 - Seccion: TS
    - Alex Giovanni Llamosas Maidana  - CI Nº: 5631704 - Seccion: TS
 */
/* Define el contrato comun para las operaciones que pueden ejecutarse y deshacerse. */
public interface Comando {
    void ejecutar();
    void deshacer();
    String descripcion();
}
