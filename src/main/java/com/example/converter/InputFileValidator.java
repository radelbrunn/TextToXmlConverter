package com.example.converter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class InputFileValidator {
    public boolean validateFile(String filePath) {
        System.out.println("Validating input file: " + filePath);
        System.out.println("--------------------------------");
        boolean isValid = true;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split("\\|");

                if (parts.length == 0) {
                    System.out.println("Missing data on line " + lineNumber);
                    continue;
                }

                char recordType = parts[0].charAt(0);
                int expectedParts = 0;

                switch (recordType) {
                    case 'P': // P|firstname|lastname
                        expectedParts = 2;
                        break;
                    case 'T': // T|phonenumber|landline number
                        expectedParts = 2;
                        break;
                    case 'A': // A|street|city|post number
                        expectedParts = 3;
                        break;
                    case 'F': // F|name|year of birth
                        expectedParts = 2;
                        break;
                    default:
                        System.out.println("Invalid record type on line " + lineNumber);
                        continue;
                }
                if ((parts.length - 1) != expectedParts) {
                    System.out.println("Missing data on: " + parts[0] + " on line " + lineNumber + " expected: " + expectedParts + ", got: " + (parts.length - 1));
                    isValid = false;
                }
            }
            System.out.println("--------------------------------");
            System.out.println("File validation complete.");
            System.out.println(isValid ? "File is valid" : "File is not valid");
            System.out.println();
            if (!isValid) {
                System.out.println("File should be according to the following format");
                System.out.println("--------------------------------");
                System.out.println("P|firstname|lastname\n" +
                        "T|phonenumber|landline number\n" +
                        "A|street|city|post number\n" +
                        "F|name|year of birth");
                System.out.println("--------------------------------");

            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
            isValid = false;
        }
        return isValid;
    }
}
