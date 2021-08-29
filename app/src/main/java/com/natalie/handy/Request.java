package com.natalie.handy;

public class Request {
    String clientId, handymanId, status, requestDate,client_handyman_status;

    public Request(String clientId, String handymanId, String status, String requestDate, String client_handyman_status) {
        this.clientId = clientId;
        this.handymanId = handymanId;
        this.status = status;
        this.requestDate = requestDate;
        this.client_handyman_status = client_handyman_status;
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

    public String getClient_handyman_status() {
        return client_handyman_status;
    }
}
