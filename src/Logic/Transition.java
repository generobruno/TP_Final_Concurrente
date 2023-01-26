package Logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase Transition.
 *  Clase que representa una transición.
 */
public class Transition extends PetrinetObject {
    // Lista de arcos de entrada
    private List<Arc> incoming = new ArrayList<Arc>();
    // Lista de arcos de salida
    private List<Arc> outgoing = new ArrayList<Arc>();

    /**
     * Constructor de la clase
     * @param name Nombre de la transición
     */
    protected Transition(String name) {
        super(name);
    }

    /**
     * Método canFire
     * Una transición solo puede dispararse si tiene arcos
     * conectados.
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