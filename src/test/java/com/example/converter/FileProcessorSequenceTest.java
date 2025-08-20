package com.example.converter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import javax.xml.stream.XMLStreamException;
import java.nio.charset.StandardCharsets;

    /**
     * Unit tests for the FileProcessor focusing on sequence validation and XML structure.
     * These tests verify the State Pattern logic for valid and invalid record sequences.
     */
    class FileProcessorSequenceTest {

        private ByteArrayOutputStream outputStream;
        private FileProcessor processor;

        @BeforeEach
        void setUp() throws XMLStreamException {
            outputStream = new ByteArrayOutputStream();
            processor = new FileProcessor(outputStream);
        }

        @Test
        void testValidSequenceP_T_A_F_T_A_P() throws Exception {
            // This sequence follows the rules:
            // P can be followed by T, A, F.
            // F can be followed by T, A.
            processor.processLine("P|Carl Gustaf|Bernadotte");
            processor.processLine("T|0768-101801|08-101801"); // Phone for Carl Gustaf
            processor.processLine("A|Drottningholms slott|Stockholm|10001"); // Address for Carl Gustaf
            processor.processLine("F|Victoria|1977"); // Family member Victoria
            processor.processLine("T|0768-101802|08-101802"); // Phone for Victoria (under family)
            processor.processLine("A|Haga Slott|Stockholm|10002"); // Address for Victoria (under family)
            processor.processLine("F|Carl Philip|1979"); // New family member, closes Victoria's family, opens Carl Philip's
            processor.processLine("T|0768-101803|08-101803"); // Phone for Carl Philip
            processor.processLine("P|Daniel|Westling"); // New person: Daniel, closes Carl Philip's family and Carl Gustaf's person
            processor.close(); // Ensures all open tags are closed

            String expectedXmlPart = "<people>" +
                    "<person>" +
                    "<firstname>Carl Gustaf</firstname>" +
                    "<lastname>Bernadotte</lastname>" +
                    "<phone><mobile>0768-101801</mobile><landline>08-101801</landline></phone>" +
                    "<address><street>Drottningholms slott</street><city>Stockholm</city><postcode>10001</postcode></address>" +
                    "<family><name>Victoria</name><born>1977</born>" +
                    "<phone><mobile>0768-101802</mobile><landline>08-101802</landline></phone>" +
                    "<address><street>Haga Slott</street><city>Stockholm</city><postcode>10002</postcode></address>" +
                    "</family>" +
                    "<family><name>Carl Philip</name><born>1979</born>" +
                    "<phone><mobile>0768-101803</mobile><landline>08-101803</landline></phone>" +
                    "</family>" +
                    "</person>" +
                    "<person>" +
                    "<firstname>Daniel</firstname>" +
                    "<lastname>Westling</lastname>" +
                    "</person>" +
                    "</people>";

            String actualXml = outputStream.toString(StandardCharsets.UTF_8.name());
            // Remove XML declaration and normalize whitespace for robust comparison
            actualXml = actualXml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "").replaceAll("\\s+", "");
            expectedXmlPart = expectedXmlPart.replaceAll("\\s+", "");

            // Assert that the actual XML contains the expected structured part.
            assertTrue(actualXml.contains(expectedXmlPart),
                    "Generated XML does not contain expected part.\nExpected: " + expectedXmlPart + "\nActual: " + actualXml);
        }

        @Test
        void testInvalidSequenceStartWithT() {
            // Rule: Must start with 'P'.
            assertThrows(IllegalArgumentException.class, () -> processor.processLine("T|0768-101801|08-101801"),
                    "Should throw IllegalArgumentException for starting with 'T'");
        }

        @Test
        void testMultipleFamiliesUnderOnePerson() throws Exception {
            processor.processLine("P|Carl Gustaf|Bernadotte");
            processor.processLine("F|Victoria|1977");
            processor.processLine("F|Carl Philip|1979"); // This should close Victoria's family and open Carl Philip's
            processor.close();

            String expectedXmlPart = "<people>" +
                    "<person>" +
                    "<firstname>Carl Gustaf</firstname>" +
                    "<lastname>Bernadotte</lastname>" +
                    "<family><name>Victoria</name><born>1977</born></family>" +
                    "<family><name>Carl Philip</name><born>1979</born></family>" +
                    "</person>" +
                    "</people>";
            String actualXml = outputStream.toString(StandardCharsets.UTF_8.name())
                    .replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "")
                    .replaceAll("\\s+", "");
            expectedXmlPart = expectedXmlPart.replaceAll("\\s+", "");
            assertTrue(actualXml.contains(expectedXmlPart),
                    "Generated XML should contain multiple family entries: " + actualXml);
        }

        @Test
        void testInvalidSequenceP_then_invalidChar() throws Exception {
            // 'X' is not a defined record type (P, T, A, F)
            processor.processLine("P|Carl Gustaf|Bernadotte");
            assertThrows(IllegalArgumentException.class, () -> processor.processLine("X|Invalid|Line"),
                    "Should throw IllegalArgumentException for an unknown record type after 'P'");
        }

        @Test
        void testP_A_T_F_A_T_P_T_A() throws Exception {
            processor.processLine("P|Person1|Last1");
            processor.processLine("A|Street1|City1|Post1");
            processor.processLine("T|111|222");
            processor.processLine("F|Family1|1990");
            processor.processLine("A|StreetF1|CityF1|PostF1"); // Address for Family1
            processor.processLine("T|333|444"); // Phone for Family1
            processor.processLine("P|Person2|Last2"); // New Person, closes previous structures
            processor.processLine("T|555|666"); // Phone for Person2
            processor.processLine("A|Street2|City2|Post2"); // Address for Person2
            processor.close();

            String expectedXmlPart = "<people>" +
                    "<person>" +
                    "<firstname>Person1</firstname><lastname>Last1</lastname>" +
                    "<address><street>Street1</street><city>City1</city><postcode>Post1</postcode></address>" +
                    "<phone><mobile>111</mobile><landline>222</landline></phone>" +
                    "<family><name>Family1</name><born>1990</born>" +
                    "<address><street>StreetF1</street><city>CityF1</city><postcode>PostF1</postcode></address>" +
                    "<phone><mobile>333</mobile><landline>444</landline></phone>" +
                    "</family>" +
                    "</person>" +
                    "<person>" +
                    "<firstname>Person2</firstname><lastname>Last2</lastname>" +
                    "<phone><mobile>555</mobile><landline>666</landline></phone>" +
                    "<address><street>Street2</street><city>City2</city><postcode>Post2</postcode></address>" +
                    "</person>" +
                    "</people>";
            String actualXml = outputStream.toString(StandardCharsets.UTF_8.name())
                    .replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "")
                    .replaceAll("\\s+", "");
            expectedXmlPart = expectedXmlPart.replaceAll("\\s+", "");
            assertTrue(actualXml.contains(expectedXmlPart),
                    "Generated XML for P_A_T_F_A_T_P_T_A sequence is incorrect.");
        }
    }