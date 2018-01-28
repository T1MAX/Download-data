import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;


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
            for (JSONObject contract : ZipReader.readZipAndSearchXml(zipFile))
                collection.insertOne(Document.parse(contract.toString()));
            //log.info(++j + " zip-files was read");
            collection = database.getCollection(collection.getNamespace().getCollectionName());
        }
        long estimatedTime = System.nanoTime() - startTime;
        log.info(estimatedTime + " nanoseconds, " + collection.count() + " inserts");
    }

    public void select() {
        long startTime = System.nanoTime();
        Bson filter = Filters.lt("price", 100000);
        List<Document> all = collection.find(filter).into(new ArrayList<>());
        long estimatedTime = System.nanoTime() - startTime;
        System.out.printf("%d contracts with price less than 100000 RUB, time for select: %d.%n", all.size(), estimatedTime);
    }
}