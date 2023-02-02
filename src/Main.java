import Data.*;
import Executor.Segment;
import Logic.Petrinet;
import Monitor.Monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase Main
 *  Utilizada para simular el funcionamiento de la red.
 */
public class Main {
    public static void main(String[] args) {

        /*
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

        // Creamos un array con los invariantes de transición y un mapa para contar su ejecución TODO MEJORAR
        int[] invT = {1,2,3,4,5,6,7,8,9,10,11,12};
        int maxInv = 1000;
        // El mapa contiene <Key, Value> = <Invariante, Ejecuciones>
        Map<Integer,Integer> invariants = new HashMap<>();
        for (int k : invT) {
            invariants.put(k, 0);
        }


        /*
         * Ahora creamos el Monitor para el manejo de la concurrencia, junto con
         * los distintos segmentos encargados del disparo de los invariantes
         * de la red.
         */

        // Creamos el monitor y le asignamos la red
        Monitor monitor = new Monitor(pn, invariants, maxInv, log);

        // Cantidad de hilos y de segmentos resultado del análisis de la red
        int threadAmount = 18;
        int segmentsAmount = 5;

        // Creamos un array de segmentos y de sus respectivos hilos de ejecución
        Thread[] threads = new Thread[threadAmount];
        Segment[] segments = new Segment[segmentsAmount];
        int[] segThreads = {4,2,4,4,4};

        // Chequeo de cantidad de hilos
        if(Arrays.stream(segThreads).sum() != threadAmount) {
            System.out.print("Error en la Cantidad de Hilos o cantidad de Hilos por segmento.\n");
            System.exit(1);
        }

        // Creamos un array para las transiciones de cada segmento
        int[] seg1T = {9,10,11,12};
        int[] seg2T = {1};
        int[] seg3T = {3,5,7};
        int[] seg4T = {2,4,6};
        int[] seg5T = {8};

        // Creamos los segmentos y les asignamos sus transiciones
        segments[0] = new Segment(monitor,pn,seg1T);
        segments[1] = new Segment(monitor,pn,seg2T);
        segments[2] = new Segment(monitor,pn,seg3T);
        segments[3] = new Segment(monitor,pn,seg4T);
        segments[4] = new Segment(monitor,pn,seg5T);

        // Creamos los hilos por segmento
        int idx = 0;
        for(int i = 0; i < segmentsAmount; i++) {
            for(int j = 0; j < segThreads[i]; j++) {
                threads[idx] = new Thread(segments[i], "Seg" + (i+1) + " - Thread" + (j+1) );
                idx++;
            }
        }

        // Lanzamos los hilos
        for(int i = 0; i < threadAmount; i++) {
            threads[i].start();
        }

        /*
         * Finalmente, ejecutamos el script en python para chequear la correcta
         * ejecución de los invariantes de transición
         */

        // El hilo Main espera a que los demás hilos terminen
        for(int i = 0; i < threadAmount; i++) {
            try{
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // El hilo main dispara transiciones hasta volver el estado inicial, para el script
        pn.fireContinuously(log);

        // Ejecutamos el script
        try {
            // Corriendo el batch file
            Process p = Runtime.getRuntime().exec("scripts/invCheck.bat");

            // Guardamos su resultado en stdin
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            // Guardamos el stderr
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // Leemos la salida del programa
            System.out.println("Resultado del Script: ");
            String s;
            while((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // Imprimimos el error del script, en caso de que ocurra
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException e) {
            System.out.println("Error: ");
            e.printStackTrace();
        }

        // Imprimimos el estado final de la red
        if(!Arrays.equals(pn.getInitialState(),pn.getMarkings())) {
            System.out.println("ERROR");
        }
        System.out.print("Petri Net State\n");
        for(int i : pn.getMarkings()){
            System.out.printf("%d - ",i);
        }


    }
}

/*
TODO
    Hay que encontrar una forma de que la red vuelva al estado original para que el script funcione, o encontrar
    un regex que funciona aunque no se vuelva al estado inicial. Para lo primero hay que revisar la inicialización de
    el atributo initialState en la clase PetriNet y después compararlo con el estado actual en Segment.run().
    Además ver si es necesario que los métodos de monitor ejecutados por los segmentos que no usan el mutex
    (ej. Monitor.isFinished()) deberían ser synchronized.
 */