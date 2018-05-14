public class Tender {
    private String regNumber;
    private int customerLastTendersNum;
    private double initialSum;
    private int winnerLastTendersNum;
    private double winnerLastWinRate;
    private int winnerRelToCustomer;
    private double winnerPrice;
    private int numOfParticipants;
    private String subject;

    public Tender() {
    }

    public double getWinnerPrice() {
        return winnerPrice;
    }

    public void setWinnerPrice(double winnerPrice) {
        this.winnerPrice = winnerPrice;
    }

    public int getNumOfParticipants() {
        return numOfParticipants;
    }

    public void setNumOfParticipants(int numOfParticipants) {
        this.numOfParticipants = numOfParticipants;
    }

    public int getCustomerLastTendersNum() {
        return customerLastTendersNum;
    }

    public void setCustomerLastTendersNum(int customerLastTendersNum) {
        this.customerLastTendersNum = customerLastTendersNum;
    }

    public int getWinnerLastTendersNum() {
        return winnerLastTendersNum;
    }

    public void setWinnerLastTendersNum(int winnerLastTendersNum) {
        this.winnerLastTendersNum = winnerLastTendersNum;
    }

    public double getWinnerLastWinRate() {
        return winnerLastWinRate;
    }

    public void setWinnerLastWinRate(double winnerLastWinRate) {
        this.winnerLastWinRate = winnerLastWinRate;
    }

    public int getWinnerRelToCustomer() {
        return winnerRelToCustomer;
    }

    public void setWinnerRelToCustomer(int winnerRelToCustomer) {
        this.winnerRelToCustomer = winnerRelToCustomer;
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

    public double getInitialSum() {
        return initialSum;
    }

    public void setInitialSum(double initialSum) {
        this.initialSum = initialSum;
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
                ", customerLastTendersNum=" + customerLastTendersNum +
                ", initialSum=" + initialSum +
                ", winnerLastTendersNum=" + winnerLastTendersNum +
                ", winnerLastWinRate=" + winnerLastWinRate +
                ", winnerRelToCustomer=" + winnerRelToCustomer +
                ", numOfParticipants=" + numOfParticipants +
                ", subject='" + subject + '\'' +
                '}';
    }
}
