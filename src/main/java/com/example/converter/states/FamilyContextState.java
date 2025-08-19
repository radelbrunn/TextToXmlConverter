package com.example.converter.states;

import com.example.converter.records.*;
import com.example.converter.records.Record;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Stack;

/**
 * Represents the state where the current XML context is a 'family' element.
 * From this state, 'T' (Phone) and 'A' (Address) records are expected as children
 * of the current 'family' element.
 * A 'P' record indicates the end of the current family and person, and the start of a new person.
 */
public class FamilyContextState implements ProcessingState {
    @Override
    public ProcessingState process(char type, Record record, XMLStreamWriter writer, Stack<String> elementStack)
            throws XMLStreamException, IllegalArgumentException {
        switch (type) {
            case 'T':
                // Close any open 'address' elements if they exist under the current 'family'
                closeCurrentElements(writer, elementStack, "family");
                PhoneRecord phoneRecord = (PhoneRecord) record;
                writer.writeStartElement("phone");
                elementStack.push("phone"); // Add 'phone' to stack
                writeElement(writer, elementStack, "mobile", phoneRecord.getMobileNumber());
                writeElement(writer, elementStack, "landline", phoneRecord.getLandlineNumber());
                writer.writeEndElement(); // Close 'phone'
                elementStack.pop(); // Remove 'phone' from stack
                return this; // Stay in FamilyContextState
            case 'A':
                // Close any open 'phone' elements if they exist under the current 'family'
                closeCurrentElements(writer, elementStack, "family");
                AddressRecord addressRecord = (AddressRecord) record;
                writer.writeStartElement("address");
                elementStack.push("address"); // Add 'address' to stack
                writeElement(writer, elementStack, "street", addressRecord.getStreet());
                writeElement(writer, elementStack, "city", addressRecord.getCity());
                writeElement(writer, elementStack, "postcode", addressRecord.getPostcode());
                writer.writeEndElement(); // Close 'address'
                elementStack.pop(); // Remove 'address' from stack
                return this; // Stay in FamilyContextState
            case 'F': // Handle new family member as a sibling
                // Close the current 'family' element and return to the 'person' context.
                closeCurrentElements(writer, elementStack, "person"); // This closes the current 'family' element

                // Now, start a new 'family' element for the next family member.
                FamilyRecord nextFamilyRecord = (FamilyRecord) record;
                writer.writeStartElement("family");
                elementStack.push("family"); // Add new 'family' to stack
                writeElement(writer, elementStack, "name", nextFamilyRecord.getName());
                writeElement(writer, elementStack, "born", nextFamilyRecord.getYearOfBirth());
                return this; // Stay in FamilyContextState for the new family member.
            case 'P':
                // A new 'P' record indicates a new person, closing the current family and parent person.
                // Close all elements up to the 'people' root element.
                closeCurrentElements(writer, elementStack, "people"); // Closes current 'family' and parent 'person' tags

                // Start the new 'person' element
                writer.writeStartElement("person");
                elementStack.push("person"); // Add new 'person' to stack

                PersonRecord newPersonRecord = (PersonRecord) record;
                writeElement(writer, elementStack, "firstname", newPersonRecord.getFirstName());
                writeElement(writer, elementStack, "lastname", newPersonRecord.getLastName());
                // Transition back to PersonContextState for the new person
                return new PersonContextState();
            default:
                throw new IllegalArgumentException("Invalid record type '" + type + "' in Family context. Expected T, A, F, or P.");
        }
    }
}