package Net;

import Data.*;
import Logic.*;
public class Test {
    public static void main(String[] args) {
        System.out.println("TESTING");

        Petrinet pn = new Petrinet("MyNet");
        Data data = new Data();

        // Create Transitions
        Transition t1 = pn.transition("t1");
        Transition t2 = pn.transition("t2");
        Transition t3 = pn.transition("t3");
        Transition t4 = pn.transition("t4");
        Transition t5 = pn.transition("t5");
        Transition t6 = pn.transition("t6");
        Transition t7 = pn.transition("t7");
        Transition t8 = pn.transition("t8");
        Transition t9 = pn.transition("t9");
        Transition t10 = pn.transition("t10");
        Transition t11 = pn.transition("t11");
        Transition t12 = pn.transition("t12");

        // Create Places
        Place p1 = pn.place("p1");
        Place p2 = pn.place("p2");
        Place p3 = pn.place("p3");
        Place p4 = pn.place("p4");
        Place p5 = pn.place("p5");
        Place p6 = pn.place("p6");
        Place p7 = pn.place("p7",4);
        Place p8 = pn.place("p8");
        Place p9 = pn.place("p9");
        Place p10 = pn.place("p10");
        Place p11 = pn.place("p11",4);
        Place p12 = pn.place("p12",2);
        Place p13 = pn.place("p13",2);
        Place p14 = pn.place("p14",3);
        Place p15 = pn.place("p15",1);

        // 15 plazas (filas), 12 transiciones (columnas)
        int[][] incidenceMatrix = data.getMatrix("data/IncidenceV2.xls",12,15);

        // Imprimir matriz de incidencia
        for(int i = 0; i < 15; i++) {
            for(int j = 0; j < 12; j++) {
                System.out.print(incidenceMatrix[i][j] + " ");
            }
            System.out.println("");
        }

        pn.AssignIncidence(incidenceMatrix, 15,12);

        PetrinetGUI.displayPetrinet(pn);
    }

}
