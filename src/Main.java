import Data.*;
import Executor.Segment;
import Logic.Petrinet;
import Monitor.Monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Clase Main
 *  Utilizada para simular el funcionamiento de la red.
 */
public class Main {
    public static void main(String[] args) {

        /**
         *  Primero Creamos la Red de Petri, utilizando la matriz de incidencia
         *  y el vector de marcado en la carpeta "data" (V2). Las últimas 3 plazas
         *  son de control (Csi).
         */

        // Red de Petri
        Petrinet pn = new Petrinet("Final Petri Net");
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

        /**
         * Ahora creamos el Monitor para el manejo de la concurrencia, junto con
         * los distintos segmentos encargados del disparo de los invariantes
         * de la red.
         */

        // Creamos el monitor y le asignamos la red
        Monitor monitor = new Monitor(pn);

        // Cantidad de hilos y de segmentos resultado del análisis de la red
        int threadAmount = 18;
        int segmentsAmount = 5;

        // Creamos un array de segmentos y de sus respectivos hilos de ejecución
        Thread threads[] = new Thread[threadAmount];
        Segment segments[] = new Segment[segmentsAmount];
        int[] segThreads = {4,2,4,4,4};

        // Creamos un array para las transiciones de cada segmento
        int[] seg1T = {9,10,11,12};
        int[] seg2T = {1};
        int[] seg3T = {3,5,7};
        int[] seg4T = {2,4,6};
        int[] seg5T = {8};

        // Creamos los segmentos y les asignamos sus transiciones
        // TODO VER IMPLEMENTACION DEL LOG
        segments[0] = new Segment(monitor,pn,seg1T,log);
        segments[1] = new Segment(monitor,pn,seg2T,log);
        segments[2] = new Segment(monitor,pn,seg3T,log);
        segments[3] = new Segment(monitor,pn,seg4T,log);
        segments[4] = new Segment(monitor,pn,seg5T,log);

        // Creamos los hilos por segmento
        int idx = 0;
        for(int i = 0; i < segmentsAmount; i++) {
            for(int j = 0; j < segThreads[i]; j++) {
                threads[idx] = new Thread(segments[i], "Seg" + (i+1) + " - Thread" + (j+1) );
                idx++;
            }
            System.out.println("");
        }

        // Lanzamos los hilos
        for(int i = 0; i < threadAmount; i++) {
            threads[i].start();
        }

        /**
         * Finalmente, ejecutamos el script en python para chequear la correcta
         * ejecución de los invariantes de transición
         */

        /*
        try {
            // Ejecutamos el script TODO Cambiar path
            Process p = Runtime.getRuntime().exec("C:\\Users\\Bruno\\Code\\Python\\chequeo_InvT.py");

            // Guardamos su resultado en stdin
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            // Leemos la salida del programa
            System.out.println("Resultado del Script: ");
            String s;
            while((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException e) {
            System.out.println("Excepción ocurrida: ");
            e.printStackTrace();
        }
        */

    }
}
