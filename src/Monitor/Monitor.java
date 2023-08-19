package Monitor;

import Data.Logger;
import Logic.Petrinet;
import Logic.Place;
import Logic.Transition;
import Policy.Policy;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Clase Monitor
 *  Se encarga de manejar la ejecución de la Red de Petri
 */
public class Monitor {

    // Red de Petri asociada
    private final Petrinet petrinet;
    // Política
    private final Policy policy;


    // Lock para la exclusión mutua
    private final ReentrantLock mutex;
    // Colas de condiciones
    private final Condition[] waitQueue;
    private final Condition coolDownQueue;
    // Variable de condición para habilitadas
    private boolean enabler;
    // Variable de condición para temporizadas
    private int fireTimed;


    // Mapa de transiciones de invariantes
    private final Map<Integer,Integer> invariantsFiredMap;
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
    // Transiciones habilitadas en la red
    private int[] enabledTransitions;
    // Transiciones temporizadas
    private int[] timedTransitions;
    // Procesos esperando a ser habilitados
    private int[] waitingProcesses;
    // Procesos listos para ser ejecutados
    private int[] readyProcesses;


    // Logger
    private final Logger log;

    /**
     * Constructor de la clase
     * @param pn Red de Petri
     */
    public Monitor(Petrinet pn, Map<Integer,Integer> inv, int maxInv, Logger logger) {
        /*
         *          Red de Petri y sus atributos
         */
        // Asociamos red de Petri
        petrinet = pn;
        // Mapa de invariantes y cantidad de ejecuciones
        invariantsFiredMap = inv;

        // Invariantes de transición
        this.invariantsT = pn.getInvariantsT();
        // Invariantes de plaza
        this.invariantsP = pn.getInvariantsP();

        /*
         *          Información del monitor
         */
        // Número de invariantes a disparar
        this.maxInv = maxInv;
        // Array con disparos por invariante
        amountForInv = new int[invariantsT.size()];
        // Array con disparos por transición
        amountForTrans = new int[inv.size()];
        // Invariantes disparadas
        invFired = 0;

        /*
         *                Política
         */
        // Creamos un objeto Política
        policy = new Policy(this,invariantsT);

        /*
         *                Registros
         */
        // Logger
        log = logger;

        /*
         *          Manejo de Concurrencia
         */
        // Lock para exclusión mutua
        mutex = new ReentrantLock();
        // Cola de condiciones para transiciones deshabilitadas
        waitQueue = new Condition[inv.size()];
        for(int i = 0; i < waitQueue.length; i++) {
            waitQueue[i] = mutex.newCondition();
        }
        // Cola de condiciones para transiciones deshabilitadas por tiempo
        coolDownQueue = mutex.newCondition();
        // Variables de Condición
        enabler = true;
        fireTimed = 1;
        // Transiciones habilitadas
        enabledTransitions = new int[inv.size()];
        // Transiciones temporizadas
        timedTransitions = petrinet.getTimeSensibleTransitions();
        // Transiciones esperando
        waitingProcesses = new int[inv.size()];
        // Transiciones listas
        readyProcesses = new int[inv.size()];
    }

    /**
     * Método fireTransition
     * Método que ejecutan los hilos del sistema cuando quieren disparar una transición
     * de la red de Petri asociada. Los segmentos de ejecución utilizan este método.
     * @param t Transición a disparar
     */
    public void fireTransition(int t) {

        // Entra un thread y toma el lock
        mutex.lock();
        // Variables de condición inicialmente habilitadas
        enabler = true;
        fireTimed = 1;

        try {
            // Mientras la variable de condición esté habilitada, un hilo se ejecuta dentro del monitor
            while(enabler && !isFinished()) {

                // Mientras la transición a disparar esté deshabilitada y si no se completaron los invariantes, espera
                while(!petrinet.isEnabled(t) && !isFinished()) {
                    waitQueue[(t-1)].await();
                }

                // Si la transición es temporizada, analizamos su instante de llegada
                if(petrinet.isTimedTransition(t)) {

                    // Revisamos las condiciones temporales
                    fireTimed = checkTimedTransition(t);

                    // En caso de no cumplirse las condiciones temporales o estructurales, espera
                    while((fireTimed != 1 || !petrinet.isEnabled(t)) && !isFinished()) {
                        waitQueue[(t-1)].await();
                        fireTimed = checkTimedTransition(t);
                    }
                }

                // En caso de estar habilitada estructural y temporalmente
                if(fireTimed == 1) {
                    // Disparamos la transición
                    petrinet.fireTransition(t,log);
                    // Actualizamos los datos del monitor
                    updateMonitorVariables(t);
                }

                // Obtenemos las colas de condiciones con procesos esperando
                waitingProcesses = getWaitingProcesses();

                // Obtenemos el vector de sensibilizadas
                enabledTransitions = getEnabledTransitions();

                // Obtenemos las transiciones listas para dispararse
                readyProcesses = getReadyProcesses(enabledTransitions, waitingProcesses);

                // Si hay 1 proceso o más listos, ejecutamos la política
                if(Arrays.stream(readyProcesses).sum() != 0) {
                    int next_transition = policy.decide(readyProcesses);
                    // Se despierta el hilo que decide la política
                    waitQueue[(next_transition-1)].signalAll();
                    // Libera el monitor
                    break;
                } else { // En caso contrario, el hilo sale del monitor
                    enabler = false;
                }

            }

        } catch (InterruptedException | IllegalMonitorStateException e) {
            e.printStackTrace();
        } finally {

            // Cuando haya terminado, libera los hilos que quedaron esperando
            if(isFinished()) {
                for (Condition condition : waitQueue) {
                    condition.signalAll();
                }
            }

            // Finalmente, libera el monitor
            mutex.unlock();

        }

    }

    /**
     * Método updateMonitorVariables
     * Actualiza los distintos valores que mantiene el monitor sobre la ejecución
     * del programa
     * @param t Transición disparada
     */
    public void updateMonitorVariables(int t) {
        // Aumentamos el número de la transición disparada
        amountForTrans[(t-1)]++;

        // Chequeamos que se cumplan los Invariantes de Plaza
        checkPlaceInv(t,log);

        // Incrementamos el valor de la transición disparada
        incrementInvariant(t);
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
        invariantsFiredMap.put(t, invariantsFiredMap.get(t)+1);

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
            if(invariantsFiredMap.get(i) >= 1){
                aux++;
            }
        }

        if(aux == inv.length) {
            // Reiniciamos los puntos del invariante
            for (int j : inv) {
                invariantsFiredMap.put(j, 0);
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
     * Método getWaitingProcesses
     * @return Array con las colas de condiciones que tienen transiciones esperando
     * (1) y las que no tienen ninguna (0).
     */
    public int[] getWaitingProcesses() {
        int[] arr = new int[waitQueue.length];

        for(int i = 0; i < waitQueue.length; i++) {
            if(mutex.hasWaiters(waitQueue[i])) {
                arr[i] = 1;
            } else {
                arr[i] = 0;
            }
        }

        return arr;
    }

    /**
     * Método getEnabledTransitions
     * @return Array con las transiciones habilitadas
     */
    public int[] getEnabledTransitions() {
        // Reiniciamos las transiciones que estaban esperando
        timedTransitions = petrinet.getTimeSensibleTransitions();
        coolDownQueue.signalAll();

        return petrinet.getEnableTransitions();
    }

    /**
     * Método getReadyProcesses
     * @param enabled Transiciones habilitadas
     * @param waiting Procesos esperando para disparar una transición
     * @return Array con las transiciones listas para dispararse
     */
    public int[] getReadyProcesses(int[] enabled, int[] waiting) {
        int[] arr = new int[readyProcesses.length];

        for(int i = 0; i < readyProcesses.length; i++) {
            if(enabled[i] == 1 && waiting[i] == 1) {
                arr[i] = (i+1);
            } else {
                arr[i] = 0;
            }
        }

        return arr;
    }

    /**
     * Método checkTimedTransitions
     * Analiza que hacer dependiendo del tiempo que haya tardado una transición hasta este momento.
     *      1. Caso (time < alfa): Pone la transición a esperar un tiempo
     *      2. Caso (time > beta): Sale del monitor
     *      3. Otros casos: Se dispara la transición correctamente, si no estaba esperando
     * @param t Transición a analizar
     * @return 1 En caso de estar dentro de su ventana de sensibilizado
     *         2 En caso de haber esperado
     *         3 En caso de que se haya superado su tiempo máximo
     */
    public int checkTimedTransition(int t) {

        // Obtenemos la transición y el tiempo que tardó desde que se sensibilizó
        Transition transition = petrinet.getTransitions().get(t-1);
        long time = new Date().getTime() - transition.getTimeStamp();

        // Chequeamos los tiempos
        if(time < transition.getAlfaTime()) { // Llegó ANTES de tiempo

            log.logTimed(transition.getName() + " COOL-DOWN - " + time + "[ms] < " + transition.getAlfaTime() + "[ms]\n");

            // La transición espera un tiempo
            coolDown(t);

            // Volvemos a obtener las transiciones habilitadas en caso de que hayan ocurrido otros disparos
            petrinet.getTransitionsAbleToFire();
            if(!petrinet.isEnabled(t)) { // Si se deshabilitó
                log.logTimed(transition.getName() + " DESHABILITADA después del cool-down\n");
                // Sale del monitor
                return 2;
            }

            // Finalmente va a disparar la transición
            transition.setTimeStamp();
            return 1;

        } else if(time > transition.getBetaTime()) { // Llegó DESPUÉS de tiempo

            log.logTimed(transition.getName() + " TIME-OUT - " + transition.getBetaTime() + "[ms] > " + time + "[ms]\n");
            // Sale del monitor
            return 3;

        } else { // Llegó dentro de su Ventana de Tiempo
            // Si la transición está esperando, sale del monitor
            if(mutex.hasWaiters(coolDownQueue)) {
                log.logTimed("Transición "+ transition.getName() + " WAITING\n");
                return 2;
            } else {
                log.logTimed("Tiempo "+ transition.getName() + " - " + time + "[ms]\n");
                transition.setTimeStamp();
                return 1;
            }
        }

    }

    /**
     * Método coolDown
     * Hace que la transición duerma por un tiempo, en caso de haber llegado
     * antes de alfa
     * @param t Transición que debe dormir
     */
    public void coolDown(int t) {
        // Obtenemos la transición
        Transition transition = petrinet.getTransitions().get(t-1);

        // Indicamos que la transición está esperando
        timedTransitions[(t-1)] = -1;

        // Duerme por un tiempo
        try {
            long timeSleep = (transition.getTimeStamp() + transition.getAlfaTime() - new Date().getTime()) * (-1);
            if(!(coolDownQueue.await(timeSleep, TimeUnit.MILLISECONDS))) {
                log.logTimed(transition.getName() + " despertó después de tiempo\n");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
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
     * Método getAmountForTrans
     * @return Disparos de las distintas transiciones
     */
    public int[] getAmountForTrans() {
        return amountForTrans;
    }

    /**
     * Método printAmountForInv
     * Imprime información sobre los invariantes y las transiciones disparadas
     */
    public void printInfo() {
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

        // Información de transiciones temporizadas
        List<Transition> timedT = new ArrayList<>();
        for(Transition t : petrinet.getTransitions()) {
            if(t.isTimed()) {
                timedT.add(t);
            }
        }
        System.out.printf("\nTransiciones temporizadas: %d ( ", timedT.size());
        int totalTransTimed = 0;
        for(Transition t : timedT) {
            System.out.printf("%s ", t.getName());
            String numberOnly= t.getName().replaceAll("[^0-9]", "");
            totalTransTimed += amountForTrans[(Integer.parseInt(numberOnly)-1)];
        }
        System.out.print(")\n");
        System.out.printf("Total de temporizadas disparadas: %d\n", totalTransTimed);

        System.out.println();

        // Transiciones por invariante
        System.out.println("Total de transiciones por invariante:");
        for(int i = 0; i < invariantsT.size(); i++) {
            System.out.printf("Invariante %d: ( ",(i+1));
            int sum = 0;
            for(int j : invariantsT.get(i)) {
                System.out.printf("T%d ",j);
                sum += amountForTrans[(j-1)];
            }
            System.out.printf(") \n%d transiciones disparadas.\n", sum);
        }

    }

}