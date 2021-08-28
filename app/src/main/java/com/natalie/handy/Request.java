package com.natalie.handy;

public class Request {
    String clientId, handymanId, status, requestDate;

    public Request(String clientId, String handymanId, String status, String requestDate) {
        this.clientId = clientId;
        this.handymanId = handymanId;
        this.status = status;
        this.requestDate = requestDate;
    }

    public String getClientId() {
        return clientId;
    }

    public String getHandymanId() {
        return handymanId;
    }

    public String getStatus() {
        return status;
    }

    public String getRequestDate() {
        return requestDate;
    }
}
