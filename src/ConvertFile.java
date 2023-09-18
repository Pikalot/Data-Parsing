import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;

import javax.xml.parsers.DocumentBuilderFactory;
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

    public static void convertToXML(String filename) {
        // Building
    }
}