package com.cargoexchange.cargocity.cargocity.models;

/**
 * Created by thespidy on 14/01/16.
 */
public class Address {
    private String houseNumber=new String();
    private String addressLine1=new String();
    private String addressLine2=new String();
    private String city=new String();
    private String pincode=new String();
    private String state=new String();

    public Address(String houseNumber,String addressLine1, String addressLine2, String city, String pincode, String state) {
        this.houseNumber = houseNumber;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.pincode = pincode;
        this.state = state;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
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
