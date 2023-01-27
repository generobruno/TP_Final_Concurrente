package Logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase Petrinet.
 *  Representa a la red.
 */
public class Petrinet extends PetrinetObject {

    // Lista de plazas
    private List<Place> places              = new ArrayList<Place>();
    // Lista de transiciones
    private List<Transition> transitions    = new ArrayList<Transition>();
    // Lista de arcos
    private List<Arc> arcs                  = new ArrayList<Arc>();
    // Lista de arcos inhibidores
    private List<InhibitorArc> inhibitors   = new ArrayList<InhibitorArc>();

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
     * Devuelva una lista con las transiciones que esten
     * habilitadas
     * @return Transiciones sensibilizadas
     */
    public List<Transition> getTransitionsAbleToFire() {
        ArrayList<Transition> list = new ArrayList<Transition>();

        for(Transition t : transitions) {
            if(t.canFire()) {
                list.add(t);
            }
        }

        return list;
    }

    /**
     * Método transition
     * Crea una transición y la agrega a la red
     * @param name Nombre de la transición
     * @return Transición
     */
    public Transition transition(String name) {
        Transition t = new Transition(name);
        transitions.add(t);
        return t;
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
     * @param name Nombre de la plaza
     * @param initial Tokens iniciales
     * @return Plaza
     */
    public Place place(String name, int initial) {
        Place p = new Place(name, initial);
        places.add(p);
        return p;
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

        for(int i = 0; i < cantP-1; i++) {
            for(int j = 0; j < cantT-1; j++) {
                if(incidenceMatrix[i][j] == 1) {
                    arcs.add(arc("Arc" + cantArcs, transitions.get(j), places.get(i)));
                    cantArcs++;
                } else if (incidenceMatrix[i][j] == -1) {
                    arcs.add(arc("Arc" + cantArcs, places.get(i), transitions.get(j)));
                    cantArcs++;
                }
            }
        }

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
     * Cambia el nombre de una plaza
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
     * Cambia el nombre de una transición
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

}