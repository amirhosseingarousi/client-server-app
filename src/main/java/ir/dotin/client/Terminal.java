package ir.dotin.client;

import ir.dotin.log.FileIO;
import ir.dotin.models.Transaction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Terminal {
    private static final String terminalXml = "src/main/resources/terminal.xml";
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private List<Transaction> transactions = new ArrayList<>();

    public Terminal(String address, int port) {
        FileIO fileIO = FileIO.getInstance();
        try {
            socket = new Socket(address, port);
            fileIO.terminalLog("Client connect to " + socket);
//            System.out.println("Client connect to " + socket);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            fileIO.terminalLog("Client can not connect to server!");
            throw new RuntimeException(e);
        }
    }

    public static String getAddress() {
        FileIO fileIO = FileIO.getInstance();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(terminalXml);
            doc.getDocumentElement().normalize();
            Element server = (Element) doc.getElementsByTagName("server").item(0);
            fileIO.terminalLog("Terminal read server address successfully -> " + server.getAttribute("ip"));
            return server.getAttribute("ip");
        } catch (ParserConfigurationException | SAXException | IOException e) {
            fileIO.terminalLog("Terminal cant read server ip!!");
            throw new RuntimeException(e);
        }
    }

    public static int getPort() {
        FileIO fileIO = FileIO.getInstance();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(terminalXml);
            doc.getDocumentElement().normalize();
            Element server = (Element) doc.getElementsByTagName("server").item(0);
            int port = Integer.parseInt(server.getAttribute("port"));
            fileIO.terminalLog("Terminal read port number " + port + " from config file successfully");
            return port;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            fileIO.terminalLog("Terminal can not read port number!!");
            throw new RuntimeException(e);
        }
    }

    public void getTransactions() {
        FileIO fileIO = FileIO.getInstance();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(terminalXml);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("transaction");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element element = (Element) node;
                String id = element.getAttribute("id");
                String type = element.getAttribute("type");
                String amount = element.getAttribute("amount").replace(",", "");
                String depositId = element.getAttribute("deposit");
                transactions.add(new Transaction(id, type, amount, depositId));
            }
            fileIO.terminalLog("There are #" + transactions.size() + " transactions in terminal");
        } catch (ParserConfigurationException | SAXException | IOException e) {
            fileIO.terminalLog("Terminal cant read transactions!!");
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        FileIO fileIO = FileIO.getInstance();
        String address = getAddress();
        int port = getPort();
//        System.out.println("address: " + address);
//        System.out.println("port: " + port);
        Terminal terminal = new Terminal(address, port);
        terminal.getTransactions();

        terminal.outputStream.writeObject(terminal.transactions);
        fileIO.terminalLog("Terminal send transactions to server...");

        try {
            Map<String, String> response = (Map<String, String>) terminal.inputStream.readObject();
            fileIO.terminalLog("Terminal get server response: " + response);
            System.out.println(response);
            WriteInXml wxd = new WriteInXml();
            wxd.createXmlFile(response);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
