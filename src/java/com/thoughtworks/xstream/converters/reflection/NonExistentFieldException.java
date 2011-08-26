package com.thoughtworks.xstream.converters.reflection;

/**
 * Indicates that field/property doesn't exist in classes (as opposed to what's implied in XML.)
 * <p/>
 * Date: 8/25/11
 *
 * @author Nikita Levyankov
 */
public class NonExistentFieldException extends ObjectAccessException {

    private final String fieldName;

    public NonExistentFieldException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
