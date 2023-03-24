package ir.dotin.client;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

public class WriteInXml {
    private static final String responsePath = "src/main/java/ir/dotin/client/response.xml";

    public void createXmlFile(Map<String, String> map) {
//        FileIO fileIO = FileIO.getInstance();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("response");
            doc.appendChild(root);

            Element transactions = doc.createElement("transactions");
            root.appendChild(transactions);

            for (Map.Entry<String, String> set : map.entrySet()) {
                Element transaction = doc.createElement("transaction");
                transactions.appendChild(transaction);
                transaction.setAttribute("id", set.getKey());
                transaction.setAttribute("message", set.getValue());
            }

            FileOutputStream output = new FileOutputStream(responsePath);
            writeXml(doc, output);
//            fileIO.terminalLog("Terminal create Response file in xml");
        } catch (ParserConfigurationException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeXml(Document doc, OutputStream output) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(output);

            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

}
