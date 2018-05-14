import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.*;
import com.mongodb.operation.AggregateOperation;
import myParser.ZipReader;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

import static com.mongodb.client.model.Accumulators.push;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;
import static java.util.Arrays.asList;

public class MongoDBClass {

    private static Logger log = Logger.getLogger(MongoDBClass.class.getName());

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    private static final String PATH_TO_PROPERTIES = "src/main/resources/config.properties";

    public MongoDBClass() {
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(PATH_TO_PROPERTIES)) {
            properties.load(inputStream);
            client = new MongoClient(properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));
            database = client.getDatabase(properties.getProperty("database"));
            collection = database.getCollection(properties.getProperty("collection"));
            log.info(collection.getNamespace().getFullName() + " - opened connection");
        } catch (IOException e) {
            log.info("Exception: " + e);
            e.printStackTrace();
        }
    }

    public void insert(String pathToDir) throws ParserConfigurationException, TransformerException, SAXException, IOException, JSONException {
        long startTime = System.nanoTime();
        //int j = 0;
        for (Path zipFile : ZipReader.searchZipFiles(Paths.get(pathToDir))) {
            for (JSONObject protocol : ZipReader.readZipAndSearchXml(zipFile))
                collection.insertOne(Document.parse(protocol.toString()));
            //log.info(++j + " zip-files was read");
            collection = database.getCollection(collection.getNamespace().getCollectionName());
        }
        long estimatedTime = System.nanoTime() - startTime;
        log.info(estimatedTime + " nanoseconds, " + collection.count() + " inserts");
    }

    public ArrayList<Long> getCustomers() {
        ArrayList<Document> customers = collection.aggregate(asList(match(exists("customer")),
                replaceRoot("$customer.mainInfo"),
                group("$inn", sum("count", 1)))).into(new ArrayList<>());
        ArrayList<Long> innCustomers = new ArrayList<>();
        customers.forEach(customer -> innCustomers.add(Long.parseLong(String.valueOf(customer.get("_id")))));
        return innCustomers;
    }

    public ArrayList<Long> getSuppliers () {
        MongoCollection<Document> localCollection = database.getCollection("SuppliersTenders");
        ArrayList<Long> suppliersInn = new ArrayList<>();
        localCollection.find().into(new ArrayList<>()).forEach(document -> suppliersInn.add(Long.parseLong(String.valueOf(document.get("_id")))));
        return suppliersInn;
    }

    public int getSupplierLastTenders (long supplierInn) {
        MongoCollection<Document> localCollection = database.getCollection("SuppliersTenders");
        ArrayList<Document> supplierTenders = localCollection.find().into(new ArrayList<>());
        for (Document document : supplierTenders)
            if (document.getLong("_id") == supplierInn)
                return document.getDouble("count").intValue();
        return 0;
    }

    public int getSupplierLastWins (long supplierInn) {
        MongoCollection<Document> localCollection = database.getCollection("SuppliersWins");
        ArrayList<Document> supplierTenders = localCollection.find().into(new ArrayList<>());
        for (Document document : supplierTenders)
            if (document.getLong("_id") == supplierInn)
                return document.getDouble("count").intValue();
        return 0;
    }

    public int getRelOfCustomerSupplier(long customerInn, long supplierInn) {
        return collection.aggregate(asList(
                match(and(exists("customer"), eq("customer.mainInfo.inn", customerInn))),
                unwind("$lotApplicationsList.protocolLotApplications.application"),
                replaceRoot("$lotApplicationsList.protocolLotApplications.application"),
                match(and(exists("supplierInfo"), eq("winnerIndication", "W"))),
                replaceRoot("$supplierInfo"),
                match(eq("inn", supplierInn)),
                count("count"))).first().getDouble("count").intValue();
    }

    public int getNumOfLastCustomerTenders(long customerInn) {
        return collection.find(eq("customer.mainInfo.inn", customerInn)).into(new ArrayList<>()).size();
    }

    public ArrayList<String> getRegNumbers() {
        ArrayList<String> regNumbers = new ArrayList<>();
        collection.find().into(new ArrayList<>()).forEach(document -> regNumbers.add(String.valueOf(document.get("registrationNumber"))));
        return regNumbers;
    }

    public TenderInfo getTenderInfo(String regNumber) {
        MongoCollection<Document> localCollection = database.getCollection("trainData");
        Document tenderMongo = localCollection.find(eq("registrationNumber", regNumber)).first();
        TenderInfo tenderInfo = new TenderInfo(regNumber);
        try {
            tenderInfo.setInitialSum(Double.parseDouble(String.valueOf(tenderMongo.get("initialSum"))));
            tenderInfo.setCustomerInn(Long.parseLong(String.valueOf(tenderMongo.get("customerInn"))));
            tenderInfo.setNumOfParticipants((int)Double.parseDouble(String.valueOf(tenderMongo.get("numParticipants"))));
            tenderInfo.setNumOfLastCustomerTenders(getNumOfLastCustomerTenders(tenderInfo.getCustomerInn()));
        } catch (NullPointerException ignored) {
        }
        return tenderInfo;
    }

    public Tender getTenderData(String regNumber, boolean train) {
        MongoCollection<Document> localCollection;
        if (train) localCollection = database.getCollection("trainData");
        else localCollection = database.getCollection("testData");
        Document tenderMongo = localCollection.find(eq("registrationNumber", regNumber)).first();
        Tender tender = new Tender(regNumber);
        long customerInn = (long)tenderMongo.get("customerInn");
        try {
            tender.setInitialSum((double)tenderMongo.get("initialSum"));
            tender.setNumOfParticipants((tenderMongo.getDouble("numApps")).intValue());
            tender.setCustomerLastTendersNum(getNumOfLastCustomerTenders(customerInn));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            ArrayList<Document> applications = (ArrayList<Document>) tenderMongo.get("applications");
            applications.forEach(document -> {
                if (document.get("winnerIndication").equals("W")) {
                    long winnerInn = (long)document.get("supplierInfo.inn");
                    tender.setWinnerLastTendersNum(getSupplierLastTenders(winnerInn));
                    tender.setWinnerLastWinRate(getSupplierLastWins(winnerInn) * 1. / tender.getWinnerLastTendersNum());
                    tender.setWinnerRelToCustomer(getRelOfCustomerSupplier(customerInn, winnerInn));
                    tender.setWinnerPrice((double)document.get("price"));
                }
            });
        } catch (NullPointerException | NumberFormatException e) {
            e.printStackTrace();
        }
        return tender;
    }

    public long get() {
        MongoCollection<Document> local = database.getCollection("moskvaProtocolsShort");
        return 1l;
    }


    //TODO
    public String getCompanyNameByInn() {
        return "";
    }

    public void printCollection(ArrayList<Document> documents) {
        for (Document doc : documents)
            Helpers.printJson(doc);
    }
}