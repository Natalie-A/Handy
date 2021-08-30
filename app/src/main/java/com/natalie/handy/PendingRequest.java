package com.natalie.handy;

public class PendingRequest {
    private String handypersonName, requestDate, handypersonPhone;

    public PendingRequest(String handypersonName, String requestDate, String handypersonPhone) {
        this.handypersonName = handypersonName;
        this.requestDate = requestDate;
        this.handypersonPhone = handypersonPhone;
    }

    public String getHandypersonName() {
        return handypersonName;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public String getHandypersonPhone() {
        return handypersonPhone;
    }
}
