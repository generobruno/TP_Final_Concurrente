package Policy;

import Monitor.Monitor;

import java.util.*;

/**
 * Clase Política
 *  Se encarga de mantener la carga en los diferentes invariantes de transición balanceada.
 *  Esto es, que el promedio de disparo de las transiciones de los invariantes sean
 *  aproximadamente iguales.
 */
public class Policy {
    // Monitor con la Red de Petri a controlar
    Monitor monitor;
    // Cantidad de Transiciones
    private final int cantT;
    // Carga objetivo para cada transición
    private final int target;
    // Cantidad de transiciones necesarias para comenzar
    private final int start;
    // Lista de invariantes
    List<int[]> invariants;
    // Mapa de transiciones repetidas
    Map<Integer,Integer> duplicates;

    /**
     * Constructor de la clase
     * @param mon Monitor de concurrencia
     * @param inv Lista con los invariantes de Transición
     */
    public Policy(Monitor mon, List<int[]> inv) {
        // Monitor asociado
        monitor = mon;
        // Cantidad de transiciones
        cantT = mon.getAmountForTrans().length;
        // Porcentaje objetivo a mantener para las transiciones
        target = (Math.round((1/(float)cantT) * 100)); // TODO Revisar número
        // Cantidad de transiciones necesarias para comenzar a ejecutar la política
        start = (Math.round(monitor.getMaxInv() * 0.1f)); // TODO Revisar número
        // Lista de invariantes de transición
        invariants = inv;
        // Mapa de transiciones repetidas en los invariantes
        duplicates = repeatedTransitions(inv);
    }

    /**
     * Método decide
     * Se encarga de decidir si una transición de la red puede ser disparada
     * o debe esperar para que se mantenga la carga de los invariantes
     * @param ready Transición listas para disparar
     * @return Número de la transición a disparar en caso de poder ser disparada
     */
    public int decide(int[] ready) {
        // Total de transiciones disparadas hasta el momento
        int totalTransFired = Arrays.stream(monitor.getAmountForTrans()).sum();
        // Transición a disparar
        int transition = 0;

        // Esperamos a que se disparen suficientes transiciones para realizar los cálculos TODO sacar esto??
        /*
        if(totalTransFired < start) {
            int rnd = new Random().nextInt(ready.length);
            while(ready[rnd] == 0) {
                rnd = new Random().nextInt(ready.length);
            }
            return ready[rnd];
        }

         */

        // Si solo hay una transición lista, se decide por ella
        int count = 0;
        for (int j : ready) {
            if (j == 0) {
                count++;
            } else {
                transition = j;
            }
        }
        if(count == (ready.length - 1)) {
            return transition;
        }

        // Revisamos todas las transiciones listas para obtener la más alejada del "target"
        float min = target; // TODO Revisar
        float[] percentages = new float[cantT]; // TODO Sacar después de debugear
        float[] values = new float[cantT]; // TODO Sacar después de debugear
        for(int i = 0; i < ready.length; i++) {
            if(ready[i] != 0) {
                // Porcentaje de veces que fue disparada una transición hasta el momento
                float percentage = (monitor.getAmountForTrans()[i]/(float)totalTransFired)*100;

                // Revisamos si la transición se repite en los invariantes
                if(duplicates.containsKey(ready[i])) {
                    percentage = percentage/(duplicates.get(ready[i]));
                }

                percentages[i] = percentage;

                // Comparamos el valor obtenido con el mínimo
                float value = (target - percentage);
                values[i] = value;
                if(value < min && value > 0) {
                    min = value;
                    transition = ready[i];
                }
            }
        }

        return transition;

    }

    /**
     * Método duplicatedTransitions
     * Analiza la lista de invariantes de transición para ver si hay transiciones
     * repetidas en los mismos.
     * @param inv Lista de invariantes de transición
     * @return Mapa con las transiciones repetidas y la cantidad de veces que se repiten
     */
    public Map<Integer,Integer> repeatedTransitions(List<int[]> inv) {
        Set<Integer> set = new HashSet<>();
        Map<Integer,Integer> mapDup = new HashMap<>();

        // Analizamos todas las listas de invariantes
        for(int[] arr : inv) {
            for(int i : arr) {
                // Si la transición está repetida, la agregamos a la lista
                if(!set.add(i)) {
                    // Asociamos como valor la cantidad de veces que se repite
                    if(mapDup.get(i) == null) {
                        mapDup.put(i,2);
                    } else {
                        mapDup.put(i,(mapDup.get(i)+1));
                    }
                }
            }
        }

        return mapDup;
    }

}
