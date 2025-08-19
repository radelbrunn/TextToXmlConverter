package com.example.converter.parsers;


import com.example.converter.records.PersonRecord;
import com.example.converter.records.Record;

/**
 * Concrete implementation of LineParser for 'P' (Person) records.
 */
public class PersonLineParser implements LineParser {
    @Override
    public Record parse(String line) throws IllegalArgumentException {
        // Split by '|' character, limit to 3 parts (P | firstname | lastname)
        String[] parts = line.split("\\|", 3);
        if (parts.length != 3 || !parts[0].equals("P")) {
            throw new IllegalArgumentException("Invalid P record format: " + line);
        }
        return new PersonRecord(parts[1].trim(), parts[2].trim());
    }
}