package ir.dotin.models;

import java.io.Serializable;

public class Transaction implements Serializable {
    private String id;
    private String type;
    private String amount;
    private String depositId;

    public Transaction(String id, String type, String amount, String depositId) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.depositId = depositId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDepositId() {
        return depositId;
    }

    public void setDepositId(String depositId) {
        this.depositId = depositId;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", amount='" + amount + '\'' +
                ", depositId='" + depositId + '\'' +
                '}';
    }
}
