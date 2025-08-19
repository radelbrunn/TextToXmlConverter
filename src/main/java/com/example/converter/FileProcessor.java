package com.example.converter;

import com.example.converter.parsers.LineParser;
import com.example.converter.parsers.LineParserFactory;
import com.example.converter.records.Record;
import com.example.converter.states.InitialState;
import com.example.converter.states.ProcessingState;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Stack;

/**
 * The main processor that orchestrates reading text lines, parsing them,
 * and writing to XML. It acts as the Context in the State design pattern,
 * holding the current state and delegating operations.
 * It's designed to process large files efficiently by streaming output.
 */
public class FileProcessor {
    private ProcessingState currentState;
    private XMLStreamWriter xmlWriter;
    private Stack<String> elementStack; // Manages the stack of open XML elements for correct nesting

    /**
     * Constructs a new FileProcessor.
     * Initializes the XMLStreamWriter and sets the initial processing state.
     *
     * @param outputStream The OutputStream to which the XML will be written.
     * @throws XMLStreamException If an error occurs during XML writer initialization.
     */
    public FileProcessor(OutputStream outputStream) throws XMLStreamException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        this.xmlWriter = factory.createXMLStreamWriter(writer);
        this.xmlWriter.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");
        this.currentState = new InitialState();
        this.elementStack = new Stack<>();
    }
    /**
     * Processes a single line from the input text file.
     * It identifies the record type, gets the appropriate parser from the factory,
     * parses the line into a Record object, and then delegates the processing
     * to the current state.
     *
     * @param line The line of text to process.
     * @throws XMLStreamException If an XML writing error occurs during state processing.
     * @throws IllegalArgumentException If the line format is invalid or the record type
     * is not allowed in the current sequence (as per state rules).
     */
    public void processLine(String line) throws XMLStreamException, IllegalArgumentException {
        // Skip empty or blank lines
        if (line == null || line.trim().isEmpty()) {
            return;
        }

        char recordType = line.charAt(0); // Get the first character to determine record type
        LineParser parser = LineParserFactory.getParser(recordType); // Get the correct parser using the Factory
        Record record = parser.parse(line); // Parse the line into a Record object

        // Delegate processing to the current state. The state will also handle
        // state transitions internally and return the next state.
        currentState = currentState.process(recordType, record, xmlWriter, elementStack);
    }

    /**
     * Closes any remaining open XML elements and the XML writer.
     * This method must be called after all lines have been processed to ensure
     * a well-formed XML document (e.g., closing the root 'people' element).
     *
     * @throws XMLStreamException If an XML writing error occurs during closing.
     */
    public void close() throws XMLStreamException {
        while (!elementStack.isEmpty()) {
            xmlWriter.writeEndElement();
            elementStack.pop();
        }
        xmlWriter.writeEndDocument();
        xmlWriter.flush();
        xmlWriter.close();
    }
}