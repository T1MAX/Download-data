public class TenderInfo {
    private String regNumber;
    private long CustomerInn;
    private double initialSum;
    private int numOfParticipants;
    private int numOfLastCustomerTenders;

    public TenderInfo(String regNumber) {
        this.regNumber = regNumber;
    }

    @Override
    public String toString() {
        return "TenderInfo{" +
                "regNumber='" + regNumber + '\'' +
                ", CustomerInn=" + CustomerInn +
                ", initialSum=" + initialSum +
                ", numOfParticipants=" + numOfParticipants +
                ", numOfLastCustomerTenders=" + numOfLastCustomerTenders +
                '}';
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    public long getCustomerInn() {
        return CustomerInn;
    }

    public void setCustomerInn(long customerInn) {
        CustomerInn = customerInn;
    }

    public double getInitialSum() {
        return initialSum;
    }

    public void setInitialSum(double initialSum) {
        this.initialSum = initialSum;
    }

    public int getNumOfParticipants() {
        return numOfParticipants;
    }

    public void setNumOfParticipants(int numOfParticipants) {
        this.numOfParticipants = numOfParticipants;
    }

    public int getNumOfLastCustomerTenders() {
        return numOfLastCustomerTenders;
    }

    public void setNumOfLastCustomerTenders(int numOfLastCustomerTenders) {
        this.numOfLastCustomerTenders = numOfLastCustomerTenders;
    }

    public TenderInfo(String regNumber, long customerInn, double initialSum, int numOfParticipants, int numOfLastCustomerTenders) {

        this.regNumber = regNumber;
        CustomerInn = customerInn;
        this.initialSum = initialSum;
        this.numOfParticipants = numOfParticipants;
        this.numOfLastCustomerTenders = numOfLastCustomerTenders;
    }
}
