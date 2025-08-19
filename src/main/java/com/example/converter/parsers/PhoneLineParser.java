package com.example.converter.parsers;

import com.example.converter.records.PhoneRecord;
import com.example.converter.records.Record;

/**
 * Concrete implementation of LineParser for 'T' (Phone) records.
 */
public class PhoneLineParser implements LineParser {
    @Override
    public Record parse(String line) throws IllegalArgumentException {
        // Split by '|' character (T | phonenumber | landlinenumber)
        String[] parts = line.split("\\|");

        String phonenumber = "";
        String landlinenumber = "";

        switch (parts.length - 1) {
            case 1 -> phonenumber = parts[1];
            case 2 -> {
                phonenumber = parts[1];
                landlinenumber = parts[2];
            }
            default -> System.out.println("default");
        }
        if (parts.length != 3 || !parts[0].equals("T")) {
            System.out.println("Invalid T record format: " + line);
        }
        return new PhoneRecord(phonenumber, landlinenumber);
    }
}
