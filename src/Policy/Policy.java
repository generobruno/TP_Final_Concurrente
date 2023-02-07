package Policy;

import Monitor.Monitor;

import java.util.List;

/**
 * Clase Política
 *  Se encarga de mantener la carga en los diferentes invariantes de transición balanceada.
 *  Esto es, que el promedio de disparo de las transiciones de los invariantes sean
 *  aproximadamente iguales.
 */
public class Policy {
    // Monitor con la Red de Petri a controlar
    Monitor monitor;
    // Disparos de los distintos invariantes
    private final int[] amountForInv;
    // Disparos de las distintas transiciones
    private final int[] amountForTrans;
    // Lista de invariantes
    List<int[]> invariants;


    /**
     * Constructor de la clase
     * @param mon Monitor de concurrencia
     * @param inv Lista con los invariantes de Transición
     */
    public Policy(Monitor mon, List<int[]> inv) {
        monitor = mon;
        amountForInv = mon.getAmountForInv();
        amountForTrans = mon.getAmountForTrans();
        invariants = inv;
    }

    /**
     * Método decide
     * Se encarga de decidir si una transición de la red puede ser disparada
     * o debe esperar para que se mantenga la carga de los invariantes
     * @param t Transición a disparar
     * @return True en caso de poder ser disparada
     */
    public boolean decide(int t) {
        return false;
    }
}
