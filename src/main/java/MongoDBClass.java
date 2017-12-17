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
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class MongoDBClass {
    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDBClass() {
        client = new MongoClient("localhost", 27017);
        database = client.getDatabase("zakupki");
        collection = database.getCollection("contracts");
    }

    public void insert(String pathToDir) throws ParserConfigurationException, TransformerException, SAXException, IOException, JSONException {
        long startTime = System.nanoTime();
        //int j = 0;
        for (Path zipFile : ZipReader.searchZipFiles(Paths.get(pathToDir))) {
            for (JSONObject contract : ZipReader.readZipAndSearchXml(zipFile))
                collection.insertOne(Document.parse(contract.toString()));
            // System.out.printf("%d zip-files read, time passed: %d, total inserts: %d.%n",
            // ++j, System.nanoTime() - startTime, collection.count());
        }
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println(estimatedTime + " nanoseconds, " + collection.count() + " inserts");
    }

    public void select() {
        long startTime = System.nanoTime();
        Bson filter = Filters.lt("price", 100000);
        List<Document> all = collection.find(filter).into(new ArrayList<>());
        long estimatedTime = System.nanoTime() - startTime;
        System.out.printf("%d contracts with price less than 100000 RUB, time for select: %d.%n", all.size(), estimatedTime);
    }
}