package com.example.converter.parsers;

import com.example.converter.records.FamilyRecord;
import com.example.converter.records.Record;

/**
 * Concrete implementation of LineParser for 'F' (Family) records.
 */
public class FamilyLineParser implements LineParser {
    @Override
    public Record parse(String line) throws IllegalArgumentException {
        // Split by '|' character, limit to 3 parts (F | name | year of birth)
        String[] parts = line.split("\\|", 3);
        if (parts.length != 3 || !parts[0].equals("F")) {
            throw new IllegalArgumentException("Invalid F record format: " + line);
        }
        return new FamilyRecord(parts[1].trim(), parts[2].trim());
    }
}
