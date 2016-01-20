package com.cargoexchange.cargocity.cargocity.models;

import java.util.List;

public class Feedback {
    private boolean isOrderDelivered;
    private boolean inGoodCondition;
    private String additionalComments;
    private List<String> documentImageList;
    private int Rating;
    private String feedback;

    public boolean isOrderDelivered() {
        return isOrderDelivered;
    }

    public void setIsOrderDelivered(boolean isOrderDelivered) {
        this.isOrderDelivered = isOrderDelivered;
    }

    public boolean isInGoodCondition() {
        return inGoodCondition;
    }

    public void setInGoodCondition(boolean inGoodCondition) {
        this.inGoodCondition = inGoodCondition;
    }

    public String getAdditionalComments() {
        return additionalComments;
    }

    public void setAdditionalComments(String additionalComments) {
        this.additionalComments = additionalComments;
    }

    public List<String> getDocumentImageList() {
        return documentImageList;
    }

    public void setDocumentImageList(List<String> documentImageList) {
        this.documentImageList = documentImageList;
    }

    public int getRating() {
        return Rating;
    }

    public void setRating(int rating) {
        Rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
