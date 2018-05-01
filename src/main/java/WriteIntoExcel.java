import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class WriteIntoExcel {


    public static void hashMapToExcel(HashMap<Long, Double> data) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Sample sheet");
        int rownum = 0;
        for (Long key : data.keySet()) {
            Row row = sheet.createRow(rownum++);
            row.createCell(0).setCellValue(key);
            Cell cell = row.createCell(1);
            cell.setCellValue(data.get(key));
        }
        try {
            FileOutputStream out = new FileOutputStream(new File("D:\\output4.xls"));
            workbook.write(out);
            out.close();
            System.out.println("Excel written successfully..");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void hashMapArrayToExcel(HashMap<String, double[]> data) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Sample sheet");
        int rownum = 0;
        for (String key : data.keySet()) {
            Row row = sheet.createRow(rownum++);
            double[] objArr = data.get(key);
            int cellnum = 0;
            for (double obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                cell.setCellValue(obj);
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(new File("D:\\output5.xls"));
            workbook.write(out);
            out.close();
            System.out.println("Excel written successfully..");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void hashMapHashtableToExcel(HashMap<Long, Hashtable<Long, Integer>> data) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Sample sheet");
        int rownum = 0;
        for (Long key : data.keySet()) {
            Hashtable<Long, Integer> hashtable = data.get(key);
            for (Map.Entry<Long, Integer> entry : hashtable.entrySet()) {
                Row row = sheet.createRow(rownum++);
                row.createCell(0).setCellValue(key);
                row.createCell(1).setCellValue(entry.getKey());
                row.createCell(2).setCellValue(entry.getValue());
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(new File("D:\\output6.xls"));
            workbook.write(out);
            out.close();
            System.out.println("Excel written successfully..");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
