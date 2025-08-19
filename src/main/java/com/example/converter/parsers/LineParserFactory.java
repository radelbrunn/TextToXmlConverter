package com.example.converter.parsers;

/**
 * Factory for creating LineParser instances based on the record type.
 * This is part of the Factory Method design pattern.
 */
public class LineParserFactory {
    /**
     * Returns the appropriate LineParser for a given record type.
     * @param type The character representing the record type (P, T, A, F).
     * @return An instance of LineParser.
     * @throws IllegalArgumentException If the type is unknown or no parser is defined for it.
     */
    public static LineParser getParser(char type) throws IllegalArgumentException {
        return switch (type) {
            case 'P' -> new PersonLineParser();
            case 'T' -> new PhoneLineParser();
            case 'A' -> new AddressLineParser();
            case 'F' -> new FamilyLineParser();
            default -> throw new IllegalArgumentException("Unknown record type: " + type);
        };
    }
}
