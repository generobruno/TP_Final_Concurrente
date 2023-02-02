package Monitor;

import Data.Logger;
import Logic.Petrinet;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Clase Monitor
 *  Se encarga de manejar la ejecución de la Red de Petri
 */
public class Monitor {

    // Red de Petri asociada
    private final Petrinet petrinet;
    // Lock para la exclusión mutua
    private final ReentrantLock mutex;
    // Cola de condiciones
    private final Condition waitQueue;
    // Mapa de transiciones de invariantes
    private final Map<Integer,Integer> invariants;
    // Cantidad máxima de invariantes a disparar
    private final int maxInv;
    // Cantidad de invariantes disparadas
    private int invFired;
    // Logger
    private Logger log;

    /**
     * Constructor de la clase
     * @param pn Red de Petri
     */
    public Monitor(Petrinet pn, Map<Integer,Integer> inv, int invAm, Logger logger) {
        petrinet = pn;
        invariants = inv;
        maxInv = invAm;
        invFired = 0;
        log = logger;
        mutex = new ReentrantLock();
        waitQueue = mutex.newCondition();
    }

    /**
     * Método fireTransition
     * Método que ejecutan los hilos del sistema cuando quieren disparar una transición
     * de la red de Petri asociada. Los segmentos de ejecución utilizan este método.
     * @param t Transición a disparar
     */
    public void fireTransition(int t) {

        /*
         * Básicamente, vienen distintos hilos a intentar disparar las transiciones.
         * Si la transición que quiere disparar un hilo específico está deshabilitada,
         * entonces dicho hilo debe ir a una COLA DE ESPERA (Wait_i), y cuando la
         * transición vuelve a habilitarse, se le da una señal para que vuelva a
         * intentar dispararla. Los hilos compiten por su ejecución, es decir, compiten
         * para ver quien puede disparar su transición. Esta es la situación SIN POLÍTICA.
         */

        // Entra un thread y toma el lock
        mutex.lock();

        try {

            // Mientras la transición a disparar esté deshabilitada y si no se completaron los invariantes, espera
            while(!petrinet.isEnabled(t) && !isFinished()) {
                waitQueue.await();
            }

            // Dispara la transición cuando se habilita
            petrinet.fireTransition(t,log);

            // Incrementamos el valor de la transición disparada
            incrementInvariant(t);

            // Luego de disparar despierta a los hilos que estaban esperando una habilitación
            // signalAll() ya que un disparo puede habilitar a más de una transición
            waitQueue.signalAll();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            // Finalmente, libera el lock
            mutex.unlock();

        }

    }

    /**
     * Método incrementInvariant
     * Luego de disparar una transición se incrementa el invariante
     * al que pertenece esta transición.
     * @param t Transición disparada
     */
    public synchronized void incrementInvariant(int t) {
        // Incrementamos el valor de la transición disparada
        invariants.put(t,invariants.get(t)+1);

        // TODO REVISAR
        int[] inv1 = {9,10,11,12};
        int[] inv2 = {1,3,5,7,8};
        int[] inv3 = {1,2,4,6,8};

        if(checkInvariant(inv1))
            invFired++;
        if(checkInvariant(inv2))
            invFired++;
        if(checkInvariant(inv3))
            invFired++;

    }

    /**
     * Método checkInvariant
     * Chequea que un invariante de transición se haya disparado
     * por completo
     * @param inv Array de transiciones que componen el invariante
     * @return True en caso de que se haya completado una transición
     */
    public synchronized boolean checkInvariant(int[] inv) {
        int aux = 0;
        for(int i : inv) {
            if(invariants.get(i) >= 1){
                aux++;
            }
        }

        if(aux == inv.length) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Método getInvFired
     * @return Invariantes disparadas
     */
    public synchronized int getInvFired() {
        return invFired;
    }

    /**
     * Método isFinished
     * @return True en caso de que se hayan disparado las transiciones necesarias
     */
    public synchronized boolean isFinished() {
        return (invFired>=maxInv);
    }

}