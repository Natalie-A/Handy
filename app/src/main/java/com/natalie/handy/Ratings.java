package com.natalie.handy;

public class Ratings {
    private String clientId, handymanId, ratingScore, requestId;

    public Ratings(String clientId, String handymanId, String ratingScore, String requestId) {
        this.clientId = clientId;
        this.handymanId = handymanId;
        this.ratingScore = ratingScore;
        this.requestId = requestId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getHandymanId() {
        return handymanId;
    }

    public void setHandymanId(String handymanId) {
        this.handymanId = handymanId;
    }

    public String getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(String ratingScore) {
        this.ratingScore = ratingScore;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
