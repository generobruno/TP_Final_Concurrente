package Logic;

import Data.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase Transition.
 *  Clase que representa una transición.
 */
public class Transition extends PetrinetObject {
    // Lista de arcos de entrada
    private final List<Arc> incoming = new ArrayList<Arc>();
    // Lista de arcos de salida
    private final List<Arc> outgoing = new ArrayList<Arc>();
    // Ventana de sensibilizado por tiempo
    private long timeFrame;
    // Momento del último disparo
    private long firedTime;
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
     * @param timeFrame Tiempo de sensibilizado [ms]
     */
    public void setTimeFrame(long timeFrame) {
        this.timed = true;
        this.timeFrame = (timeFrame); // TODO REVISAR
        this.firedTime = -1;
    }

    /**
     * Método isTimed
     * @return True en caso de estar temporizada
     */
    public boolean isTimed() {
        return timed;
    }

    /**
     * Método canFire
     * Una transición solo puede dispararse si tiene arcos
     * conectados y si está dentro de su ventana de sensibilizado,
     * en caso de tener una.
     * @return True en caso de poder dispararse
     */
    public boolean canFire() {
        boolean canFire = true;

        canFire = !(this.isNotConnected());

        for(Arc arc : incoming) {
            canFire = canFire & arc.canFire();
        }

        for(Arc arc : outgoing) {
            canFire = canFire & arc.canFire();
        }

        if(firedTime == -1) {
            firedTime = System.nanoTime();
        }

        // Revisamos que se esté dentro de la ventana de tiempo TODO REVISAR
        if(canFire && timed) {
            long timeTaken = (System.nanoTime() - firedTime)/1000000;
            canFire = (timeTaken <= timeFrame);

            if(!canFire) {
                System.out.printf("Transición %s Desensibilizada - Tiempo %d\n", this.getName(), timeTaken);
            }
        }

        return canFire;
    }

    /**
     * Método canFire
     * Una transición solo puede dispararse si tiene arcos
     * conectados y si está dentro de su ventana de sensibilizado,
     * en caso de tener una.
     * @param log Log para los tiempos
     * @return True en caso de poder dispararse
     */
    public boolean canFire(Logger log) {
        boolean canFire = true;

        canFire = !(this.isNotConnected());

        for(Arc arc : incoming) {
            canFire = canFire & arc.canFire();
        }

        for(Arc arc : outgoing) {
            canFire = canFire & arc.canFire();
        }

        if(firedTime == -1) {
            firedTime = System.nanoTime();
        }

        // Revisamos que se esté dentro de la ventana de tiempo TODO REVISAR
        if(canFire && timed) {
            long timeTaken = (System.nanoTime() - firedTime)/1000000;
            canFire = (timeTaken <= timeFrame);

            // Registramos información de los tiempos
            if(canFire) {
                log.logTimed("Tiempo "+ this.getName() + " - " + timeTaken + "[ms]\n");
                //log.logTimed(this.getName() + " " + timeTaken + "\n");
            } else {
                log.logTimed(this.getName() + " TIME OUT - " + timeTaken + "[ms] > " + timeFrame + "[ms]\n");
            }

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

        // Momento del disparo TODO REVISAR
        if(timed) {
            this.firedTime = System.nanoTime();
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