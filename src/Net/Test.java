package Net;

import Data.*;
import Logic.*;

import java.util.ArrayList;
import java.util.List;

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

        // Crea la Red
        pn.createNet(cantT, cantP, incidenceMatrix, initialMarks, pn);

        // Cambia el nombre de la última plaza y transición
        pn.setPlaceName("P15", "LAST");
        pn.setTransitionName("T12","LAST");

        pn.printIncidence();
        System.out.println("");
        pn.printMarks();

        // Muestra la red
        PetrinetGUI.displayPetrinet(pn);

    }

}
