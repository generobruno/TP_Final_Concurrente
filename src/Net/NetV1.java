package Net;

import Data.*;
import Logic.*;

/**
 * Clase NetV1
 * Implementación de la red de petri original
 * con Deadlock
 */
public class NetV1 {
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
        int cantP = 15;

        // Matriz de Incidencia : 15 plazas (filas), 12 transiciones (columnas)
        int[][] incidenceMatrix = data.getMatrix("data/IncidenceV1.xls", cantT, cantP);
        // Marcado inicial
        int[] initialMarks = data.getMarks("data/MarkingV1.xls", cantP);

        // Crea la Red
        pn.createNet(cantT, cantP, incidenceMatrix, initialMarks, pn);

        // Imprimimos información sobre los estados
        int[] activityPlaces = {1,2,3,4,5,6,8,9,10};
        pn.printAllStates(activityPlaces);

        // Muestra la red
        PetrinetGUI.displayPetrinet(pn);
    }
}
