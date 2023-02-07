package Monitor;

import Data.Logger;
import Logic.Petrinet;
import Logic.Place;

import java.util.Arrays;
import java.util.List;
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
    private final Map<Integer,Integer> invariantsFired;
    // Lista con los invariantes de transición
    final List<int[]> invariantsT;
    // Mapa con los invariantes de plaza
    final Map<String[],Integer> invariantsP;
    // Cantidad máxima de invariantes a disparar
    private final int maxInv;
    // Cantidad de invariantes disparadas
    private int invFired;
    // Disparos de los distintos invariantes
    private final int[] amountForInv;
    // Disparos de las distintas transiciones
    private final int[] amountForTrans;
    // Logger
    private final Logger log;

    /**
     * Constructor de la clase
     * @param pn Red de Petri
     */
    public Monitor(Petrinet pn, Map<Integer,Integer> inv, int maxInv, Logger logger) {
        // Asociamos red de Petri
        petrinet = pn;
        // Mapa de invariantes y cantidad de ejecuciones
        invariantsFired = inv;
        // Invariantes de transición
        this.invariantsT = pn.getInvariantsT();
        // Invariantes de plaza
        this.invariantsP = pn.getInvariantsP();
        // Número de invariantes a disparar
        this.maxInv = maxInv;
        // Array con disparos por invariante
        amountForInv = new int[invariantsT.size()];
        // Array con disparos por transición
        amountForTrans = new int[inv.size()];
        // Invariantes disparadas
        invFired = 0;
        // Logger
        log = logger;
        // Lock para exclusión mutua
        mutex = new ReentrantLock();
        // Cola de condiciones
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
            amountForTrans[(t-1)]++;

            // Chequeamos que se cumplan los Invariantes de Plaza
            checkPlaceInv(t,log);

            // Incrementamos el valor de la transición disparada
            incrementInvariant(t);

            // Luego de disparar despierta a los hilos que estaban esperando una habilitación
            // signalAll() ya que un disparo puede habilitar a más de una transición
            waitQueue.signalAll();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IllegalMonitorStateException e) {
            e.printStackTrace();
        } finally {

            // Finalmente, libera el lock
            mutex.unlock();

        }

    }

    /**
     * Método incrementInvariant
     * Luego de disparar una transición se incrementa el invariante
     * al que pertenece esta transición y la cantidad de disparos
     * por invariante respectiva.
     * @param t Transición disparada
     */
    public void incrementInvariant(int t) {
        // Incrementamos el valor de la transición disparada
        invariantsFired.put(t, invariantsFired.get(t)+1);

        if(!isFinished()) {
            for(int i = 0; i < invariantsT.size(); i++) {
                if(checkInvariant(invariantsT.get(i))) {
                    invFired++;
                    amountForInv[i]++;
                }
            }
        }

    }

    /**
     * Método checkInvariant
     * Chequea que un invariante de transición se haya disparado
     * por completo
     * @param inv Array de transiciones que componen el invariante
     * @return True en caso de que se haya completado una transición
     */
    public boolean checkInvariant(int[] inv) {
        int aux = 0;
        for(int i : inv) {
            if(invariantsFired.get(i) >= 1){
                aux++;
            }
        }

        if(aux == inv.length) {
            // Reiniciamos los puntos del invariante
            for (int j : inv) {
                invariantsFired.put(j, 0);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Método checkPlaceInv
     * Chequea que se cumplan los invariantes de plaza de la red
     * @param log Logger para escribir la información
     */
    public void checkPlaceInv(int t, Logger log) {

        log.logP("Disparo de T"+ t);
        for(String[] s : invariantsP.keySet()) {
            if(!sumInvP(s,invariantsP.get(s),log)) {
                log.logP("Error en Invariante de plaza.");
            }
        }
        log.logP("\n\n");

    }

    /**
     * Método sumInvP
     * Suma los tokens de los invariantes de plazas señalados en el array
     * invP y compara el resultado con "num".
     * @param invP Array de plazas del invariante
     * @param num Número al que debe igualarse la suma de sus tokens
     * @return True en caso de ser igual a num
     */
    public boolean sumInvP(String[] invP, int num, Logger log) {
        int sum = 0;

        log.logP("\nInvariantes de Plaza: ");
        for (String s : invP) {
            for (Place p : petrinet.getPlaces()) {
                if (p.getName().equals(s)) {
                    log.logP(p.getName() + " ");
                    sum += p.getTokens();
                }
            }
        }

        if(sum == num) {
            log.logP("= " + sum);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Método getInvFired
     * @return Invariantes disparadas
     */
    public int getInvFired() {
        return invFired;
    }

    /**
     * Método isFinished
     * @return True en caso de que se hayan disparado las transiciones necesarias
     */
    public boolean isFinished() {
        return (invFired>=maxInv);
    }

    /**
     * Método printAmountForInv
     * Imprime información sobre los invariantes y las transiciones disparadas
     */
    public void printInfo(List<int[]> invariants) {
        // Información de invariantes
        System.out.printf("\nTotal de invariantes disparados: %d\n",getInvFired());
        System.out.print("Carga en los invariantes:\n");
        for(int i = 0; i < amountForInv.length; i++) {
            float percentage = (float) amountForInv[i]/getInvFired();
            System.out.printf("Invariante %d: Disparado %d veces ( %3.2f %% )\n", (i+1), amountForInv[i], (percentage*100));
        }

        System.out.println();

        // Información de transiciones
        int totalTrans = Arrays.stream(amountForTrans).sum();
        System.out.printf("Total de Transiciones disparadas: %d\n",totalTrans);
        for(int i = 0; i < amountForTrans.length; i++) {
            float percentage = (float) amountForTrans[i]/totalTrans;
            System.out.printf("Transición %d: Disparada %d veces ( %3.3f %% )\n", (i+1), amountForTrans[i], (percentage*100));
        }

        System.out.println();

        // Transiciones por invariante
        System.out.println("Total de transiciones por invariante:");
        for(int i = 0; i < invariants.size(); i++) {
            System.out.printf("Invariante %d: ( ",(i+1));
            int sum = 0;
            for(int j : invariants.get(i)) {
                System.out.printf("T%d ",j);
                sum += amountForTrans[(j-1)];
            }
            System.out.printf(") \n%d transiciones disparadas.\n", sum);
        }

    }

}