package Logic;

import Data.Logger;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Clase Petrinet.
 *  Representa a la red.
 */
public class Petrinet extends PetrinetObject {

    // Lista de plazas
    private final List<Place> places              = new ArrayList<Place>();
    // Lista de transiciones
    private final List<Transition> transitions    = new ArrayList<Transition>();
    // Lista de arcos
    private final List<Arc> arcs                  = new ArrayList<Arc>();
    // Lista de arcos inhibidores
    private final List<InhibitorArc> inhibitors   = new ArrayList<InhibitorArc>();
    // Invariantes de transición
    List<int[]> invariantsT = new ArrayList<int[]>();
    // Invariantes de plaza
    Map<String[],Integer> invariantsP = new HashMap<>();
    // Matriz de Incidencia
    private int[][] incidenceMatrix;
    // Matriz de Marcado
    private int[] markings;
    // Estado inicial
    private int[] initialState;
    // Transiciones habilitadas
    private int[] enableTransitions;
    // Lista con todos los estados simulados
    final Set<List<Integer>> states = new HashSet<List<Integer>>();

    private static final String nl = "\n";

    /**
     * Método Petrinet
     * Constructor de la clase
     * @param name Nombre de la red
     */
    public Petrinet(String name) {
        super(name);
    }

    /**
     * Método add
     * Agrega un componente a la red
     * @param o PetrinetObject
     */
    public void add(PetrinetObject o) {
        if (o instanceof InhibitorArc) {
            inhibitors.add((InhibitorArc) o);
        } else if (o instanceof Arc) {
            arcs.add((Arc) o);
        } else if (o instanceof Place) {
            places.add((Place) o);
        } else if (o instanceof Transition) {
            transitions.add((Transition) o);
        }
    }

    /**
     * Método getTransitionsAbleToFire
     * Obtiene las transiciones que están habilitadas y las guarda
     * en el vector enableTransitions con un 1 en caso de estarlo,
     * y con un 0 en caso contrario.
     */
    public void getTransitionsAbleToFire() {

        int cantT = transitions.size();
        this.enableTransitions = new int[cantT];

        /* TODO, Hacer que funcione con las matrices?.
            negIncidenceMatrix = Matriz de incidencia negativa
        for(int i = 0; i < cantT; i++) {
            for(int j = 0; j < places.size(); j++) {
                if((markings[j] < negIncidenceMatrix[j][i]) && (negIncidenceMatrix[j][i] != 0)) {
                    this.enableTransitions[i] = 0;
                    break;
                } else {
                    this.enableTransitions[i] = 1;
                }
            }
        }
        */

        for(int i = 0; i < cantT; i++){
            if(transitions.get(i).canFire()) {
                enableTransitions[i] = 1;
            } else {
                enableTransitions[i] = 0;
            }
        }

    }

    /**
     * Método isEnabled
     * Chequea si una transición especifica esta
     * habilitada
     * @param transition Número de transición
     * @return True en caso de estar habilitada
     *         False en caso contrario
     */
    public boolean isEnabled(int transition) {
        return 1 == enableTransitions[(transition - 1)];
    }

    /**
     * Método getEnableTransitions
     * @return enableTransitions
     */
    public int[] getEnableTransitions() {
        return enableTransitions;
    }

    public void printVectorE() {
        System.out.printf("------------- Enabled Transitions ---------------\n");

        for (Transition transition : transitions) {
            System.out.printf("%s ", transition.getName());
        }

        System.out.println("");

        for (int enableTransition : enableTransitions) {
            System.out.printf(" %d ", enableTransition);
        }
    }

    /**
     * Método transition
     * Crea una transición y la agrega a la red
     *
     * @param name Nombre de la transición
     */
    public void transition(String name) {
        Transition t = new Transition(name);
        transitions.add(t);
    }

    /**
     * Método place
     * Crea una plaza y la agrega a la red
     * @param name Nombre de la plaza
     * @return Plaza
     */
    public Place place(String name) {
        Place p = new Place(name);
        places.add(p);
        return p;
    }

    /**
     * Método place
     * Crea una plaza y la agrega a la red
     *
     * @param name    Nombre de la plaza
     * @param initial Tokens iniciales
     */
    public void place(String name, int initial) {
        Place p = new Place(name, initial);
        places.add(p);
    }

    /**
     * Método arc
     * Crea un arco y lo agrega a la red
     * PLACE_TO_TRANSITION
     * @param name Nombre del arco
     * @param p Plaza
     * @param t Transición
     * @return Arco
     */
    public Arc arc(String name, Place p, Transition t) {
        Arc arc = new Arc(name, p, t);
        arcs.add(arc);
        return arc;
    }

    /**
     * Método arc
     * Crea un arco y lo agrega a la red
     * TRANSITION_TO_PLACE
     * @param name Nombre del arco
     * @param p Plaza
     * @param t Transición
     * @return Arco
     */
    public Arc arc(String name, Transition t, Place p) {
        Arc arc = new Arc(name, t, p);
        arcs.add(arc);
        return arc;
    }

    /**
     * Método AssignIncidence
     * Recorre la matriz de izquierda a derecha (columna 0 a cantT) y de
     * arriba hacia abajo (fila 0 a cantP).
     * FILAS        = PLAZAS        = i
     * COLUMNAS     = TRANSICIONES  = j
     * si [i][j] = 1 -> TRANSITION_TO_PLACE
     * si [i][j] = -1 -> PLACE_TO_TRANSITION
     */
    public void AssignIncidence(int[][] incidenceMatrix, int cantP, int cantT) {
        int cantArcs = 1;

        for(int i = 0; i < cantP; i++) {
            for(int j = 0; j < cantT; j++) {
                if(incidenceMatrix[i][j] == 1) {
                    arcs.add(arc("Arc" + cantArcs, transitions.get(j), places.get(i)));
                    cantArcs++;
                } else if (incidenceMatrix[i][j] == -1) {
                    arcs.add(arc("Arc" + cantArcs, places.get(i), transitions.get(j)));
                    cantArcs++;
                }
            }
        }

        this.incidenceMatrix = incidenceMatrix;
    }

    /**
     * Método InhibitorArc
     * Crea un arco inhabilitado y lo agrega a la red
     * @param name Nombre del arco
     * @param p Plaza
     * @param t Transición
     * @return Arco Inhabilitado
     */
    public InhibitorArc inhibitor(String name, Place p, Transition t) {
        InhibitorArc i = new InhibitorArc(name, p, t);
        inhibitors.add(i);
        return i;
    }

    /**
     * Método toString
     * @return Información de la red
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PetriNet ");
        sb.append(super.toString()).append(nl);
        sb.append("---Transitions---").append(nl);
        for (Transition t : transitions) {
            sb.append(t).append(nl);
        }
        sb.append("---Places---").append(nl);
        for (Place p : places) {
            sb.append(p).append(nl);
        }
        return sb.toString();
    }

    /**
     * Método getPlaces
     * @return Lista de plazas
     */
    public List<Place> getPlaces() {
        return places;
    }

    /**
     * Método getTransitions
     * @return Lista de transiciones
     */
    public List<Transition> getTransitions() {
        return transitions;
    }

    /**
     * Método getArcs
     * @return Lista de arcos
     */
    public List<Arc> getArcs() {
        return arcs;
    }

    /**
     * Método getInhibitors
     * @return Lista de arcos inhabilitados
     */
    public List<InhibitorArc> getInhibitors() {
        return inhibitors;
    }

    /**
     * Método generatePlaces
     * Crea la cantidad de plazas determinadas, junto con
     * sus tokens
     * @param initialMarks Vector con los tokens iniciales
     * @param cantP Cantidad de plazas a crear
     * @param pn Red de Petri
     */
    public void generatePlaces(int[] initialMarks, int cantP, Petrinet pn){
        int cant = 1;
        for(int i = 1; i < (cantP+1); i++) {
            pn.place("P" + cant, initialMarks[i-1]);
            cant++;
        }

        this.markings = initialMarks;
    }

    /**
     * Método generateTransitions
     * Crea la cantidad de transiciones determinada
     * @param cantT Cantidad de transiciones
     * @param pn Red de Petri
     */
    public void generateTransitions(int cantT, Petrinet pn){
        int cant = 1;
        for(int i = 1; i < (cantT+1); i++) {
            pn.transition("T" + cant);
            cant++;
        }
    }

    /**
     * Método setPlaceName
     * Cambia el nombre de una plaza (SOLO DESPUÉS DE
     * CREAR LA RED)
     * @param oldName Nombre antigüo
     * @param newName Nombre nuevo
     */
    public void setPlaceName(String oldName, String newName) {
        for(Place p : places) {
            if(p.getName().equals(oldName)) {
                p.setName(newName);
            }
        }
    }

    /**
     * Método setTransitionName
     * Cambia el nombre de una transición (SOLO DESPUÉS DE
     * CREAR LA RED)
     * @param oldName Nombre antigüo
     * @param newName Nombre nuevo
     */
    public void setTransitionName(String oldName, String newName) {
        for(Transition t : transitions) {
            if(t.getName().equals(oldName)) {
                t.setName(newName);
            }
        }
    }

    /**
     * Método createNet
     * Genera la Red de Petri
     * @param t Cantidad de Transiciones
     * @param p Cantidad de Plazas
     * @param incidence Matriz de Incidencia
     * @param state Matriz de Marcado Inicial
     * @param pn Red de Petri
     */
    public void createNet(int t, int p, int[][] incidence, int[] state, Petrinet pn) {
        // Crea las transiciones
        pn.generateTransitions(t, pn);

        // Crea las plazas y asigna sus tokens
        pn.generatePlaces(state, p,pn);

        // Asignamos el estado inicial
        this.initialState = new int[p];
        System.arraycopy(state, 0, initialState, 0, p);

        // Agrega el estado inicial a la lista
        List<Integer> marksClone = cloneStates(state);
        this.states.add(marksClone);

        // Asigna la matriz de incidencia
        pn.AssignIncidence(incidence, p, t);

        // Asigna valores del vector E
        pn.getTransitionsAbleToFire();
    }

    /**
     * Método cloneStates
     * Clona el array de estados del sistema a un ArrayList
     * para poder agregarlo a todos los estados posibles del
     * sistema
     * @param states Array a clonar
     * @return Array clonado como ArrayList
     */
    public List<Integer> cloneStates(int[] states) {
        ArrayList<Integer> clone = new ArrayList<Integer>();

        for (int state : states) {
            clone.add(state);
        }

        return clone;
    }

    /**
     * Método getAllStates
     * @return Todos los estados simulados del sistema
     */
    public Set<List<Integer>> getAllStates() {
        return states;
    }

    /**
     * Método printAllStates
     * Imprime información sobre todos los estados posibles
     * de la red.
     *      1. Número de estados posibles.
     *      2. Marcado máximo (Para plazas de actividades).
     */
    public void printAllStates(int[] activityPlaces) {
        System.out.printf("\n---------- Possible States ----------\n");
        System.out.printf("The number of possible states are: %d \n", states.size());

        ArrayList<Integer> acts = new ArrayList<Integer>();
        for (int activityPlace : activityPlaces) {
            acts.add(activityPlace);
        }

        ArrayList<Integer> sumas = new ArrayList<>();
        for(List<Integer> l : states){
            int sum_i = 0;
            for(int i = 0; i < l.size(); i++){
                if(acts.contains(i)) {
                    sum_i += l.get(i);
                }
            }
            sumas.add(sum_i);
        }

        System.out.printf("El marcado máximo es %d", Collections.max(sumas));
    }

    /**
     * Método printIncidence
     * Imprime por consola la matriz de incidencia de la Red
     */
    public void printIncidence() {
        System.out.printf("------------- Incidence ---------------\n");

        System.out.printf("  ");

        for (Transition transition : transitions) {
            System.out.printf("%s ", transition.getName());
        }

        System.out.println("");

        for(int i = 0; i < places.size(); i++) {
            System.out.printf( "%s ", places.get(i).getName());
            for(int j = 0; j < transitions.size(); j++) {
                System.out.printf(" %d ",incidenceMatrix[i][j]);
            }
            System.out.println("");
        }
    }

    /**
     * Método getIncidenceMatrix
     * @return Matriz de incidencia
     */
    public int[][] getIncidenceMatrix() {
        return incidenceMatrix;
    }

    /**
     * Método printMarks
     * Imprime el marcado de la red
     */
    public void printMarks() {
        System.out.printf("------------- Marking ---------------\n");

        for (Place place : places) {
            System.out.printf("%s ", place.getName());
        }

        System.out.println("");

        for(int i = 0; i < places.size(); i++) {
            System.out.printf( " %d ", markings[i] );
        }
    }

    /**
     * Método getMarkings
     * @return Matriz de marcado
     */
    public int[] getMarkings() {
        return markings;
    }

    /**
     * Método fireTransition
     * Dispara una transición específica que le pasemos como
     * parámetro
     * @param transition Transición a disparar
     */
    public void fireTransition(int transition, Logger log) {
        // Obtenemos la transición
        Transition t = transitions.get(transition - 1);

        if(t.canFire()) {
            // Dispara la transición si es posible
            t.fire();
            // Registramos el disparo
            log.logT(t.getName());
            // Actualiza la red
            updateNet(transition);
        }
    }

    /**
     * Método fireTransition
     * Sin logger
     * @param transition Transición a disparar
     */
    public void fireTransition(int transition) {
        // Obtenemos la transición
        Transition t = transitions.get(transition - 1);

        if(t.canFire()) {
            // Dispara la transición si es posible
            t.fire();
            // Actualiza la red
            updateNet(transition);
        }
    }


    /**
     * Método fireContinuously
     * Dispara transiciones de manera aleatoria hasta que
     * se encuentra con un deadlock y no puede disparar ninguna.
     * Imprime en el log una lista con la secuencia
     * de Transiciones disparadas.
     * @param log Log para las transiciones
     */
    public void fireContinuously(Logger log) {

        boolean deadlock = false;
        while(!deadlock) {
            // Disparamos transición
            int i = ThreadLocalRandom.current().nextInt(1,13);
            Transition t = transitions.get(i-1);
            if(t.canFire()){
                log.logT(t.getName());
            }
            fireTransition(i);
            int[] enabled = getEnableTransitions();
            int test = 0;

            // Chequeamos las t habilitadas
            for(int j = 0; j < transitions.size(); j++){
                if(enabled[j] == 0){
                    test++;
                }
            }

            // Si no hay ninguna habilitada -> Deadlock
            if(test == transitions.size()) {
                System.out.println("---------- DEADLOCK ----------");
                deadlock = true;
            }

            // Luego de volver al estado inicial 10 veces, se detiene
            if(Arrays.equals(getMarkings(), initialState)){
                System.out.printf("------ Vuelta al comienzo -------\n");
                deadlock = true;
            }

        }
    }

    /**
     * Método fireContinuouslyTimed
     * Método similar a fireContinuously, pero con un tiempo
     * limite
     * @param log Log para las transiciones
     * @param time tiempo límite de ejecución [Segundos]
     */
    public void fireContinuouslyTimed(Logger log, int time) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + time*1000;

        boolean deadlock = false;
        while(!deadlock && (System.currentTimeMillis() <  endTime)) {
            // Disparamos transición
            int i = ThreadLocalRandom.current().nextInt(1,13);
            log.logT(transitions.get(i-1).getName());
            fireTransition(i);
            int[] enabled = getEnableTransitions();
            int test = 0;

            // Chequeamos las t habilitadas
            for(int j = 0; j < transitions.size(); j++){
                if(enabled[j] == 0){
                    test++;
                }
            }

            // Si no hay ninguna habilitada -> Deadlock
            if(test == transitions.size()) {
                System.out.println("---------- DEADLOCK ----------");
                deadlock = true;
            }

        }

    }

    /**
     * Método getInitialState
     * @return Estado inicial de la red
     */
    public int[] getInitialState() {
        return initialState;
    }

    /**
     * Método addInvariantsT
     * Asocia a la red una lista con sus invariantes de transición
     * @param invariantsT Lista de invariantes
     */
    public void addInvariantsT(List<int[]> invariantsT) {
        this.invariantsT = invariantsT;
    }

    /**
     * Método getInvariantsT
     * @return Invariantes de transición de la red
     */
    public List<int[]> getInvariantsT() {
        return invariantsT;
    }

    /**
     * Método addInvariantsP
     * Asocia a la red una lista con sus invariantes de plaza
     * @param invariantsP Lista de invariantes
     */
    public void addInvariantsP(Map<String[],Integer> invariantsP) {
        this.invariantsP = invariantsP;
    }

    /**
     * Método getInvariantsP
     * @return Invariantes de plaza de la red
     */
    public Map<String[],Integer> getInvariantsP() {
        return invariantsP;
    }

    /**
     * Método updateNet
     * Función que actualiza los atributos de la red
     * luego de que se haya disparado una transición
     *  1. Actualiza los Marcados
     *  2. Actualiza las transiciones habilitadas
     */
    public void updateNet(int t) {
        // Marcados (Estado del Sistema)
        for(int i = 0; i < places.size(); i++) {
            markings[i] += incidenceMatrix[i][t-1];
        }
        // Agrega el estado a la lista
        List<Integer> marksClone = cloneStates(markings);
        this.states.add(marksClone);
        // Transiciones habilitadas
        getTransitionsAbleToFire();
    }

}