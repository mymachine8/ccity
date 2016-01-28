package com.cargoexchange.cargocity.cargocity.models;

import java.io.Serializable;

/**
 * Created by thespidy on 27/01/16.
 */
public class Phone implements Serializable{
private String countryCode;
    private String number;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}

