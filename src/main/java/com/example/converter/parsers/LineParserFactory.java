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
        switch (type) {
            case 'P':
                return new PersonLineParser();
            case 'T':
                return new PhoneLineParser();
            case 'A':
                return new AddressLineParser();
            case 'F':
                return new FamilyLineParser();
            default:
                throw new IllegalArgumentException("Unknown record type: " + type);
        }
    }
}
