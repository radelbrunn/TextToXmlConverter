package com.example.converter.records;

import java.util.Objects;

/**
 * Represents a Family member record with name and year of birth.
 * Corresponds to the 'F' line type in the input file.
 */
public class FamilyRecord implements Record {
    private final String name;
    private final String yearOfBirth; // Stored as String as per example, could be int/LocalDate

    public FamilyRecord(String name, String yearOfBirth) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.yearOfBirth = Objects.requireNonNull(yearOfBirth, "Year of birth cannot be null");
    }

    public String getName() {
        return name;
    }

    public String getYearOfBirth() {
        return yearOfBirth;
    }

    @Override
    public String toString() {
        return "FamilyRecord{" +
                "name='" + name + '\'' +
                ", yearOfBirth='" + yearOfBirth + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FamilyRecord that = (FamilyRecord) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(yearOfBirth, that.yearOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, yearOfBirth);
    }
}