package com.example.converter.parsers;

import com.example.converter.records.AddressRecord;
import com.example.converter.records.Record;

/**
 * Concrete implementation of LineParser for 'A' (Address) records.
 */
public class AddressLineParser implements LineParser {
    @Override
    public Record parse(String line) {
        // Split by '|' character (A | street | city | post number)
        String[] parts = line.split("\\|");

        String street = "";
        String city = "";
        String postcode = "";

        switch (parts.length - 1) {
            case 1 -> street = parts[1];
            case 2 -> {
                street = parts[1];
                city = parts[2];
            }
            case 3 -> {
                street = parts[1];
                city = parts[2];
                postcode = parts[3];
            }
            default -> System.out.println("default");
        }

        if (parts.length != 4 || !parts[0].equals("A")) {
            System.out.println("Invalid A record format: " + line);
        }
        return new AddressRecord(street, city, postcode);
    }
}
