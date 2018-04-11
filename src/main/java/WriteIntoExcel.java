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
        HSSFSheet sheet = workbook.createSheet("Sample sheet2");
        int rownum = 0;
        for (Long key : data.keySet()) {
            Row row = sheet.createRow(rownum++);
            row.createCell(0).setCellValue(key);
            Cell cell = row.createCell(1);
            cell.setCellValue(data.get(key));
        }
        try {
            FileOutputStream out = new FileOutputStream(new File("D:\\output2.xls"));
            workbook.write(out);
            out.close();
            System.out.println("Excel written successfully..");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
