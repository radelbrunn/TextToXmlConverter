// src/main/java/com/example/converter/FileProcessor.java
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
import java.io.UnsupportedEncodingException;
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
 /*   public FileProcessor(OutputStream outputStream) throws XMLStreamException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        // Create an XMLStreamWriter. The output will be UTF-8 encoded.
        this.xmlWriter = factory.createXMLStreamWriter(outputStream, "UTF-8");
        // Write the XML declaration at the very beginning of the document
        this.xmlWriter.writeStartDocument("1.0", "UTF-8");
        // Set the initial state for processing
        this.currentState = new InitialState();
        // Initialize the stack to keep track of currently open XML elements
        this.elementStack = new Stack<>();
    }*/
    public FileProcessor(OutputStream outputStream) throws XMLStreamException, UnsupportedEncodingException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        // Create an OutputStreamWriter to explicitly handle UTF-8 encoding.
        // This ensures the byte stream is correctly encoded before the XMLStreamWriter processes it.
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8.name());

        // Create an XMLStreamWriter from the Writer. When a Writer is provided,
        // the encoding is handled by the Writer itself.
        this.xmlWriter = factory.createXMLStreamWriter(writer);

        // Write the XML declaration. Since the encoding is handled by the OutputStreamWriter,
        // we can use the method overload that only specifies the XML version.
//        this.xmlWriter.writeStartDocument("1.0"); // Only XML version is needed here
        this.xmlWriter.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");
        // Set the initial state for processing
        this.currentState = new InitialState();
        // Initialize the stack to keep track of currently open XML elements
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
        // Close all remaining open elements in the stack.
        // This will close elements like 'person' and finally the root 'people' element.
        while (!elementStack.isEmpty()) {
            xmlWriter.writeEndElement();
            elementStack.pop();
        }
        // Write the end of the XML document
        xmlWriter.writeEndDocument();
        // Flush any buffered output to the underlying stream
        xmlWriter.flush();
        // Close the XML writer, releasing resources
        xmlWriter.close();
    }
}