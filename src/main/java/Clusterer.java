import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import scala.collection.mutable.HashTable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Clusterer {
    private MongoDBClass mongoDBClass;
    private ArrayList<Tender> tenders;
    private ArrayList<double[]> normalizedData;
    private SparkMongoDBReader sparkMongo;
    public Clusterer(MongoDBClass mongoDBClass) {
        //this.sparkMongo = sparkMongoDBReader;
        this.mongoDBClass = mongoDBClass;
        this.tenders = new ArrayList<>();
        this.normalizedData = new ArrayList<>();
    }

    public void getTenders() {
        mongoDBClass.getRegNumbers().forEach(regNumber -> {
            Tender tender = mongoDBClass.getTenderData(regNumber, true);
            if (tender.getInitialSum() != 0.0
                    && tender.getWinnerPrice() != 0.0
                    && tender.getNumOfParticipants() != 0) {
                tenders.add(tender);
            }
        });
    }

    public void normalizeTenderInfo() {
        double minInitialSum = 10000000., maxInitialSum = 0;
        int minParticipants = 1000, maxParticipants = 0,
                minLastTenders = 10000, maxLastTenders = 0;

        for (Tender tender : tenders) {
            double initialSum = tender.getInitialSum();
            int participants = tender.getNumOfParticipants(), lastTenders = tender.getCustomerLastTendersNum();
            if (initialSum > maxInitialSum) maxInitialSum = initialSum;
            else if (initialSum < minInitialSum) minInitialSum = initialSum;
            if (participants > maxParticipants) maxParticipants = participants;
            else if (participants < minParticipants) minParticipants = participants;
            if (lastTenders > maxLastTenders) maxLastTenders = lastTenders;
            else if (lastTenders < minLastTenders) minLastTenders = lastTenders;
        }
        System.out.println("Sums: " + maxInitialSum + " " + minInitialSum);
        System.out.println("Participants: " + maxParticipants + " " + minParticipants);
        System.out.println("Tenders: " + maxLastTenders + " " + minLastTenders);
        double initialSumPeriod = maxInitialSum - minInitialSum;
        int participantPeriod = maxParticipants - minParticipants;
        int lastTendersPeriod = maxLastTenders - minLastTenders;

        for (Tender tender : tenders) {
            double[] data = new double[3];
            data[0] = (tender.getInitialSum() - minInitialSum) / initialSumPeriod;
            data[1] = (tender.getNumOfParticipants() - minParticipants) * 1. / participantPeriod;
            data[2] = (tender.getCustomerLastTendersNum() - minLastTenders) * 1. / lastTendersPeriod;
            normalizedData.add(data);
        }
    }

    public void printDataToFile(String filename) throws FileNotFoundException {
        File file = new File(filename);
        PrintWriter writer = new PrintWriter(file);
        normalizedData.forEach(data -> {
            writer.print(data[0] + " ");
            writer.print(data[1] + " ");
            writer.println(data[2]);
        });
        writer.flush();
        writer.close();
    }


    public void sparkClustering() throws FileNotFoundException {
        SparkConf conf = new SparkConf().setAppName("JavaKMeansExample").setMaster("local[*]");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        jsc.setLogLevel("WARN");



        String path = "moskvaTrainData";
        JavaRDD<String> data = jsc.textFile(path);
        JavaRDD<Vector> parsedData = data.map(s -> {
            String[] sarray = s.split(" ");
            double[] values = new double[sarray.length];
            for (int i = 0; i < sarray.length; i++) {
                values[i] = Double.parseDouble(sarray[i]);
            }
            return Vectors.dense(values);
        });
        parsedData.cache();

        // Cluster the data into two classes using KMeans
//        int optNumCl = 29, optIter = 26;
//        double minError = 10000;
//        for (int i = 20; i < 22; i++) {
//            for (int j = 0; j < 40; j++) {
//                KMeansModel clusters = KMeans.train(parsedData.rdd(), i, j);
//                double error = clusters.computeCost(parsedData.rdd());
//                if (error < minError) {
//                    minError = error;
//                    optIter = j;
//                    optNumCl = i;
//                }
//            }
//        }
//        System.out.println("optimal iterations: " + optIter);
//        KMeansModel clusters = KMeans.train(parsedData.rdd(), optNumCl, optIter);
//        System.out.println("Cluster centers:");
//        for (Vector center: clusters.clusterCenters()) {
//            System.out.println(" " + center);
//        }

        KMeansModel loadModel = KMeansModel.load(jsc.sc(),
                "target/KMeansModel");
        JavaRDD<Integer> predictingClusters = loadModel.predict(parsedData);

        PrintWriter writer = new PrintWriter("moskvaTrainedData");
        HashMap<Vector, Integer> clus = new HashMap<>();
        List<Vector> pData = parsedData.collect();
        pData.forEach(pr -> clus.put(pr, predictingClusters.collect().get(pData.indexOf(pr))));
        clus.forEach((vec, cl) -> {
            writer.println(vec + " cluster " + cl);
        });

        writer.flush();
        writer.close();
        // Evaluate clustering by computing Within Set Sum of Squared Errors
//        double WSSSE = loadModel.computeCost(parsedData.rdd());
//        System.out.println("Within Set Sum of Squared Errors = " + WSSSE);

        // Save and load model
//        clusters.save(jsc.sc(), "target/KMeansModel");
//        KMeansModel sameModel = KMeansModel.load(jsc.sc(),
//                "target/KMeansModel");
//        // $example off$

        jsc.stop();
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.setProperty("hadoop.home.dir", "D:\\Загрузки\\hadoop-3.0.2");
        Clusterer clusterer = new Clusterer(new MongoDBClass());
        //clusterer.getTenders();
        //clusterer.getTenders();
        //clusterer.normalizeTenderInfo();
        //clusterer.printDataToFile("moskvaTrainData");
       // System.out.println("Time for retrieving, working and print data: " + (startTime - System.nanoTime()));

        clusterer.sparkClustering();


    }
}
