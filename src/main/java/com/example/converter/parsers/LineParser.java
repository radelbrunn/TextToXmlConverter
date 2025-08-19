package com.example.converter.parsers;

import com.example.converter.records.Record;

/**
 * Strategy interface for parsing a line of text into a specific Record type.
 * This is part of the Strategy design pattern.
 */
public interface LineParser {
    /**
     * Parses a single line of text into a Record object.
     * @param line The input line to parse.
     * @return A Record object representing the parsed data.
     * @throws IllegalArgumentException If the line format is invalid or cannot be parsed.
     */
    Record parse(String line) throws IllegalArgumentException;
}