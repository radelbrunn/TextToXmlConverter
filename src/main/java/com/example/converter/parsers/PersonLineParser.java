package com.example.converter.parsers;

import com.example.converter.records.PersonRecord;
import com.example.converter.records.Record;

/**
 * Concrete implementation of LineParser for 'P' (Person) records.
 */
public class PersonLineParser implements LineParser {
    @Override
    public Record parse(String line) throws IllegalArgumentException {
        // Split by '|' character (P | firstname | lastname)
        String[] parts = line.split("\\|");

        String firstname = "";
        String lastname = "";

        switch (parts.length - 1) {
            case 1 -> firstname = parts[1];
            case 2 -> {
                firstname = parts[1];
                lastname = parts[2];
            }
            default -> System.out.println("default");
        }

        if (parts.length != 3 || !parts[0].equals("P")) {
            System.out.println("Invalid P record format: " + line);
        }
        return new PersonRecord(firstname, lastname);
    }
}