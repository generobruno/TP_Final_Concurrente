package Monitor;

import Logic.Petrinet;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {

    private final Petrinet petrinet;
    private final ReentrantLock mutex;
    private final Condition waitQueue;

    /**
     * Constructor de la clase
     * @param pn Red de Petri
     */
    public Monitor(Petrinet pn) {
        petrinet = pn;
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
         * Basicamente, vienen distintos hilos a intentar disparar las transiciones.
         * Si la transicion que quiere disparar un hilo especifico esta deshabilitada,
         * entonces dicho hilo debe ir a una COLA DE ESPERA (Wait_i) , y cuando la
         * transición vuelve a habilitarse, se le da una señal para que vuelva a
         * intentar dispararla. Los hilos compiten por su ejecución, es decir, compiten
         * para ver quien puede disparar su transición. Esta es la situación SIN POLITICA.
         */

        try {

            // Entra un thread y toma el lock
            mutex.lock();

            // Mientras la transición a disparar esté deshabilitada, espera
            while(!petrinet.isEnabled(t)) {
                waitQueue.await();
            }

            // Dispara la transición cuando se habilita
            petrinet.fireTransition(t);


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

}

    /*
       TODO
         1. Agregar semánticas temporales [alfa_i, beta_i].
     */
