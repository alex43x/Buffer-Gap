/*
 * Grupo: <GRUPO>
 * Tarea: TP1 - U1
 * Integrantes:
 * - Portillo Sales, Angel Zacarias - CIC: <CIC_1> - Seccion: <SECCION_1>
 * - Llamosas Maidana, Alex Giovanni - CIC: <CIC_2> - Seccion: <SECCION_2>
 * - <APELLIDO_3>, <NOMBRE_3> - CIC: <CIC_3> - Seccion: <SECCION_3>
 */

/**
 * Es checked porque borrar en cursor 0 puede ocurrir durante el uso normal
 * del TAD y el llamador puede prever y manejar esa situacion.
 */
public class BufferVacioException extends Exception {
    public BufferVacioException(String mensaje) {
        super(mensaje);
    }
}
