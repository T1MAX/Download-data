import java.util.Hashtable;
import java.util.Map;

public class TenderPredictionClass {
    Tender tender;
    long predictedWinner;

    public TenderPredictionClass(Tender tender) {
        this.tender = tender;
    }

    public void predictWinner() {
        Hashtable<Supplier, Double> winRate= new Hashtable<>();
        tender.getSuppliers().forEach(supplier -> {
            double rate = 0;
            if (supplier.getPrice() == tender.getMinPrice()) rate += 5;
            if (supplier.getPrice() == tender.getMaxPrice()) rate += 2;
            rate += 2 * Math.log(supplier.getOtherTendersNumber());
            winRate.put(supplier, rate);
        });
        double maxRate = 0;
        Supplier curLeader = new Supplier();
        for (Map.Entry<Supplier, Double> entry: winRate.entrySet()) {
            if (entry.getValue() > maxRate) {
                maxRate = entry.getValue();
                curLeader = entry.getKey();
            }
        }
        predictedWinner = curLeader.getSupplierInn();
    }

    public boolean isPredictionRight() {
        return tender.getWinnerInn() == predictedWinner;
    }
}
