package com.example.converter.records;
import java.util.Objects;

/**
 * Represents a Phone record with mobile and landline numbers.
 * Corresponds to the 'T' line type in the input file.
 */
public class PhoneRecord implements Record {
    private final String mobileNumber;
    private final String landlineNumber;

    public PhoneRecord(String mobileNumber, String landlineNumber) {
        this.mobileNumber = Objects.requireNonNull(mobileNumber, "Mobile number cannot be null");
        this.landlineNumber = Objects.requireNonNull(landlineNumber, "Landline number cannot be null");
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getLandlineNumber() {
        return landlineNumber;
    }

    @Override
    public String toString() {
        return "PhoneRecord{" +
                "mobileNumber='" + mobileNumber + '\'' +
                ", landlineNumber='" + landlineNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneRecord that = (PhoneRecord) o;
        return Objects.equals(mobileNumber, that.mobileNumber) &&
                Objects.equals(landlineNumber, that.landlineNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mobileNumber, landlineNumber);
    }
}
