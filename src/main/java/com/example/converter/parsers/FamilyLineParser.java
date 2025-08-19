package com.example.converter.parsers;

import com.example.converter.records.FamilyRecord;
import com.example.converter.records.Record;

/**
 * Concrete implementation of LineParser for 'F' (Family) records.
 */
public class FamilyLineParser implements LineParser {
    @Override
    public Record parse(String line) throws IllegalArgumentException {
        // Split by '|' character (F | name | year of birth)
        String[] parts = line.split("\\|");

        String name = "";
        String yearOfBirth = "";

        switch (parts.length - 1) {
            case 1 -> name = parts[1];
            case 2 -> {
                name = parts[1];
                yearOfBirth = parts[2];
            }
            default -> System.out.println("default");
        }

        if (parts.length != 3 || !parts[0].equals("F")) {
            System.out.println("Invalid F record format: " + line);
        }
        return new FamilyRecord(name, yearOfBirth);
    }
}
