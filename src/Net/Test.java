package Net;

import Data.*;
import Logic.*;
public class Test {
    public static void main(String[] args) {
        // Red de Petri
        Petrinet pn = new Petrinet("MyNet");
        // Data para la red
        Data data = new Data();

        // Cantidad de Transiciones
        int cantT = 12;
        // Cantidad de Plazas
        int cantP = 15;

        // Matriz de Incidencia : 15 plazas (filas), 12 transiciones (columnas)
        int[][] incidenceMatrix = data.getMatrix("data/IncidenceV1.xls", cantT, cantP);
        // Marcado inicial
        int[] initialMarks = data.getMarks("data/MarkingV1.xls", cantP);

        // Crea las transiciones
        pn.generateTransitions(cantT, pn);

        // Crea las plazas y asigna sus tokens
        pn.generatePlaces(initialMarks,cantP,pn);

        // Asigna la matriz de incidencia
        pn.AssignIncidence(incidenceMatrix, cantP, cantT);

        // Muestra la red
        PetrinetGUI.displayPetrinet(pn);
    }

}
