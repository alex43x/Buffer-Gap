/*
 * Grupo: <GRUPO>
 * Tarea: TP1 - U1
 * Integrantes:
 * - Portillo Sales, Angel Zacarias - CIC: <CIC_1> - Seccion: <SECCION_1>
 * - Llamosas Maidana, Alex Giovanni - CIC: <CIC_2> - Seccion: <SECCION_2>
 * - <APELLIDO_3>, <NOMBRE_3> - CIC: <CIC_3> - Seccion: <SECCION_3>
 */

public class HistorialEdicion {
    private final PilaES<Comando> deshacer = new PilaES<Comando>();
    private final PilaES<Comando> rehacer = new PilaES<Comando>();

    public void ejecutar(Comando comando) {
        comando.ejecutar();
        deshacer.apilar(comando);
        while (!rehacer.estaVacia()) {
            rehacer.desapilar();
        }
    }

    public boolean deshacer() {
        if (deshacer.estaVacia()) {
            return false;
        }
        Comando comando = deshacer.desapilar();
        comando.deshacer();
        rehacer.apilar(comando);
        return true;
    }

    public boolean rehacer() {
        if (rehacer.estaVacia()) {
            return false;
        }
        Comando comando = rehacer.desapilar();
        comando.ejecutar();
        deshacer.apilar(comando);
        return true;
    }

    public int sizeDeshacer() {
        return deshacer.size();
    }

    public int sizeRehacer() {
        return rehacer.size();
    }
}
