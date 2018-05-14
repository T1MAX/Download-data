import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;


/**
 * Created by 803019 on 12.05.2018.
 */
public class SparkMongoDBReader {

    private static final String PATH_TO_PROPERTIES = "src/main/resources/sparkMongoDBConfig.properties";

    private SparkSession spark;
    private JavaSparkContext jsc;

    public SparkMongoDBReader() {

        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(PATH_TO_PROPERTIES)) {
            properties.load(inputStream);
            spark = SparkSession.builder()
                    .master("local")
                    .appName("MongoSparkConnectorIntro")
                    .config("spark.mongodb.input.uri", "mongodb://127.0.0.1/zakupki.moskvaProtocols")
                    .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/zakupki.moskvaProtocols")
                    .getOrCreate();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsc = new JavaSparkContext(spark.sparkContext());

    }

    public JavaRDD<String> getRegNumbers() {
        return MongoSpark.load(jsc).map(document -> String.valueOf(document.get("registrationNumber")));
    }

    public TenderInfo getTenderInfo(String regNumber) {
        SparkSession spark = SparkSession.builder()
                .master("local")
                .appName("MongoSparkConnectorIntro")
                .config("spark.mongodb.input.uri", "mongodb://127.0.0.1/zakupki.trainData")
                .getOrCreate();
        jsc = new JavaSparkContext(spark.sparkContext());
        Document tenderMongo = MongoSpark.load(jsc).filter(doc -> doc.get("registrationNumber").equals(regNumber)).first();
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

    private int getNumOfLastCustomerTenders(long customerInn) {
        return MongoSpark.load(jsc).filter(doc -> doc.get("customer.mainInfo.inn").equals(customerInn)).collect().size();
    }
}
