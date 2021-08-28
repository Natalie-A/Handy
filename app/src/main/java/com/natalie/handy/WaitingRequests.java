package com.natalie.handy;

public class WaitingRequests {
    private String clientName, requestDate;

    public WaitingRequests(String clientName, String requestDate) {
        this.clientName = clientName;
        this.requestDate = requestDate;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }
}
