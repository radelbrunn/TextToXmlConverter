package com.example.converter.records;

import java.util.Objects;

/**
 * Represents an Address record with street, city, and postcode.
 * Corresponds to the 'A' line type in the input file.
 */
public class AddressRecord implements Record {
    private final String street;
    private final String city;
    private final String postcode;

    public AddressRecord(String street, String city, String postcode) {
        this.street = Objects.requireNonNull(street, "Street cannot be null");
        this.city = Objects.requireNonNull(city, "City cannot be null");
        this.postcode = Objects.requireNonNull(postcode, "Postcode cannot be null");
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getPostcode() {
        return postcode;
    }

    @Override
    public String toString() {
        return "AddressRecord{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", postcode='" + postcode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressRecord that = (AddressRecord) o;
        return Objects.equals(street, that.street) &&
                Objects.equals(city, that.city) &&
                Objects.equals(postcode, that.postcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, postcode);
    }
}