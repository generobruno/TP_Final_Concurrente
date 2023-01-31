package Monitor;

import Logic.Petrinet;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {

    private Petrinet petrinet;
    private ReentrantLock mutex;  // TODO REENTRANTLOCK O SEMAPHORE?
    private Condition[] waitQueue;

    /**
     * Constructor de la clase
     * @param pn Red de Petri
     */
    public Monitor(Petrinet pn) {
        petrinet = pn;
        mutex = new ReentrantLock();
        waitQueue = new Condition[18]; // TODO 18 o 12?
        for(int i = 0; i < waitQueue.length; i++) {
            waitQueue[i] = mutex.newCondition();
        }
    }

    /**
     * Método fireTransition
     * Método que ejecutan los hilos del sistema cuando quieren disparar una transición
     * de la red de Petri asociada. Los segmentos de ejecución utilizan este método.
     * @param t
     */
    public void fireTransition(int t) {

        /**
         * Basicamente, vienen distintos hilos a intentar disparar las transiciones.
         * Si la transicion que quiere disparar un hilo especifico esta deshabilitada,
         * entonces dicho hilo debe ir a una COLA DE ESPERA (Wait_i) , y cuando la
         * transición vuelve a habilitarse, se le da una señal para que vuelva a
         * intentar dispararla. Los hilos compiten por su ejecución, es decir, compiten
         * para ver quien puede disparar su transición. Esta es la situación SIN POLITICA.
         */

        try {
            mutex.lock();


            while(!petrinet.isEnabled(t)) {
                waitQueue[t-1].await();
            }


            petrinet.fireTransition(t);


            for(int i = 0; i < 18; i++) {
                waitQueue[i].signalAll();
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {


            mutex.unlock();


        }

    }

}

    /*
       TODO
        Asignar a la clase los atributos:
            1. Red de Petri
            2. Mutex (ReentrantLock o Semaphore)
            3. Estado del sistema (Distintos valores de la matriz de marcado)
            4. Eventos del sistema (Transiciones disparadas)
        Ademas, los métodos:
            1. Agregar Cola de Eventos (Events) a las transiciones de la red. La cola es un contador
            que se incrementa al llegar un evento (?) y se decrementa cuando la transición asociada se dispara.
            Para que la transición esté sensibilizada se necesita que su cola tenga al menos 1 evento.
            CADA TRANSICIÓN OPERA COMO UNA VARIABLE DE CONDICIÓN (?).
            2. Luego de que esto funcione, agregar semánticas temporales [alfa_i, beta_i].
     */
