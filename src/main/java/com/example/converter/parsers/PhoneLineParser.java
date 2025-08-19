package com.example.converter.parsers;


import com.example.converter.records.PhoneRecord;
import com.example.converter.records.Record;

/**
 * Concrete implementation of LineParser for 'T' (Phone) records.
 */
public class PhoneLineParser implements LineParser {
    @Override
    public Record parse(String line) throws IllegalArgumentException {
        // Split by '|' character, limit to 3 parts (T | phonenumber | landlinenumber)
        String[] parts = line.split("\\|", 3);
        if (parts.length != 3 || !parts[0].equals("T")) {
            throw new IllegalArgumentException("Invalid T record format: " + line);
        }
        return new PhoneRecord(parts[1].trim(), parts[2].trim());
    }
}
