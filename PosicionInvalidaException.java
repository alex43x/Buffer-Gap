/*
 * Grupo: <GRUPO>
 * Tarea: TP1 - U1
 * Integrantes:
 * - Portillo Sales, Angel Zacarias - CIC: <CIC_1> - Seccion: <SECCION_1>
 * - Llamosas Maidana, Alex Giovanni - CIC: <CIC_2> - Seccion: <SECCION_2>
 * - <APELLIDO_3>, <NOMBRE_3> - CIC: <CIC_3> - Seccion: <SECCION_3>
 */

/**
 * Es unchecked porque solicitar un indice o movimiento fuera del rango valido
 * representa normalmente un error de programacion del codigo llamador.
 */
public class PosicionInvalidaException extends RuntimeException {
    public PosicionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
