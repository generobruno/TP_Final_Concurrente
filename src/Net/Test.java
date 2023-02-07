package Net;

import Data.*;
import Logic.*;

public class Test {
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

        // Cambia el nombre de la última plaza y transición
        pn.setPlaceName("P15", "LAST");
        pn.setTransitionName("T12","LAST");

        // Mostramos info de la Red
        pn.printIncidence();
        System.out.println("");
        pn.printMarks();
        System.out.println("");
        pn.printVectorE();
        System.out.println("");
        //System.out.printf("T8 is enabled?: %b \n", pn.isEnabled(8));
        //System.out.printf("T9 is enabled?: %b \n", pn.isEnabled(9));

        // Disparamos una transición
        //System.out.println(pn);
        pn.fireTransition(8);
        pn.fireTransition(9);
        //System.out.println(pn);

        // Mostramos info de la Red Actualizada
        pn.printMarks();
        System.out.println("");
        pn.printVectorE();
        System.out.println("");
        //System.out.printf("T8 is enabled?: %b \n", pn.isEnabled(8));
        //System.out.printf("T9 is enabled?: %b \n", pn.isEnabled(9));

        // Disparamos hasta deadlock
        pn.fireContinuously(log,false);

        // Imprimimos información sobre los estados
        int[] activityPlaces = {1,2,3,4,5,6,8,9,10};
        pn.printAllStates(activityPlaces);

        // Muestra la red
        PetrinetGUI.displayPetrinet(pn);

    }

}
