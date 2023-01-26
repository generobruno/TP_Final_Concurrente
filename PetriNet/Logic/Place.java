package Logic;

/**
 * Clase Place
 *  Representa una plaza o lugar en la red.
 */
public class Place extends PetrinetObject {

    // Constante para las plazas sin límite de tokens
    public static final int UNLIMITED = -1;
    // Tokens de la plaza
    private int tokens = 0;
    // Capacidad de la plaza
    private int maxTokens = UNLIMITED;

    /**
     * Constructor de la clase (Tokens iniciales = 0)
     * @param name Nombre de la plaza
     */
    protected Place(String name) {
        super(name);
    }

    /**
     * Constructor de la clase (Tokens iniciales = initial)
     * @param name Nombre de la plaza
     * @param initial Tokens iniciales
     */
    protected Place(String name, int initial) {
        this(name);
        this.tokens = initial;
    }

    /**
     * Método hasAtLeastTokens
     * Compara la cantidad de tokens con un umbral
     * @param threshold Umbral para comparar
     * @return True en caso de tener más tokens
     *         False en caso contrario
     */
    public boolean hasAtLeastTokens(int threshold) {
        return (tokens >= threshold);
    }

    /**
     * Método maxTokensReached
     * Chequea si al agregarle la cantidad especificada
     * se supera el límite de tokens
     * @param newTokens Tokens a agregar
     * @return True en caso de superar el máximo
     *         False en caso contrario
     */
    public boolean maxTokensReached(int newTokens) {
        if(hasUnlimitedMaxTokens()) {
            return false;
        }

        return(tokens+newTokens > maxTokens);
    }

    /**
     * Método hasUnlimitedMaxTokens
     * @return True en caso de no estar limitado
     */
    public boolean hasUnlimitedMaxTokens() {
        return maxTokens == UNLIMITED;
    }

    /**
     * Método getTokens
     * @return Cantidad de tokens en la plaza
     */
    public int getTokens() {
        return tokens;
    }

    /**
     * Método setTokens
     * @param tokens Tokens para la plaza
     */
    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    /**
     * Método setMaxTokens
     * @param max Cantidad de tokens máxima
     */
    public void setMaxTokens(int max) {
        this.maxTokens = max;
    }

    /**
     * Método addTokens
     * @param weight Cantidad de tokens a agregar
     */
    public void addTokens(int weight) {
        this.tokens += weight;
    }

    /**
     * Método removeTokens
     * @param weight Cantidad de tokens a remover
     */
    public void removeTokens(int weight) {
        this.tokens -= weight;
    }

    /**
     * Método toString
     * @return Información de la plaza
     */
    @Override
    public String toString() {
        return super.toString() +
                " Tokens: " + this.tokens +
                " Max: " + (hasUnlimitedMaxTokens()? "Unlimited" : this.maxTokens);
    }
}