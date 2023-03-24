package ir.dotin.models;

public class Deposit {
    private String id;
    private String customer;
    private String initialBalance;
    private String upperBound;

    public Deposit() {
    }

    public Deposit(String id, String customer, String initialBalance, String upperBound) {
        this.id = id;
        this.customer = customer;
        this.initialBalance = initialBalance;
        this.upperBound = upperBound;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getBalance() {
        return initialBalance;
    }

    public void setInitialBalance(String initialBalance) {
        this.initialBalance = initialBalance;
    }

    public String getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(String upperBound) {
        this.upperBound = upperBound;
    }

    @Override
    public String toString() {
        return "Deposit{" +
                "id='" + id + '\'' +
                ", customer='" + customer + '\'' +
                ", balance='" + initialBalance + '\'' +
                ", upperBound='" + upperBound + '\'' +
                '}';
    }
}
