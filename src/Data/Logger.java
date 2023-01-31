package Data;

import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Clase Logger
 *  Log para los eventos del sistema
 */
public class Logger {

    // Archivo a escribir
    private FileWriter file;
    // Path para la secuencia de transiciones
    private final String LOG_TRANSITION = "data/log/log.txt";

    /**
     * Constructor de la clase
     */
    public Logger() {
        file = null;
        clear();
    }

    /**
     * Método logT
     * Escribe un archivo de texto con la secuencia de transiciones,
     * o eventos, de una red de petri.
     * @param transition Transición a escribir
     */
    public void logT(String transition) {
        try {
            file = new FileWriter(LOG_TRANSITION, true);
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.printf("%s", transition);
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método clear
     * Limpia el archivo log de la simulación.
     */
    public void clear() {
        try {
            file = new FileWriter(LOG_TRANSITION);
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
