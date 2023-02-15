import Data.*;
import Executor.Segment;
import Logic.Petrinet;
import Monitor.Monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Clase Main
 *  Utilizada para simular el funcionamiento de la red.
 */
public class Main {
    public static void main(String[] args) {

        /*
         *  Primero Creamos la Red de Petri, utilizando la matriz de incidencia
         *  y el vector de marcado en la carpeta "data" (V2). Las últimas 3 plazas
         *  son de control (Csi). Además, creamos listas con los invariantes de
         *  plaza y de transición de la red.
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

        // Asignamos tiempo de sensibilizado a las transiciones
        pn.setTransitionTime("T4",1,22300);
        pn.setTransitionTime("T5",1,22800);
        pn.setTransitionTime("T6",1,24800);
        pn.setTransitionTime("T7",1,21900);
        pn.setTransitionTime("T8",1,20000);
        pn.setTransitionTime("T10",1,28200);
        pn.setTransitionTime("T11",1,32850);
        pn.setTransitionTime("T12",1,35500);

        // Creamos un array con los invariantes de transición y un mapa para contar su ejecución
        int[] invT = {1,2,3,4,5,6,7,8,9,10,11,12};
        int maxInv = 1000;
        int invTAmount = 3;
        int invPAmount = 9;
        // El mapa contiene <Key, Value> = <Invariante, Ejecuciones>
        Map<Integer,Integer> invariantsTMap = new HashMap<>();
        for (int k : invT) {
            invariantsTMap.put(k, 0);
        }

        // Invariantes de transición
        List<int[]> invariants = new ArrayList<>();
        int[] invT1 = {9,10,11,12};
        int[] invT2 = {1,3,5,7,8};
        int[] invT3 = {1,2,4,6,8};
        invariants.add(invT1);
        invariants.add(invT2);
        invariants.add(invT3);
        // Asociamos los invariantes a la red
        pn.addInvariantsT(invariants);

        // Chequeo de lista de invariantes T
        if(invariants.size() != invTAmount) {
            System.out.println("Error en la cantidad de invariantes.");
            System.exit(1);
        }

        // Invariantes de plaza
        Map<String[],Integer> invariantsPMap = new HashMap<>();
        int[] invPTokens =  {4,2,2,3,1,4,3,4,6};
        String[] invP1 = {"P10","P11","P8","P9"};               // = 4
        invariantsPMap.put(invP1,invPTokens[0]);
        String[] invP2 = {"P1","P10","P12"};                    // = 2
        invariantsPMap.put(invP2,invPTokens[1]);
        String[] invP3 = {"P13","P2","P3","P9"};                // = 2
        invariantsPMap.put(invP3,invPTokens[2]);
        String[] invP4 = {"P14","P4","P5","P8"};                // = 3
        invariantsPMap.put(invP4,invPTokens[3]);
        String[] invP5 = {"P15","P6"};                          // = 1
        invariantsPMap.put(invP5,invPTokens[4]);
        String[] invP6 = {"P1","P2","P3","P4","P5","P6","P7"};  // = 4
        invariantsPMap.put(invP6,invPTokens[5]);
        String[] invP7 = {"P1","P9","Cs1"};                     // = 3
        invariantsPMap.put(invP7,invPTokens[6]);
        String[] invP8 = {"P2","P3","P8","Cs2"};                // = 4
        invariantsPMap.put(invP8,invPTokens[7]);
        String[] invP9 = {"P1","P2","P3","P8","P9","Cs3"};      // = 6
        invariantsPMap.put(invP9,invPTokens[8]);
        // Asociamos los invariantes a la red
        pn.addInvariantsP(invariantsPMap);

        // Chequeo de lista de invariantes P
        if(invariantsPMap.size() != invPAmount) {
            System.out.println("Error en la cantidad de invariantes.");
            System.exit(1);
        }

        /*
         * Ahora creamos el Monitor para el manejo de la concurrencia, junto con
         * los distintos segmentos encargados del disparo de los invariantes
         * de la red.
         * Además, dentro del monitor creamos un objeto Policy para mantener la
         * carga de los invariantes disparados lo más equitativa posible
         */

        // Creamos el monitor y le asignamos la red
        Monitor monitor = new Monitor(pn, invariantsTMap, maxInv, log);

        // Cantidad de hilos y de segmentos resultado del análisis de la red
        int threadAmount = 15;
        int segmentsAmount = 5;

        // Creamos un array de segmentos y de sus respectivos hilos de ejecución
        Thread[] threads = new Thread[threadAmount];
        Segment[] segments = new Segment[segmentsAmount];
        int[] segThreads = {4,2,4,4,1};

        // Chequeo de cantidad de hilos
        if(Arrays.stream(segThreads).sum() != threadAmount) {
            System.out.print("Error en la Cantidad de Hilos o Cantidad de Hilos por segmento.\n");
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

        // Empezamos a medir el tiempo
        long startTime = new Date().getTime();

        // Lanzamos los hilos
        for(int i = 0; i < threadAmount; i++) {
            threads[i].start();
        }

        /*
         * Finalmente, ejecutamos el script en python para chequear la correcta
         * ejecución de los invariantes de transición, imprimimos información
         * sobre la ejecución del sistema en el monitor y el tiempo que se demoró.
         */

        // El hilo Main espera a que los demás hilos terminen
        for(int i = 0; i < threadAmount; i++) {
            try{
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Termina ejecución de la red
        long endTime = new Date().getTime();

        // El hilo main dispara transiciones hasta volver el estado inicial, para el script
        System.out.println("\nVolviendo al estado inicial...");
        pn.fireContinuously(log,true);

        // Ejecutamos el script
        try {
            // Corriendo el batch file
            Process p = Runtime.getRuntime().exec("scripts/invCheck.bat");

            // Guardamos su resultado en stdin
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            // Guardamos el stderr
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // Leemos la salida del programa
            System.out.println("\nResultado del Script: ");
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
        System.out.print("Petri Net Final State\n");
        for(int i : pn.getMarkings()){
            System.out.printf("%d ",i);
        }
        System.out.println();

        // Imprimimos info sobre la ejecución del monitor
        monitor.printInfo();

        // Agregamos los estados alcanzados al log de invP
        System.out.println("\nAgregando los estados al log...");
        log.logP("Estados alcanzados por el sistema (" + pn.getAllStates().size() + "):\n");
        for(List<Integer> list : pn.getAllStates()) {
            for(int i : list) {
                log.logP(i + " ");
            }
            log.logP("\n");
        }

        // Termina la ejecución
        long timeTaken = (endTime - startTime);
        System.out.printf("\nFIN - Tiempo de ejecución: %d [ms].", timeTaken);

    }
}

/*
TODO
    1. Encontrar una forma de que la red vuelva al estado original para que el script funcione, o encontrar
    un regex que funciona aunque no se vuelva al estado inicial. Para lo primero hay que revisar la inicialización de
    el atributo initialState en la clase PetriNet y después compararlo con el estado actual en Segment.run().
    2. Ver si es necesario que los métodos de monitor ejecutados por los segmentos que no usan el mutex
    (ej. Monitor.isFinished()) deberían ser synchronized.
    3. Analizar el tiempo medido por el main (timeTaken) y el de logTimes.txt.
    4. Agregar lo que se imprime con monitor.printInfo() en un archivo log?.
    5. Ver si sacar la doble revisión de canFire() o implementarla de otra manera.
TODO
    6. Revisar el algoritmo y la implementación de Policy.decide() el monitor. Revisar caso en el que ninguna
    de las transiciones pueda ser disparada por no cumplir con el target.
    En la actual implementación el algoritmo de la política decide si dejar disparar una transición dependiendo
    de si se esta disparando por sobre el porcentaje objetivo,
        a) ¿Qué pasa en caso de que se esté sub-disparando?
        b) ¿Debería hacer que se dispare una transición en caso de que se este sub-disparando?
        c) En caso de hacer que esto ocurra, quizá deberíamos tener 2 colas de condición en el monitor:
            1 Para hilos queriendo disparar transiciones sobre-disparadas (percentage >= threshold) y
            1 para hilos queriendo disparar transiciones sub-disparadas (percentage < threshold)
        d) ¿Cuando se dispare una transición de 1 cola se debería despertar a una de la otra para mantener el
        equilibrio?
 */