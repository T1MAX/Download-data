import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.rdd.RDD;
import scala.Tuple2;

public class RegressionClass {

    JavaSparkContext jsc;

    public RegressionClass() {
        SparkConf conf = new SparkConf().setAppName("JavaKMeansExample").setMaster("local[*]");
        jsc = new JavaSparkContext(conf);
        jsc.setLogLevel("WARN");
    }

    public void readData() {
        String path = "moskvaTrainedData";
        JavaRDD<String> data = jsc.textFile(path);
//        JavaRDD<Tuple2<Integer, Vector>> parsedData = data.map(s -> {
//            String[] sarray = s.split(" ");
//            Vector vector = Vectors.parse(sarray[0]);
//            int cluster = Integer.parseInt(sarray[2]);
//            return new Tuple2<>(cluster, vector);
//        });
//        parsedData.cache();

        JavaRDD<LabeledPoint> parsedData = data.map(line -> {
            String[] parts = line.split(",");
            String[] features = parts[1].split(" ");
            double[] v = new double[features.length];
            for (int i = 0; i < features.length - 1; i++) {
                v[i] = Double.parseDouble(features[i]);
            }
            return new LabeledPoint(Double.parseDouble(parts[0]), Vectors.dense(v));
        });


        //JavaPairRDD<Integer, Iterable<Vector>> groupData = JavaPairRDD.fromJavaRDD(parsedData).groupByKey();

    }
}
