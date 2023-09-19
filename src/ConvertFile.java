import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;


public class ConvertFile {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java ConvertFile <filename> -<code>");
            System.exit(1);

        }
    try {
        convertFile(args[0], args[1]);
    }
    catch (Exception ex) {
        ex.printStackTrace();
    }
    }

    public static void convertFile(String sourceFile, String code) throws Exception {
        switch (code) {
            case "-c": convertToCSV(sourceFile);
            case "-j": convertToJSON(sourceFile);
            case "-x": convertToXML(sourceFile);
        }
    }

    public static void convertToCSV(String filename) {
        String outputFile = filename.split("\\.")[0] + ".csv";
        try (
                BufferedReader reader = new BufferedReader(new FileReader(filename));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))
                ) {
            String line;
            while ((line = reader.readLine()) != null) { //Read every line if not null
                writer.write(line.replace("\t", ",")); //Write to output file and replace Tab delimiter with commas
                writer.newLine(); //Start a new line when finish writing a line
            }
            System.out.println("Converted " + filename + " to CSV format");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            }
    }

    public static void convertToJSON(String filename) throws Exception {
        String outputFile = filename.split("\\.")[0] + ".json";
        List<String> jsonArray = new ArrayList<>();

        // Create an Array of Jason Objects
        try (
                BufferedReader reader = new BufferedReader(new FileReader(filename))
                ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\t"); //Create all fields in a line as an array
                StringBuilder jsonField = new StringBuilder("{"); //Add each field in a line to a jason bracket
                for (int i = 0; i < fields.length; i++) {
                    if (i != 0) jsonField.append(","); //Add a separator between each field
                    jsonField.append("\"field_" + i + "\":\"" + fields[i] + "\"");
                }
                jsonField.append("}"); //Add closing field bracket
                jsonArray.add(jsonField.toString()); //Add all jason objects to the array list
            }
        }

        // write jason list to the output file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("["); //Start a JSON file
            for (int i = 0; i < jsonArray.size(); i ++) {
                if (i != 0) writer.write(",");
                writer.write(jsonArray.get(i));
            }
            writer.write("]"); //Close a JSON file
            System.out.println("Converted " + filename + " to JSON format");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void convertToXML(String filename) throws Exception {
        List<String[]> dataList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        List<String> sanitizedTitles = new ArrayList<>();
        String outputFile = filename.split("\\.")[0] + ".xml";

        try (
                BufferedReader reader = new BufferedReader(new FileReader(filename))
                ) {
            String line;

            if ((line = reader.readLine()) != null) {
                String[] titles = line.split("\t");
                for (String title : titles)
                    titleList.add(title);
            }

            for (String title : titleList) {
                // Remove invalid characters or replace them with underscores
                String sanitizedTitle = title.replaceAll("[^a-zA-Z0-9_]", "_");
                // Ensure the title starts with a valid character (e.g., a letter or underscore)
                if (!Character.isLetter(sanitizedTitle.charAt(0)) && sanitizedTitle.charAt(0) != '_') {
                    sanitizedTitle = "_" + sanitizedTitle;
                }
                sanitizedTitles.add(sanitizedTitle);
            }

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\t");
                dataList.add(fields);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element root = document.createElement("data"); //Create root element of the document
        document.appendChild(root);

        for (String[] dataField : dataList) {
            Element item = document.createElement("item");
            for (int i = 0; i < dataField.length; i++) {
                String title = sanitizedTitles.get(i);
                Element field = document.createElement(title);
                field.appendChild(document.createTextNode(dataField[i]));
                item.appendChild(field);
            }
            root.appendChild(item);
        }

        TransformerFactory transformerFact = TransformerFactory.newInstance();
        Transformer transformer = transformerFact.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult output = new StreamResult(new File(outputFile));
        transformer.transform(source, output);

        System.out.println("Converted " + filename + " to XML format");
    }
}