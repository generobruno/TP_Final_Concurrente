package Logic;

/**
 * Class PetriNetObject:
 *  Clase utilizada para crear los objetos de la red.
 */
public class PetrinetObject {

    // Nombre de la red
    private String name;

    /**
     * Constructor de la clase
     * @param name Nombre de la red de petri
     */
    public PetrinetObject(String name) {
        super();
        this.name = name;
    }

    /**
     * Método getName.
     * @return Nombre de la red
     */
    public String getName() {
        return name;
    }

    /**
     * Método setName
     * @param name Nombre de la red
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método ToString
     * @return Nombre de la red
     */
    @Override
    public String toString() {
        return name;
    }

}

