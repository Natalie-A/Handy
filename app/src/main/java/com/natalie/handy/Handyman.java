package com.natalie.handy;

public class Handyman {
    private String handymanName, handymanLocation, ratingScore;

    public Handyman(String handymanName, String handymanLocation, String ratingScore) {
        this.handymanName = handymanName;
        this.handymanLocation = handymanLocation;
        this.ratingScore = ratingScore;
    }

    public String getHandymanName() {
        return handymanName;
    }

    public void setHandymanName(String handymanName) {
        this.handymanName = handymanName;
    }

    public String getHandymanLocation() {
        return handymanLocation;
    }

    public void setHandymanLocation(String handymanLocation) {
        this.handymanLocation = handymanLocation;
    }

    public String getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(String ratingScore) {
        this.ratingScore = ratingScore;
    }
}
