package Monitor;

public class Monitor {

    /*
       TODO
        Asignar a la clase los atributos:
            1. Red de Petri
            2. Mutex (ReentrantLock o Semaphore)
            3. Estado del sistema (Distintos valores de la matriz de marcado)
            4. Eventos del sistema (Transiciones disparadas)
        Ademas, los métodos:
            1. fireTransition(): Debe poder disparar una transición o conjunto
            de transiciones que le pasemos como parámetro. También se debe actualizar
            el la matriz de marcado y el vector E.
            2. TransicionesSensibilizadas -> Petrinet.getTransitionsAbleToFire(): (S_i)
            Cambiar el método para que devuelva un array (vector E) de dimension = cantT. Siendo
            1 las T sensibilizadas y 0 las que no. (S_i = M_j + I^i ; Ver Paper).
            3. Agregar Guardas (Ward) a las transiciones de la red. Para disparar una transición con
            guarda, la misma debe estar sensibilizada y el valor de su guarda debe ser TRUE.
            4. Agregar Cola de Eventos (Events) a las transiciones de la red. La cola es un contador
            que se incrementa al llegar un evento (?) y se decrementa cuando la transición asociada se dispara.
            Para que la transición esté sensibilizada se necesita que su cola tenga al menos 1 evento.
            5. Luego de que esto funcione, agregar semánticas temporales [alfa_i, beta_i].
     */

    /*
       TODO
        En la clase Petrinet, el disparo de las transiciones debería actualizar su
        matriz de incidencias y de marcados, segun la ecuacion de estados.
     */
}
