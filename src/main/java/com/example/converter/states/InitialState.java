// src/main/java/com/example/converter/states/InitialState.java
package com.example.converter.states;

import com.example.converter.records.PersonRecord;
import com.example.converter.records.Record;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Stack;

/**
 * Represents the initial state of the file processing.
 * In this state, only a 'P' (Person) record is expected to start the XML structure.
 */
public class InitialState implements ProcessingState {
    @Override
    public ProcessingState process(char type, Record record, XMLStreamWriter writer, Stack<String> elementStack)
            throws XMLStreamException, IllegalArgumentException {
        if (type == 'P') {
            // Start the root 'people' element
            writer.writeStartElement("people");
            elementStack.push("people");

            // Start the first 'person' element
            writer.writeStartElement("person");
            elementStack.push("person");

            // Write the person's first name and last name
            PersonRecord personRecord = (PersonRecord) record;
            writeElement(writer, elementStack, "firstname", personRecord.getFirstName());
            writeElement(writer, elementStack, "lastname", personRecord.getLastName());

            // Transition to the PersonContextState, as we are now inside a person element
            return new PersonContextState();
        } else {
            // Any other record type is invalid at the beginning of the file
            throw new IllegalArgumentException("Unexpected record type '" + type + "'. Expected 'P' to start a new person record.");
        }
    }
}
