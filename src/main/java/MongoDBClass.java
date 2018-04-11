import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.operation.AggregateOperation;
import myParser.ZipReader;
import org.bson.Document;
import org.bson.conversions.Bson;
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

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
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

    public void select() throws JSONException {
        collection = database.getCollection("supplierTop");
        List<Document> supplierTop = collection.find().into(new ArrayList<>());
        collection = database.getCollection("participantTop");
        List<Document> participantTop = collection.find().into(new ArrayList<>());

        for (Document supplier : supplierTop) {
            long inn = Long.parseLong(String.valueOf(supplier.get("_id")));
            double count = supplier.getDouble("count");
            boolean exists = false;
            for (Document participant : participantTop) {
                if (participant.get("_id").equals(inn)) {
                    double curCount = (Double) participant.remove("count");
                    participant.append("count", curCount + count);
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                participantTop.add(supplier);
            }
        }

        int sumCount = 0;
        for (Document participant: participantTop) {
            sumCount += participant.getDouble("count");
            Helpers.printJson(participant);
        }
        System.out.println(participantTop.size());
        System.out.println(sumCount);
    }

    public ArrayList<Long> getCustomers() {
        ArrayList<Document> customers = collection.aggregate(asList(match(exists("customer")),
                replaceRoot("$customer.mainInfo"),
                group("$inn", sum("count", 1)),
                sort(orderBy(descending("count"))))).into(new ArrayList<>());
        ArrayList<Long> innCustomers = new ArrayList<>();
        for (Document customer : customers)
            innCustomers.add(Long.parseLong(String.valueOf(customer.get("_id"))));
        return innCustomers;
    }

    public ArrayList<Long> getSuppliers () {
        MongoCollection<Document> localCollection = database.getCollection("moskvaSupplierTop");
        ArrayList<Long> suppliersInn = new ArrayList<>();
        localCollection.find().into(new ArrayList<>()).forEach(document -> suppliersInn.add(Long.parseLong(String.valueOf(document.get("_id")))));
        return suppliersInn;
    }

    public int getSupplierTenders (long supplierInn) {
        MongoCollection<Document> localCollection = database.getCollection("moskvaSupplierTop");
        int supplierTenders = 0;
        ArrayList<Document> moskvaSupplierTop = localCollection.find().into(new ArrayList<>());
        for (Document document : moskvaSupplierTop) {
            if (Long.parseLong(String.valueOf(document.get("_id"))) == supplierInn) {
                supplierTenders = document.getDouble("count").intValue();
                break;
            }
        }
        return supplierTenders;
    }

    public HashMap<Long, Integer> getAllSuppliersTenders() {
        HashMap<Long, Integer> result = new HashMap<>();
        MongoCollection<Document> localCollection = database.getCollection("moskvaSupplierTop");
        localCollection.find().into(new ArrayList<>()).forEach(document -> result.put(Long.parseLong(String.valueOf(document.get("_id"))), document.getDouble("count").intValue()));
        return result;
    }

    public HashMap<Long, Integer> getAllWinnerSuppliersTenders() {
        HashMap<Long, Integer> result = new HashMap<>();
        MongoCollection<Document> localCollection = database.getCollection("moskvaSupplierWinnerTop");
        localCollection.find().into(new ArrayList<>()).forEach(document -> result.put(Long.parseLong(String.valueOf(document.get("_id"))), document.getDouble("count").intValue()));
        return result;
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