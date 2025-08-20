package com.example.converter;

import com.example.converter.parsers.PersonLineParser;
import com.example.converter.records.PersonRecord;
import com.example.converter.records.Record;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

    /**
     * Unit tests for the PersonLineParser.
     * Verifies correct parsing of 'P' type lines and handles invalid formats.
     */
    class PersonLineParserTest {

        private PersonLineParser parser;

        @BeforeEach
        void setUp() { // Methods do not need to be public in JUnit 5
            parser = new PersonLineParser();
        }

        @Test
        void testParseValidPersonLine() {
            String line = "P|Carl Gustaf|Bernadotte";
            Record record = parser.parse(line);
            assertNotNull(record, "Record should not be null");
            assertTrue(record instanceof PersonRecord, "Record should be an instance of PersonRecord");
            PersonRecord person = (PersonRecord) record;
            assertEquals("Carl Gustaf", person.getFirstName(), "First name should match");
            assertEquals("Bernadotte", person.getLastName(), "Last name should match");
        }

        @Test
        void testParseInvalidPersonLineNull() {
            String line = null;
            assertThrows(NullPointerException.class, () -> parser.parse(line),
                    "Should throw NullPointerException for null line input to String.split()");
        }
    }