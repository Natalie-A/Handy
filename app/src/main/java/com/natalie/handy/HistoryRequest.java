package com.natalie.handy;

public class HistoryRequest {
    private String name, requestDate, status;

    public HistoryRequest(String name, String requestDate, String status) {
        this.name = name;
        this.requestDate = requestDate;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
