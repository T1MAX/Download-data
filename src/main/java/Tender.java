import java.util.List;

/**
 * Created by 803019 on 18.04.2018.
 */
public class Tender {
    private String regNumber;
    private long customerInn;
    private List<Supplier> suppliers;
    private double initialSum;
    private long winnerInn;
    private String subject;
    private double maxPrice;
    private double minPrice;

    public Tender() {
    }

    public Tender(String regNumber) {
        this.regNumber = regNumber;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public long getCustomerInn() {
        return customerInn;
    }

    public void setCustomerInn(long customerInn) {
        this.customerInn = customerInn;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    public double getInitialSum() {
        return initialSum;
    }

    public void setInitialSum(double initialSum) {
        this.initialSum = initialSum;
    }

    public long getWinnerInn() {
        return winnerInn;
    }

    public void setWinnerInn(long winnerInn) {
        this.winnerInn = winnerInn;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "Tender{" +
                "regNumber='" + regNumber + '\'' +
                ", customerInn=" + customerInn +
                ", suppliers=" + suppliers +
                ", initialSum=" + initialSum +
                ", winnerInn=" + winnerInn +
                ", subject='" + subject + '\'' +
                ", maxPrice=" + maxPrice +
                ", minPrice=" + minPrice +
                '}';
    }
}
