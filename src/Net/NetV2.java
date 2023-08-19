package Net;

import Data.*;
import Logic.*;

/**
 * Clase NetV2
 * Implementación de la red de petri modificada
 * sin Deadlock
 */
public class NetV2 {
    public static void main(String[] args) {

        // Red de Petri
        Petrinet pn = new Petrinet("MyNet");
        // Data para la red
        Data data = new Data();
        // Logger
        Logger log = new Logger();

        // Cantidad de Transiciones
        int cantT = 12;
        // Cantidad de Plazas
        int cantP = 18;

        // Matriz de Incidencia : 18 plazas (filas), 12 transiciones (columnas)
        int[][] incidenceMatrix = data.getMatrix("data/IncidenceV2.xls", cantT, cantP);
        // Marcado inicial
        int[] initialMarks = data.getMarks("data/MarkingV2.xls", cantP);

        // Crea la Red
        pn.createNet(cantT, cantP, incidenceMatrix, initialMarks, pn);

        // Asignando Nombre a las plazas de Control
        pn.setPlaceName("P16", "Cs1");
        pn.setPlaceName("P17", "Cs2");
        pn.setPlaceName("P18", "Cs3");

        // Imprimimos información sobre los estados
        int[] activityPlaces = {1,2,3,4,5,6,8,9,10};
        pn.printAllStates(activityPlaces);

        // Mostrar la Red
        PetrinetGUI.displayPetrinet(pn);
    }
}
