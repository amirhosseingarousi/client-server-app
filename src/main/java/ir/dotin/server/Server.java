package ir.dotin.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.dotin.log.FileIO;
import ir.dotin.models.Deposit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {
    private static final String coreJson = "src/main/resources/core.json";
    private long port;
    private List<Deposit> deposits = new ArrayList<>();

    public void getAllDeposits() {
        FileIO fileIO = FileIO.getInstance();
        try {
            Object obj = new JSONParser().parse(new FileReader(coreJson));
            JSONObject jo = (JSONObject) obj;
            JSONArray ja = (JSONArray) jo.get("deposits");

            ObjectMapper mapper = new ObjectMapper();
            deposits = Arrays.asList(mapper.readValue(ja.toJSONString(), Deposit[].class));
        } catch (IOException | ParseException e) {
            fileIO.serverLog("Server cant read deposits!");
            throw new RuntimeException(e);
        }
    }

    public List<Deposit> getDeposits() {
        return deposits;
    }

    public static void main(String[] args) throws IOException, ParseException {
        FileIO fileIO = FileIO.getInstance();
        Server server = new Server();
        server.getPortNumber();

        ServerSocket serverSocket = new ServerSocket((int) server.port);

        while (true) {
            Socket socket = null;
            socket = serverSocket.accept();
            fileIO.serverLog("A new client is connected : " + socket);
            System.out.println("A new client is connected : " + socket);

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());


            System.out.println("Assigning new thread for this client");
            Thread t = new ClientHandler(socket, inputStream, outputStream);
            fileIO.serverLog("Assigning new thread for this client");
            t.start();
        }
    }

    private void getPortNumber() {
        FileIO fileIO = FileIO.getInstance();
        try {
            Object obj = new JSONParser().parse(new FileReader(coreJson));
            JSONObject jo = (JSONObject) obj;
            port = (long) jo.get("port");
            fileIO.serverLog("Server read port number " + port + " from config file successfully");
        } catch (IOException | ParseException e) {
            fileIO.serverLog("Server cant read port number!");
            throw new RuntimeException(e);
        }
    }
}
