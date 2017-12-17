import org.json.JSONException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, JSONException, TransformerException {
        MongoDBClass mongo = new MongoDBClass();
        long startTime = System.nanoTime();
        mongo.insert(args[0]);
        System.out.printf("Time for insert: %d", System.nanoTime() - startTime);
        //mongo.select();
    }
}
