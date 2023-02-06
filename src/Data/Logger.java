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
    private final String LOG_TRANSITION = "data/log/logFired.txt";
    // Path para los invariantes de plaza
    private final String LOG_PLACES = "data/log/logInvP.txt";
    // Path para información de tiempo
    private final String LOG_TIME = "data/log/logTimes.txt";

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

            // Creamos el archivo en caso de no existir
            logFile = new File(LOG_PLACES);
            logFile.createNewFile();

            // Creamos el archivo en caso de no existir
            logFile = new File(LOG_TIME);
            logFile.createNewFile();

            // Limpiamos los archivos
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
     * Método logP
     * Escribe un archivo de texto con los invariantes de plazas
     * de una red de petri.
     * @param invP Invariante a escribir
     */
    public void logP(String invP) {
        try {
            file = new FileWriter(LOG_PLACES, true);
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.printf("%s", invP);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método logTimed
     * Escribe un archivo de texto con los tiempos que tardaron las
     * transiciones temporizadas en ser disparados.
     * @param T Transición a escribir
     */
    public void logTimed(String T) {
        try {
            file = new FileWriter(LOG_TIME, true);
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.printf("%s", T);
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

            file = new FileWriter(LOG_PLACES);
            PrintWriter writer1 = new PrintWriter(file);
            writer1.print("");
            writer1.close();

            file = new FileWriter(LOG_TIME);
            PrintWriter writer2 = new PrintWriter(file);
            writer2.print("");
            writer2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
