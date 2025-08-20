package com.example.converter.states;

import com.example.converter.records.Record;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Stack;

/**
 * Interface for defining the behavior of different processing states.
 */
public interface ProcessingState {
    /**
     * Processes a record based on the current state.
     * Determines if the record type is valid for the current state and writes
     * the corresponding XML elements. It also manages state transitions.
     *
     * @param type The type character of the record (P, T, A, F).
     * @param record The Record object to process.
     * @param writer The XMLStreamWriter to write to the output.
     * @param elementStack A stack to manage open XML elements for correct nesting.
     * @return The next ProcessingState based on the input.
     * @throws XMLStreamException If an error occurs during XML writing.
     * @throws IllegalArgumentException If the record type is not allowed in the current state,
     * enforcing sequence rules.
     */
    ProcessingState process(char type, Record record, XMLStreamWriter writer, Stack<String> elementStack)
            throws XMLStreamException, IllegalArgumentException;

    /**
     * Helper method to close XML elements until a specific target element is on top of the stack,
     * or until the stack is empty (if targetElement is null).
     * This is crucial for correctly closing nested XML tags.
     *
     * @param writer The XMLStreamWriter.
     * @param elementStack The stack of open element names.
     * @param targetElement If specified, elements are closed until this element is at the top.
     * If null, all elements in the stack are closed.
     * @throws XMLStreamException If an XML writing error occurs.
     */
    default void closeCurrentElements(XMLStreamWriter writer, Stack<String> elementStack, String targetElement)
            throws XMLStreamException {
        while (!elementStack.isEmpty()) {
            // Stop closing if the target element is reached, meaning it's the current context parent
            if (targetElement != null && elementStack.peek().equals(targetElement)) {
                break;
            }
            writer.writeEndElement(); // Close the last opened element
            elementStack.pop(); // Remove it from the stack
        }
    }

    /**
     * Helper method to write a simple XML element with text content.
     * Handles pushing and popping the element from the stack.
     *
     * @param writer The XMLStreamWriter.
     * @param elementStack The stack of open element names.
     * @param tagName The name of the XML tag to write.
     * @param textContent The text content for the element.
     * @throws XMLStreamException If an XML writing error occurs.
     */
    default void writeElement(XMLStreamWriter writer, Stack<String> elementStack, String tagName, String textContent)
            throws XMLStreamException {
        writer.writeStartElement(tagName);
        elementStack.push(tagName); // Push the element name onto the stack
        writer.writeCharacters(textContent);
        writer.writeEndElement();
        elementStack.pop(); // Pop it after it's closed
    }
}