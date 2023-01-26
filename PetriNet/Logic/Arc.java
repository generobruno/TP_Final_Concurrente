package Logic;

/**
 * Clase Arc.
 *  Representa los arcos de entrada y salida de la red
 *  a las distintas Plazas o Transiciones.
 */
public class Arc extends PetrinetObject {

    // Plaza que el arco conecta
    private Place place;
    // Transición que el arco conecta
    private Transition transition;
    // Dirección del arco
    private Direction direction;
    // Peso del arco
    private int weight = 1;

    /**
     * Enum Direction
     * Representa las 2 direcciones que puede tener
     * un arco
     */
    enum Direction {

        // Disparo de Plaza a Transición
        PLACE_TO_TRANSITION {
            /**
             * Método canFire
             * El arco puede disparar de PLACE_TO_TRANSITION
             * solo si la plaza tiene la misma cantidad de tokens
             * que el peso del arco
             * @param p Plaza desde la que se dispara
             * @param weight Peso del arco
             * @return True en caso de poder disparar
             */
            @Override
            public boolean canFire(Place p, int weight) {
                return p.hasAtLeastTokens(weight);
            }

            /**
             * Método fire
             * Al efectuar un disparo de PLACE_TO_TRANSITION se
             * eliminan de la plaza la cantidad de tokens igual
             * al peso del arco
             * @param p Plaza desde la que se dispara
             * @param weight Peso del arco
             */
            @Override
            public void fire(Place p, int weight) {
                p.removeTokens(weight);
            }
        },

        // Disparo de Transición a Plaza
        TRANSITION_TO_PLACE {
            /**
             * Método canFire
             * El arco puede disparar de TRANSITION_TO_PLACE
             * solo si la plaza no ha llegado a su máxima
             * cantidad de tokens
             * @param p Plaza a la que se dispara
             * @param weight Peso de la plaza
             * @return True en caso de poder disparar
             */
            @Override
            public boolean canFire(Place p, int weight) {
                return !(p.maxTokensReached(weight));
            }

            /**
             * Método fire
             * Al efectuar un disparo de TRANSITION_TO_PLACE se
             * agregan a la plaza la cantidad de tokens igual
             * al peso del arco
             * @param p Plaza a la que se dispara
             * @param weight Peso del arco
             */
            @Override
            public void fire(Place p, int weight) {
                p.addTokens(weight);
            }
        };

        public abstract boolean canFire(Place p, int weight);

        public abstract void fire(Place p, int weight);
    }

    /**
     * Constructor del Arco
     * @param name Nombre del arco
     * @param d Dirección del arco
     * @param p Plaza
     * @param t Transición
     */
    private Arc(String name, Direction d, Place p, Transition t) {
        super(name);
        this.direction = d;
        this.place = p;
        this.transition = t;
    }

    /**
     * Método Arc(String name, Place p, Transition t)
     * Genera un arco que va desde una plaza a una
     * transición
     * @param name Nombre del arco
     * @param p Plaza
     * @param t Transición
     */
    protected Arc(String name, Place p, Transition t) {
        this(name, Direction.PLACE_TO_TRANSITION, p, t);
        t.addIncoming(this);
    }

    /**
     * Método Arc(String name, Transition t, Place p)
     * Genera un arco que va desde una transición a una
     * plaza
     * @param name Nombre del arco
     * @param p Plaza
     * @param t Transición
     */
    protected Arc(String name, Transition t, Place p) {
        this(name, Direction.TRANSITION_TO_PLACE, p, t);
        t.addOutgoing(this);
    }

    /**
     * Método canFire
     * Chequea si se puede realizar un disparo
     * en la dirección del arco
     * @return True en caso de poder disparar
     */
    public boolean canFire() {
        return direction.canFire(place, weight);
    }

    /**
     * Método fire
     * Realiza un disparo en la dirección del
     * arco
     */
    public void fire() {
        direction.fire(place, this.weight);
    }

    /**
     * Método setWeight
     * Asigna un peso al arco
     * @param weight Peso del arco
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Método getWeight
     * @return Peso del arco
     */
    public int getWeight() {
        return weight;
    }
}