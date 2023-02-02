package Data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Clase Logger
 *  Log para los eventos del sistema
 */
public class Logger {

    // Carpeta para los logs
    private File dir;
    // File para crear el archivo a escribir
    private File logFile;
    // Archivo a escribir
    private FileWriter file;
    // Path para la secuencia de transiciones
    private final String LOG_TRANSITION = "data/log/log.txt";

    /**
     * Constructor de la clase
     */
    public Logger() {
        try {
            file = null;

            // Creamos la carpeta en caso de que no exista
            dir = new File("data/log");
            dir.mkdir();

            // Creamos el archivo en caso de no existir
            logFile = new File(LOG_TRANSITION);
            logFile.createNewFile();

            // Limpiamos el archivo
            clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            printWriter.printf("%s-", transition);
            printWriter.close();
        } catch (IOException e) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
