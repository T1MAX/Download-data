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

//        //long startTime = System.nanoTime();
//        HashMap<Long, Integer> suppliersTendersForCertainCompany = mongo.getAllSuppliersTendersForCertainCompany(7717149663L);
//
//        HashMap<Long, Integer> winnerTendersForCertainCompany = mongo.getAllWinnerSuppliersTendersForCertainCompany(7717149663L);
//        //System.out.println(System.nanoTime() - startTime);
//        HashMap<Long, Double> probsOfSupplierWin = new HashMap<>();
//        winnerTendersForCertainCompany.forEach((k, v) -> probsOfSupplierWin.put(k, v.doubleValue()));
//        suppliersTendersForCertainCompany.forEach((k, v) -> probsOfSupplierWin.merge(k, v.doubleValue(), (v1, v2) -> v1 / v2));
//        WriteIntoExcel.hashMapToExcel(probsOfSupplierWin);

        //WriteIntoExcel.hashMapHashtableToExcel(mongo.getWinnersCompany());
        //WriteIntoExcel.hashMapArrayToExcel(mongo.getWinnerPrice());

        //System.out.println(mongo.getSupplierTenders(7706688536L, true));

//        Tender tender = mongo.getTenderData("31200005055-01");
//        TenderPredictionClass prediction = new TenderPredictionClass(tender);
//        prediction.predictWinner();
//        System.out.println(prediction.predictedWinner);
//        System.out.println(prediction.isPredictionRight());
        int rightPredictions = 0, wrongPredictions = 0;
        for (String regNumber : mongo.getRegNumbers()) {
            Tender tender = mongo.getTenderData(regNumber);
            TenderPredictionClass prediction = new TenderPredictionClass(tender);
            prediction.predictWinner();
            if (prediction.isPredictionRight()) rightPredictions++;
            else {
                wrongPredictions++;
                System.out.println("Wrong prediction on tender: " + regNumber + ", predicted winner: " + prediction.predictedWinner);
            }
        }
        System.out.println("Right predictions: " + rightPredictions);
        System.out.println("Wrong predictions: " + wrongPredictions);
    }
}
