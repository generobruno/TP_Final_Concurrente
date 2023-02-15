package Logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Clase Transition.
 *  Clase que representa una transición.
 */
public class Transition extends PetrinetObject {
    // Lista de arcos de entrada
    private final List<Arc> incoming = new ArrayList<>();
    // Lista de arcos de salida
    private final List<Arc> outgoing = new ArrayList<>();
    // Instante de inicio de sensibilizado
    private long alfaTime;
    // Instante de fin de sensibilizado
    private long betaTime;
    // Momento del último disparo
    private long sensitizedTime;
    // Temporizada
    private boolean timed = false;

    /**
     * Constructor de la clase
     * @param name Nombre de la transición
     */
    protected Transition(String name) {
        super(name);
    }

    /**
     * Método setTimeFrame
     * @param alfaTime Tiempo de sensibilizado inicial [ms]
     * @param betaTime Tiempo de sensibilizado final [ms]
     */
    public void setTimeFrame(long alfaTime, long betaTime) {
        this.timed = true;
        this.alfaTime = alfaTime;
        this.betaTime = betaTime;
    }

    /**
     * Método isTimed
     * @return True en caso de estar temporizada
     */
    public boolean isTimed() {
        return timed;
    }

    /**
     * Método setTimeStamp
     * Actualiza el momento de sensibilizado de la transición
     */
    public void setTimeStamp() {
        if(isTimed()) {
            sensitizedTime = new Date().getTime();
        }
    }

    /**
     * Método getTimeStamp
     * @return Tiempo de sensibilizado
     */
    public long getTimeStamp() {
        return sensitizedTime;
    }

    /**
     * Método getAlfaTime
     * @return Instante de sensibilizado alfa
     */
    public long getAlfaTime() {
        if(isTimed()) {
            return alfaTime;
        } else {
            return 0;
        }
    }

    /**
     * Método getBetaTime
     * @return Instante de sensibilizado beta
     */
    public long getBetaTime() {
        if(isTimed()) {
            return betaTime;
        } else {
            return 0;
        }
    }

    /**
     * Método canFire
     * Una transición solo puede dispararse si tiene arcos
     * conectados y si está dentro de su ventana de sensibilizado,
     * en caso de tener una.
     * @return True en caso de poder dispararse
     */
    public boolean canFire() {
        boolean canFire;

        canFire = !(this.isNotConnected());

        for(Arc arc : incoming) {
            canFire = canFire & arc.canFire();
        }

        for(Arc arc : outgoing) {
            canFire = canFire & arc.canFire();
        }

        return canFire;
    }

    /**
     * Método fire
     * Dispara todos los arcos conectados a la
     * transición, si pueden hacerlo
     */
    public void fire() {
        for(Arc arc : incoming) {
            arc.fire();
        }

        for(Arc arc : outgoing) {
            arc.fire();
        }

    }

    /**
     * Método addIncoming
     * Agrega un arco de entrada a una transición
     * @param arc Arco a agregar
     */
    public void addIncoming(Arc arc) {
        this.incoming.add(arc);
    }

    /**
     * Método addOutgoing
     * Agrega un arco de salida a una transición
     * @param arc Arco a agregar
     */
    public void addOutgoing(Arc arc) {
        this.outgoing.add(arc);
    }

    /**
     * Método isNotConnected
     * Chequea si una transición tiene al menos un
     * arco conectado
     * @return True en caso de estar conectada
     */
    public boolean isNotConnected() {
        return incoming.isEmpty() && outgoing.isEmpty();
    }

    /**
     * Método toString
     * @return Información de la transición
     */
    public String toString() {
        return super.toString() +
                (isNotConnected() ? " NOT CONNECTED" : "") +
                (canFire()? " READY TO FIRE" : "");
    }
}