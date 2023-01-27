package Net;

import Data.Data;
import Logic.*;

/**
 * Clase NetV2
 * Implementación de la red de petri modificada
 * sin Deadlock
 */
public class NetV2 {
    public static void main(String[] args) {
        /* TODO Crear Monitor para simular la red */

        /* LO DEJO POR LAS DUDAS
        Petrinet pn = new Petrinet("MyNet");

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
        Place cs1 = pn.place("cs1",3);
        Place cs2 = pn.place("cs2",4);
        Place cs3 = pn.place("cs3",6);

        // Create arcs
        // Cs1
        Arc a1 = pn.arc("a1", cs1, t1);
        Arc a2 = pn.arc("a2", cs1, t10);
        Arc a3 = pn.arc("a3", t11, cs1);
        Arc a4 = pn.arc("a4", t2, cs1);
        Arc a5 = pn.arc("a5", t3, cs1);
        // Cs2
        Arc a6 = pn.arc("a6", t10, cs2);
        Arc a7 = pn.arc("a7",cs2, t2);
        Arc a8 = pn.arc("a8",cs2, t3);
        Arc a9 = pn.arc("a9", t4, cs2);
        Arc a10 = pn.arc("a10", t5, cs2);
        Arc a11 = pn.arc("a11", cs2, t9);
        // Cs3
        Arc a12 = pn.arc("a12", cs3, t1);
        Arc a13 = pn.arc("a13", t11, cs3);
        Arc a14 = pn.arc("a14", t4, cs3);
        Arc a15 = pn.arc("a15", t5, cs3);
        Arc a16 = pn.arc("a16", cs3, t9);
        // P1
        Arc a17 = pn.arc("a17", t1, p1);
        Arc a18 = pn.arc("a18", p1, t2);
        Arc a19 = pn.arc("a19", p1, t3);
        // P2
        Arc a20 = pn.arc("a20", t2, p2);
        Arc a21 = pn.arc("a21", p2, t4);
        // P3
        Arc a22 = pn.arc("a22", t3, p3);
        Arc a23 = pn.arc("a23", p3, t5);
        // P4
        Arc a24 = pn.arc("a24", t4, p4);
        Arc a25 = pn.arc("a25", p4, t6);
        // P5
        Arc a26 = pn.arc("a26", t5, p5);
        Arc a27 = pn.arc("a27", p5, t7);
        // P6
        Arc a28 = pn.arc("a28", t6, p6);
        Arc a29 = pn.arc("a29", t7, p6);
        Arc a30 = pn.arc("a30", p6, t8);
        // P7
        Arc a31 = pn.arc("a31", p7, t1);
        Arc a32 = pn.arc("a32", t8, p7);
        // P8
        Arc a33 = pn.arc("a33", p8, t10);
        Arc a34 = pn.arc("a34", t9, p8);
        // P9
        Arc a35 = pn.arc("a35", t10, p9);
        Arc a36 = pn.arc("a36", p9, t11);
        // P10
        Arc a37 = pn.arc("a37",t11 , p10);
        Arc a38 = pn.arc("a38", p10, t12);
        // P11
        Arc a39 = pn.arc("a39", t12, p11);
        Arc a40 = pn.arc("a40", p11, t9);
        // P12
        Arc a41 = pn.arc("a41", p12, t1);
        Arc a42 = pn.arc("a42", p12, t11);
        Arc a43 = pn.arc("a43", t12, p12);
        Arc a44 = pn.arc("a44", t2, p12);
        Arc a45 = pn.arc("a45", t3, p12);
        // P13
        Arc a46 = pn.arc("a46", p13, t10);
        Arc a47 = pn.arc("a47", t11, p13);
        Arc a48 = pn.arc("a48", p13, t2);
        Arc a49 = pn.arc("a49", p13, t3);
        Arc a50 = pn.arc("a50", t4, p13);
        Arc a51 = pn.arc("a51", t5, p13);
        // P14
        Arc a52 = pn.arc("a52", t10, p14);
        Arc a53 = pn.arc("a53", p14, t4);
        Arc a54 = pn.arc("a54", p14, t5);
        Arc a55 = pn.arc("a55", t6, p14);
        Arc a56 = pn.arc("a56", t7, p14);
        Arc a57 = pn.arc("a57", p14, t9);
        // P15
        Arc a58 = pn.arc("a58", p15, t6);
        Arc a59 = pn.arc("a59", p15, t7);
        Arc a60 = pn.arc("a60", t8, p15);
        */

        // Red de Petri
        Petrinet pn = new Petrinet("MyNet");
        // Data para la red
        Data data = new Data();

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

        PetrinetGUI.displayPetrinet(pn);
    }
}
