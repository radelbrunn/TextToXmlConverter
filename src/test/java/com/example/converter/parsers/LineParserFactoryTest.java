package com.example.converter.parsers;

import org.junit.jupiter.api.Test; // JUnit 5 @Test
import static org.junit.jupiter.api.Assertions.*; // JUnit 5 assertions

    /**
     * Unit tests for the LineParserFactory.
     * Verifies that the factory returns the correct parser instance for each record type.
     */
    class LineParserFactoryTest {

        @Test
        void testGetParserPerson() {
            LineParser parser = LineParserFactory.getParser('P');
            assertNotNull(parser, "Parser for 'P' should not be null");
            assertTrue(parser instanceof PersonLineParser, "Parser for 'P' should be an instance of PersonLineParser");
        }

        @Test
        void testGetParserPhone() {
            LineParser parser = LineParserFactory.getParser('T');
            assertNotNull(parser, "Parser for 'T' should not be null");
            assertTrue(parser instanceof PhoneLineParser, "Parser for 'T' should be an instance of PhoneLineParser");
        }

        @Test
        void testGetParserAddress() {
            LineParser parser = LineParserFactory.getParser('A');
            assertNotNull(parser, "Parser for 'A' should not be null");
            assertTrue(parser instanceof AddressLineParser, "Parser for 'A' should be an instance of AddressLineParser");
        }

        @Test
        void testGetParserFamily() {
            LineParser parser = LineParserFactory.getParser('F');
            assertNotNull(parser, "Parser for 'F' should not be null");
            assertTrue(parser instanceof FamilyLineParser, "Parser for 'F' should be an instance of FamilyLineParser");
        }

        @Test
        void testGetParserUnknownType() {
            assertThrows(IllegalArgumentException.class, () -> LineParserFactory.getParser('X'),
                    "Should throw IllegalArgumentException for unknown record type 'X'");
        }
    }