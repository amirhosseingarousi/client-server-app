package ir.dotin.log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileIO {
    private static FileIO instance;

    private FileIO() {}

    public static FileIO getInstance() {
        if (instance == null) {
            instance = new FileIO();
        }
        return instance;
    }

    public void terminalLog(String message) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/ir/dotin/log/term21374.log", true));
            writer.append("\n" + message);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void serverLog(String message) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/ir/dotin/log/server.out", true));
            writer.append("\n" + message);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
