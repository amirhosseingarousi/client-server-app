package ir.dotin.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.dotin.log.FileIO;
import ir.dotin.models.Deposit;
import ir.dotin.models.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.*;

public class ClientHandler extends Thread {
    private static final String coreJson = "src/main/resources/core.json";

    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Map<String, String> response;

    public ClientHandler(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        response = new HashMap<>();
    }

    public ClientHandler() {
    }

    @Override
    public void run() {
        FileIO fileIO = FileIO.getInstance();
        List<Transaction> transactions = receiveRequest();

        Server server = new Server();
        server.getAllDeposits();
        List<Deposit> deposits = server.getDeposits();
        fileIO.serverLog("There are #" + deposits.size() + " deposits on server");
        try {
            processRequest(transactions, deposits);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            outputStream.writeObject(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        System.out.println(response);
    }

    public List<Transaction> receiveRequest() {
        try {
            List<Transaction> transactions = (List<Transaction>) inputStream.readObject();
            System.out.println("Received #" + transactions.size() + " req.");
            return transactions;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void processRequest(List<Transaction> transactions, List<Deposit> deposits) throws IOException {
        FileIO fileIO = FileIO.getInstance();
        for (Transaction transaction : transactions) {
            // validate req...
            try {
                validateTransaction(transaction);
                String depositId = transaction.getDepositId();
                for (Deposit deposit : deposits) {
                    String id = deposit.getId();
                    if (depositId.equals(id)) {
                        if (transaction.getType().equals("deposit")) {
                            deposit(transaction, deposit);
                            fileIO.serverLog("Transaction id: " + transaction.getId() + " done");
                            response.put(transaction.getId(), "Successful");
//                            fileIO.serverLog("Transaction id:" + transaction.getId() + " made successfully.");
                        } else if (transaction.getType().equals("withdraw")) {
                            withdraw(transaction, deposit);
                            fileIO.serverLog("Transaction id: " + transaction.getId() + " done");
                            response.put(transaction.getId(), "Successful");
//                            fileIO.serverLog("Transaction id:" + transaction.getId() + " made successfully.");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                fileIO.serverLog("Transaction id: " + transaction.getId() + " Failed!!");
                response.put(transaction.getId(), "Failed");
//                fileIO.serverLog("Transaction id:" + transaction.getId() + " failed");
            }
        }
    }

    public void validateTransaction(Transaction transaction) {
        FileIO fileIO = FileIO.getInstance();
        if (!("deposit".equals(transaction.getType()) || "withdraw".equals(transaction.getType()))) {
            fileIO.serverLog("Transaction type: " + transaction.getType() + " is invalid");
            throw new RuntimeException("Transaction type " + transaction.getType() + " is invalid!");
        }

        String amount = transaction.getAmount().replace(", ", "");
        double amountValue = Double.parseDouble(amount);
        if (amountValue < 0) {
            fileIO.serverLog("Transaction amount is negative");
            throw new RuntimeException("Transaction amount cannot be negative!");
        }

        Server server = new Server();
        server.getAllDeposits();
        boolean flag = false;
        for (Deposit deposit : server.getDeposits()) {
            if (transaction.getDepositId().equals(deposit.getId())) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            fileIO.serverLog("Transaction id: " + transaction.getDepositId() + " not exist!");
            throw new RuntimeException("Transaction id: " + transaction.getDepositId() + " not exist!");
        }
    }


    private void deposit(Transaction transaction, Deposit deposit) {
        String balance = deposit.getBalance().replace(",", "");
        String amount = transaction.getAmount().replace(",", "");
        BigDecimal bd = new BigDecimal(balance);
        bd = bd.add(new BigDecimal(amount));
        deposit.setInitialBalance(bd.toString());
    }

    private void withdraw(Transaction transaction, Deposit deposit) {
        FileIO fileIO = FileIO.getInstance();
        String balance = deposit.getBalance().replace(",", "");
        String amount = transaction.getAmount().replace(",", "");
        BigDecimal bd = new BigDecimal(balance);
        bd = bd.subtract(new BigDecimal(amount));
        if (bd.doubleValue() < 0) {
            fileIO.serverLog("Insufficient balance deposit: " + deposit);
            throw new RuntimeException("Insufficient balance!");
        }
        deposit.setInitialBalance(bd.toString());
    }


    public static void main(String[] args) throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(coreJson));
        JSONObject jo = (JSONObject) obj;
        JSONArray ja = (JSONArray) jo.get("deposits");

        ObjectMapper mapper = new ObjectMapper();
        List<Deposit> deposits1 = Arrays.asList(mapper.readValue(ja.toJSONString(), Deposit[].class));
        System.out.println(deposits1);
    }
}
