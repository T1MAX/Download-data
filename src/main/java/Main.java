import org.bson.Document;
import org.json.JSONException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {

    private static Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, JSONException, TransformerException {
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("src/main/resources/logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }
        log.info("log created");
        MongoDBClass mongo = new MongoDBClass();
        //mongo.insert(args[0]);
        //mongo.select();
        //mongo.getSuppliers().forEach(System.out::println);
        //HashMap<Long, Integer> suppliersTenders = mongo.getAllSuppliersTenders();
        //HashMap<Long, Integer> winnerSuppliersTenders = mongo.getAllWinnerSuppliersTenders();
//        for (Long supplierInn : suppliersTenders.keySet()) {
//            System.out.println(supplierInn + " - " + suppliersTenders.get(supplierInn));
//        }

        //1047966022
        //171161031

        long startTime = System.nanoTime();
        System.out.println(mongo.getSupplierTenders(7716748907L));
        System.out.println(System.nanoTime() - startTime);
        //HashMap<Long, Double> probsOfSupplierWin = new HashMap<>();
        //winnerSuppliersTenders.forEach((k, v) -> probsOfSupplierWin.put(k, v.doubleValue()));
        //suppliersTenders.forEach((k, v) -> probsOfSupplierWin.merge(k, v.doubleValue(), (v1, v2) -> v1 / v2));
        //WriteIntoExcel.hashMapToExcel(probsOfSupplierWin);

        //System.out.println(mongo.getSupplierTenders(7706688536L, true));
    }
}
