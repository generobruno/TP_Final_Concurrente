package Logic;

/**
 * Clase InhibitorArc.
 *  Representa un arco inhibidor en la red.
 */
public class InhibitorArc extends Arc {

    // Plaza que el arco conecta
    private Place place;
    // Transición que el arco conecta
    private Transition transition;
    // Dirección del arco
    private Direction direction;
    // Peso del arco
    private int weight = 1;

    /**
     * Constructor de la clase
     * @param name Nombre del arco
     * @param p Plaza
     * @param t Transición
     */
    protected InhibitorArc(String name, Place p, Transition t) {
        super(name, p, t);
    }

    @Override
    public boolean canFire() {
        return (place.getTokens() < this.getWeight());
    }

    @Override
    public void fire() {
        // do nothing
    }

}
