package com.natalie.handy;

public class WaitingRequests {
    private String clientName, requestDate, clientLocation;

    public WaitingRequests(String clientName, String requestDate, String clientLocation) {
        this.clientName = clientName;
        this.requestDate = requestDate;
        this.clientLocation = clientLocation;
    }

    public String getClientName() {
        return clientName;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public String getClientLocation() {
        return clientLocation;
    }
}
