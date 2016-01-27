package com.cargoexchange.cargocity.cargocity.models;

/**
 * Created by thespidy on 14/01/16.
 */
public class Address {
    private String line1;
    private String line2;
    private String landmark;
    private String city;
    private String pincode;
    private String state;

    public Address(String addressLine1, String addressLine2, String city, String pincode, String state) {
        this.line1 = addressLine1;
        this.line2 = addressLine2;
        this.city = city;
        this.pincode = pincode;
        this.state = state;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
