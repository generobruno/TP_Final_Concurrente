package Data;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

public class Data {

    public Data() {}

    public int[] getMarks(String name, int t) {
        int[] vector = new int[t];

        try(FileInputStream file = new FileInputStream(new File(name))) {

            HSSFWorkbook calc = new HSSFWorkbook(file);
            HSSFSheet hoja = calc.getSheetAt(0);

            Iterator<Row> filaIt = hoja.iterator();
            Row fila = filaIt.next();

            Iterator<Cell> colIt = fila.cellIterator();
            Cell columna;

            int j = 0;

            while(colIt.hasNext()) {
                columna = colIt.next();
                vector[j] = (int) columna.getNumericCellValue();
                j++;
            }

        } catch (Exception e) {
            e.getMessage();
        }

        return vector;
    }

    public int[][] getMatrix(String name, int t, int p) {

        int[][] matrix = new int[p][t];

        try(FileInputStream file = new FileInputStream(new File(name))) {

            HSSFWorkbook calc = new HSSFWorkbook(file);
            HSSFSheet hoja = calc.getSheetAt(0);

            Iterator<Row> filaIt = hoja.iterator();
            Row fila;
            int i = 0;

            while(i < p) {
                fila = filaIt.next();

                Iterator<Cell> colIt = fila.cellIterator();
                Cell col;

                int j = 0;
                while(j < t) {
                    col = colIt.next();
                    matrix[i][j] = (int) col.getNumericCellValue();
                    j++;
                }

                i++;
            }

        } catch (Exception e) {
            e.getMessage();
        }

        return matrix;
    }

}
