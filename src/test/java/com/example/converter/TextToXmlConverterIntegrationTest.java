package com.example.converter;

import org.junit.jupiter.api.BeforeEach; // JUnit 5 @BeforeEach
import org.junit.jupiter.api.Test;    // JUnit 5 @Test
import org.junit.jupiter.api.io.TempDir; // JUnit 5 @TempDir for temporary directories

import static org.junit.jupiter.api.Assertions.*; // JUnit 5 assertions

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path; // Import Path for @TempDir

/**
 * Integration tests for the TextToXmlConverter.
 * These tests ensure the end-to-end functionality, reading from an input stream
 * and writing to an output stream, and verifying the generated XML content.
 */
class TextToXmlConverterIntegrationTest { // Class does not need to be public

    private TextToXmlConverter converter;

    @TempDir // JUnit 5 way to get a temporary directory
    Path tempDir; // tempDir will be injected by JUnit 5

    @BeforeEach
        // Use @BeforeEach for setup
    void setUp() { // Methods do not need to be public in JUnit 5
        converter = new TextToXmlConverter();
    }

    @Test
    void testConversionWithExampleInput() throws Exception {
        String inputContent =
                "P|Carl Gustaf|Bernadotte\n" +
                        "T|0768-101801|08-101801\n" +
                        "A|Drottningholms slott|Stockholm|10001\n" +
                        "F|Victoria|1977\n" +
                        "A|Haga Slott|Stockholm|10002\n" + // Address for Victoria
                        "F|Carl Philip|1979\n" +
                        "T|0768-101802|08-101802\n" + // Phone for Carl Philip
                        "P|Daniel|Westling\n" + // New person
                        "A|Kungliga slottet|Stockholm|10003"; // Address for Daniel

        String expectedXml =
                "<people>" +
                        "<person>" +
                        "<firstname>Carl Gustaf</firstname>" +
                        "<lastname>Bernadotte</lastname>" +
                        "<phone><mobile>0768-101801</mobile><landline>08-101801</landline></phone>" +
                        "<address><street>Drottningholms slott</street><city>Stockholm</city><postcode>10001</postcode></address>" +
                        "<family><name>Victoria</name><born>1977</born>" +
                        "<address><street>Haga Slott</street><city>Stockholm</city><postcode>10002</postcode></address>" +
                        "</family>" +
                        "<family><name>Carl Philip</name><born>1979</born>" +
                        "<phone><mobile>0768-101802</mobile><landline>08-101802</landline></phone>" +
                        "</family>" +
                        "</person>" +
                        "<person>" +
                        "<firstname>Daniel</firstname>" +
                        "<lastname>Westling</lastname>" +
                        "<address><street>Kungliga slottet</street><city>Stockholm</city><postcode>10003</postcode></address>" +
                        "</person>" +
                        "</people>";

        // Use ByteArrayInputStream and ByteArrayOutputStream for in-memory testing
        try (InputStream is = new ByteArrayInputStream(inputContent.getBytes(StandardCharsets.UTF_8));
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            converter.convert(is, os);
            String actualXml = os.toString(StandardCharsets.UTF_8.name());

            // Remove XML declaration and normalize whitespace for comparison
            actualXml = actualXml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "").replaceAll("\\s+", "");
            expectedXml = expectedXml.replaceAll("\\s+", "");

            assertEquals(expectedXml, actualXml, "Generated XML should match expected XML content.");
        }
    }

    @Test
    void testConversionWithInvalidFirstLine() {
        String inputContent = "T|0768-101801|08-101801"; // Invalid start: must be 'P'
        assertThrows(IllegalArgumentException.class, () -> {
            try (InputStream is = new ByteArrayInputStream(inputContent.getBytes(StandardCharsets.UTF_8));
                 ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                converter.convert(is, os);
            }
        }, "Should throw IllegalArgumentException for invalid first line.");
    }

    @Test
    void testConversionWithOnlyOnePerson() throws Exception {
        String inputContent =
                "P|Single|Person\n" +
                        "A|One Street|Some City|12345";
        String expectedXml =
                "<people>" +
                        "<person>" +
                        "<firstname>Single</firstname>" +
                        "<lastname>Person</lastname>" +
                        "<address><street>One Street</street><city>Some City</city><postcode>12345</postcode></address>" +
                        "</person>" +
                        "</people>";

        try (InputStream is = new ByteArrayInputStream(inputContent.getBytes(StandardCharsets.UTF_8));
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            converter.convert(is, os);
            String actualXml = os.toString(StandardCharsets.UTF_8.name());

            actualXml = actualXml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "").replaceAll("\\s+", "");
            expectedXml = expectedXml.replaceAll("\\s+", "");

            assertEquals(expectedXml, actualXml, "Generated XML for single person should be correct.");
        }
    }

    @Test
    void testConversionWithFileIO() throws Exception {
        // Create a temporary input file using @TempDir
        Path inputFile = tempDir.resolve("valid.txt");
        String inputContent = "P|Test|User\nF|Child|2000";
        Files.write(inputFile, inputContent.getBytes(StandardCharsets.UTF_8));

        // Create a temporary output file using @TempDir
        Path outputFile = tempDir.resolve("output.xml");

        // Perform conversion using file streams
        try (FileInputStream fis = new FileInputStream(inputFile.toFile());
             FileOutputStream fos = new FileOutputStream(outputFile.toFile())) {
            converter.convert(fis, fos);
        }

        // Read the generated XML
        String actualXml = new String(Files.readAllBytes(outputFile), StandardCharsets.UTF_8);
        String expectedXml =
                "<people>" +
                        "<person>" +
                        "<firstname>Test</firstname>" +
                        "<lastname>User</lastname>" +
                        "<family><name>Child</name><born>2000</born></family>" +
                        "</person>" +
                        "</people>";

        actualXml = actualXml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "").replaceAll("\\s+", "");
        expectedXml = expectedXml.replaceAll("\\s+", "");

        assertEquals(expectedXml, actualXml, "Generated XML from file should be correct.");
    }
}