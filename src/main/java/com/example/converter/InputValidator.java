package com.example.converter;

import com.example.converter.parsers.LineParserFactory;
import com.example.converter.records.Record;
// These imports below are not strictly necessary for the validator's logic,
// but are good for general context in the records package.
import com.example.converter.records.PersonRecord;
import com.example.converter.records.FamilyRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates the format and sequence of records in the input text file.
 * This class performs a preprocessing step to ensure the input is well-formed
 * before attempting to convert it to XML.
 */
public class InputValidator {

    /**
     * Represents the current state of the validation process.
     */
    private enum ValidationState {
        INITIAL,      // Expecting a 'P' record to start
        IN_PERSON,    // Inside a 'P' record, expecting 'T', 'A', 'F', or new 'P'
        IN_FAMILY     // Inside an 'F' record, expecting 'T', 'A', or new 'P'/'F'
    }

    /**
     * Validates the input stream line by line, checking for format and sequence errors.
     *
     * @param inputStream The input stream containing the text data.
     * @return A list of error messages found during validation. If the list is empty, the input is considered valid.
     * @throws IOException If an I/O error occurs while reading the input stream.
     */
    public List<String> validate(InputStream inputStream) throws IOException {
        List<String> errors = new ArrayList<>();
        ValidationState currentState = ValidationState.INITIAL;
        int lineNumber = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmedLine = line.trim();

                // Skip empty lines in validation; the XML FileProcessor also skips them.
                if (trimmedLine.isEmpty()) {
                    continue;
                }

                char recordType;
                try {
                    recordType = trimmedLine.charAt(0);
                } catch (StringIndexOutOfBoundsException e) {
                    errors.add(String.format("Line %d: Empty line or line too short to determine record type: '%s'", lineNumber, line));
                    continue;
                }

                // First, check if the record type character itself is valid.
                if (!isValidRecordType(recordType)) {
                    errors.add(String.format("Line %d: Invalid record type '%c'. Expected P, T, A, or F.", lineNumber, recordType));
                    continue;
                }

                // Attempt to parse the record to check field count and basic format.
                // We use LineParserFactory.getParser().parse() which throws IllegalArgumentException on format errors.
                // The returned 'record' object itself isn't used in validator, but the parse call is for its error checking.
                try {
                    LineParserFactory.getParser(recordType).parse(trimmedLine);
                } catch (IllegalArgumentException e) {
                    errors.add(String.format("Line %d: Formatting error for record type '%c': %s (Line: '%s')", lineNumber, recordType, e.getMessage(), line));
                    // Even if parsing fails, we continue to check sequence, as we want to collect all errors.
                }

                // Now, perform sequence validation based on current state and record type.
                // This ensures logical flow of records (e.g., A 'P' must start a new block).
                switch (currentState) {
                    case INITIAL:
                        if (recordType == 'P') {
                            currentState = ValidationState.IN_PERSON;
                        } else {
                            errors.add(String.format("Line %d: Unexpected record type '%c'. File must start with a 'P' record.", lineNumber, recordType));
                        }
                        break;
                    case IN_PERSON:
                        // T, A, F are allowed under P. A new P indicates a new person.
                        if (recordType == 'P') {
                            // Valid transition: new person starts. State remains IN_PERSON for the new person.
                        } else if (recordType == 'F') {
                            currentState = ValidationState.IN_FAMILY; // Transition to IN_FAMILY state.
                        } else if (recordType != 'T' && recordType != 'A') {
                            errors.add(String.format("Line %d: Invalid record type '%c' in person context. Expected T, A, F, or P.", lineNumber, recordType));
                        }
                        // For T and A, stay in IN_PERSON state as they are direct children of person.
                        break;
                    case IN_FAMILY:
                        // T, A are allowed under F. A new P or F indicates a new person or sibling family.
                        if (recordType == 'P') {
                            currentState = ValidationState.IN_PERSON; // Valid transition: back to person context.
                        } else if (recordType == 'F') {
                            // Another 'F' means a sibling family member. State remains IN_FAMILY for the new sibling.
                        } else if (recordType != 'T' && recordType != 'A') {
                            errors.add(String.format("Line %d: Invalid record type '%c' in family context. Expected T, A, F, or P.", lineNumber, recordType));
                        }
                        // For T and A, stay in IN_FAMILY state as they are direct children of family.
                        break;
                }
            }
        }

        // Final check: If the file was not empty, but never properly started with a 'P' record.
        // This is a safeguard, as the INITIAL state check should ideally catch this on line 1.
        if (lineNumber > 0 && currentState == ValidationState.INITIAL && errors.isEmpty()) {
            errors.add("File did not start with a valid 'P' record.");
        }

        return errors;
    }

    /**
     * Checks if a given character represents a valid record type (P, T, A, F).
     * @param type The character to check.
     * @return true if the type is valid, false otherwise.
     */
    private boolean isValidRecordType(char type) {
        return type == 'P' || type == 'T' || type == 'A' || type == 'F';
    }
}
