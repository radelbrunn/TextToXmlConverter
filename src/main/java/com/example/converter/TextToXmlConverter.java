package com.example.converter;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.Scanner;

/**
 * Main class for converting a text file to an XML file.
 * This class provides the entry point for the conversion process,
 * handling file I/O and orchestrating the FileProcessor.
 */
public class TextToXmlConverter {

    /**
     * Converts the content from a given InputStream (text file) to an OutputStream (XML file).
     * This method uses the FileProcessor to process the input line by line,
     * which is efficient for large files as it avoids loading the entire file into memory.
     *
     * @param inputStream  The input stream from the text file.
     * @param outputStream The output stream for the XML file.
     * @throws IOException              If an I/O error occurs during file reading or writing.
     * @throws XMLStreamException       If an XML writing error occurs.
     * @throws IllegalArgumentException If there's an issue with record format or sequence
     *                                  according to the defined rules.
     */
    public void convert(InputStream inputStream, OutputStream outputStream)
            throws IOException, XMLStreamException, IllegalArgumentException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            FileProcessor processor = new FileProcessor(outputStream);
            String line;
            while ((line = reader.readLine()) != null) {
                processor.processLine(line);
            }
            processor.close();
        }
    }

    /**
     * Main method for running the converter from the command line.
     * It expects two command-line arguments: the input text file path and the output XML file path.
     *
     * @param args Command-line arguments: [0] = inputFile, [1] = outputFile
     */
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("Usage: java -jar TextToXmlConverter.jar <inputFile> <outputFile>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        InputFileValidator inputFileValidator = new InputFileValidator();
        boolean isValidFile = inputFileValidator.validateFile(inputFile);
        if (!isValidFile) {
            String userChoice = getYesOrNoFromUser();
            if (userChoice.equals("yes")) {
                System.out.println("Proceeding with generation...");
                generateXML(inputFile, outputFile);
            } else {
                System.out.println("Aborting.");
            }
        } else {
            generateXML(inputFile, outputFile);
        }
    }

    private static void generateXML(String inputFile, String outputFile) {
        TextToXmlConverter converter = new TextToXmlConverter();
        try (InputStream is = new FileInputStream(inputFile);
             OutputStream os = new FileOutputStream(outputFile)) {
            System.out.println("Starting conversion from " + inputFile + " to " + outputFile);
            converter.convert(is, os);
            System.out.println("Conversion completed successfully.");
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found. Please check the file path. " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading or writing files: " + e.getMessage());
            e.printStackTrace();
        } catch (XMLStreamException e) {
            System.err.println("Error writing XML content. The XML structure might be malformed or an internal XML stream error occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Error in input data format or sequence: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during conversion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getYesOrNoFromUser() {
        try (Scanner scanner = new Scanner(System.in)) {
            String userInput;

            while (true) {
                System.out.println("The input file was not valid, generate the XML anyway? (yes/no)");
                userInput = scanner.nextLine().trim().toLowerCase();

                if (userInput.equals("yes")) {
                    return "yes";
                } else if (userInput.equals("no")) {
                    return "no";
                } else {
                    System.out.println("Invalid input. Please try again.");
                }
            }
        }
    }
}