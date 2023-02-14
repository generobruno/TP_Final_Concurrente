package Executor;

import Logic.Petrinet;
import Monitor.Monitor;

/**
 * Clase Segment
 *  Segmento para ejecutar transiciones de la Red.
 */
public class Segment implements Runnable{

    // Monitor en donde se ejecuta el segmento
    private final Monitor monitor;
    // Red de Petri simulada
    private final Petrinet petrinet;
    // Transiciones a disparar
    private final int[] transitions;
    // Boolean para terminar la ejecución
    private boolean finish;

    /**
     * Constructor de la clase
     * @param monitor Monitor de concurrencia que ejecuta el segmento
     * @param pn Red de Petri
     * @param transiciones Conjunto de transiciones
     */
    public Segment(Monitor monitor, Petrinet pn, int[] transiciones) {
        this.monitor = monitor;
        this.petrinet = pn;
        this.transitions = transiciones;
        finish = false;
    }

    /**
     * Método fireSegment
     * Dispara las transiciones asociadas al segmento
     */
    public void fireSegment() {
        for (int transition : this.transitions) {
            //System.out.printf("Thread %s entering monitor - (T%d)\n",Thread.currentThread().getName(),transitions[i]);
            monitor.fireTransition(transition);
            //System.out.printf("Transition %d FIRED\n", transitions[i]);
        }
    }

    /**
     * Método finishExec
     * Utilizado para terminar la ejecución del segmento
     */
    public void finishExec() {
        System.out.printf("%s Finished\n",Thread.currentThread().getName());
        finish = true;
    }

    /**
     * Método run
     * Dispara todas transiciones asociadas al segmento
     */
    @Override
    public void run() {

        while(!finish) {
            for (int transition : this.transitions) {

                monitor.fireTransition(transition);

                // Si se dispararon más de [invMax] invariantes, se detiene la ejecución
                if ((monitor.isFinished())) {
                    System.out.printf("%s Finished\n", Thread.currentThread().getName());
                    finish = true;
                    break;
                } else {
                    System.out.printf("Invariantes disparadas: %d\n", monitor.getInvFired());
                }
            }
        }

    }

}
