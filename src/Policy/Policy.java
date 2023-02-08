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
        monitor = mon;
        cantT = mon.getAmountForTrans().length;
        target = Math.round((1/(float)cantT)*100);
        invariants = inv;
        duplicates = repeatedTransitions(inv);
    }

    /**
     * Método decide
     * Se encarga de decidir si una transición de la red puede ser disparada
     * o debe esperar para que se mantenga la carga de los invariantes
     * @param t Transición a disparar
     * @return True en caso de poder ser disparada
     */
    public boolean decide(int t) {
        // Total de transiciones disparadas hasta el momento
        int totalTransFired = Arrays.stream(monitor.getAmountForTrans()).sum();

        // Esperamos a que se disparen "cantT" transiciones para realizar los cálculos
        if(totalTransFired < cantT) {
            return true;
        }

        // Porcentaje de veces que fue disparada la transición "t" hasta el momento
        int percentage = Math.round((monitor.getAmountForTrans()[(t-1)]/(float)totalTransFired)*100);

        // Revisamos si la transición se repite en los invariantes
        if(duplicates.containsKey(t)) {
            percentage = percentage/(duplicates.get(t));
        }

        // Si el porcentaje de disparos es mayor al objetivo, no disparamos la transición
        if(percentage > target) {
            return false;
        } else {
            return true;
        }
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
