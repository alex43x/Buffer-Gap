/*
  Grupo: g_ts9
  Tarea: TP1 - U1
  Integrantes:
    - Angel Zacarias Portillo Sales   - CI Nº: 7259245 - Seccion: TS
    - Alex Giovanni Llamosas Maidana  - CI Nº: 5631704 - Seccion: TS
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
