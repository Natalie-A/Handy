package com.natalie.handy;

public class OngoingRequest {

    private String name, date, clientLocation, clientPhoneNumber;

    public OngoingRequest(String name, String date, String clientLocation, String clientPhoneNumber) {
        this.name = name;
        this.date = date;
        this.clientLocation = clientLocation;
        this.clientPhoneNumber = clientPhoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getClientLocation() {
        return clientLocation;
    }

    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }
}
