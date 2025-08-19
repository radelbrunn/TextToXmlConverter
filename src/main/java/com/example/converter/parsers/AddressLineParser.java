package com.example.converter.parsers;

import com.example.converter.records.AddressRecord;
import com.example.converter.records.Record;

/**
 * Concrete implementation of LineParser for 'A' (Address) records.
 */
public class AddressLineParser implements LineParser {
    @Override
    public Record parse(String line) throws IllegalArgumentException {
        // Split by '|' character, limit to 4 parts (A | street | city | post number)
        String[] parts = line.split("\\|", 4);

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
//        System.out.println(street + ", " + city + ", " + postcode);

        if (parts.length != 4 || !parts[0].equals("A")) {
//            throw new IllegalArgumentException("Invalid A record format: " + line);
        }
//        return new AddressRecord(parts[1].trim(), parts[2].trim(), parts[3].trim());
        return new AddressRecord(street, city, postcode);
    }
}
