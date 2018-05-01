/**
 * Created by 803019 on 18.04.2018.
 */
public class Supplier {
    private long supplierInn;
    private double price;
    private boolean isWin;
    private int otherTendersNumber;

    public long getSupplierInn() {
        return supplierInn;
    }

    public void setSupplierInn(long supplierInn) {
        this.supplierInn = supplierInn;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setWin(boolean win) {
        isWin = win;
    }

    public int getOtherTendersNumber() {
        return otherTendersNumber;
    }

    public void setOtherTendersNumber(int otherTendersNumber) {
        this.otherTendersNumber = otherTendersNumber;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "supplierInn=" + supplierInn +
                ", price=" + price +
                ", isWin=" + isWin +
                '}';
    }
}
