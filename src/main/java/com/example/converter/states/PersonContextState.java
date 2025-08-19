// src/main/java/com/example/converter/states/PersonContextState.java
package com.example.converter.states;

import com.example.converter.records.*;
import com.example.converter.records.Record;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Stack;

/**
 * Represents the state where the current XML context is a 'person' element.
 * From this state, 'T' (Phone), 'A' (Address), 'F' (Family) records are expected
 * as children of the current 'person' element.
 * A new 'P' record indicates the end of the current person and the start of a new one.
 */
public class PersonContextState implements ProcessingState {
    @Override
    public ProcessingState process(char type, Record record, XMLStreamWriter writer, Stack<String> elementStack)
            throws XMLStreamException, IllegalArgumentException {
        switch (type) {
            case 'T':
                // Close any open elements that are siblings to 'phone' (e.g., 'address' or 'family')
                // This ensures correct placement of 'phone' directly under 'person'.
                closeCurrentElements(writer, elementStack, "person");
                PhoneRecord phoneRecord = (PhoneRecord) record;
                writer.writeStartElement("phone");
                elementStack.push("phone"); // Add 'phone' to stack
                writeElement(writer, elementStack, "mobile", phoneRecord.getMobileNumber());
                writeElement(writer, elementStack, "landline", phoneRecord.getLandlineNumber());
                writer.writeEndElement(); // Close 'phone'
                elementStack.pop(); // Remove 'phone' from stack
                return this; // Stay in PersonContextState
            case 'A':
                // Close any open elements that are siblings to 'address'
                closeCurrentElements(writer, elementStack, "person");
                AddressRecord addressRecord = (AddressRecord) record;
                writer.writeStartElement("address");
                elementStack.push("address"); // Add 'address' to stack
                writeElement(writer, elementStack, "street", addressRecord.getStreet());
                writeElement(writer, elementStack, "city", addressRecord.getCity());
                writeElement(writer, elementStack, "postcode", addressRecord.getPostcode());
                writer.writeEndElement(); // Close 'address'
                elementStack.pop(); // Remove 'address' from stack
                return this; // Stay in PersonContextState
            case 'F':
                // Close any open elements that are siblings to 'family'
                closeCurrentElements(writer, elementStack, "person");
                FamilyRecord familyRecord = (FamilyRecord) record;
                writer.writeStartElement("family");
                elementStack.push("family"); // Add 'family' to stack
                writeElement(writer, elementStack, "name", familyRecord.getName());
                writeElement(writer, elementStack, "born", familyRecord.getYearOfBirth());
                // Transition to FamilyContextState as we are now inside a family element
                return new FamilyContextState();
            case 'P':
                // A new 'P' record indicates a new person.
                // First, close all elements related to the *current* person, up to the 'people' root.
                closeCurrentElements(writer, elementStack, "people"); // Closes current 'person' tag

                // Start the new 'person' element
                writer.writeStartElement("person");
                elementStack.push("person"); // Add new 'person' to stack

                PersonRecord newPersonRecord = (PersonRecord) record;
                writeElement(writer, elementStack, "firstname", newPersonRecord.getFirstName());
                writeElement(writer, elementStack, "lastname", newPersonRecord.getLastName());
                return this; // Stay in PersonContextState for the new person
            default:
                throw new IllegalArgumentException("Invalid record type '" + type + "' in Person context. Expected T, A, F, or P.");
        }
    }
}