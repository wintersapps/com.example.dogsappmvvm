package com.examples.dogsappmvvm.model;

public class SmsInfo {
    private String sendTo;
    private String text;
    private String imageUrl;

    public SmsInfo() {
        this.sendTo = "";
        this.text = "";
        this.imageUrl = "";
    }

    public SmsInfo(String sendTo, String text, String imageUrl) {
        this.sendTo = sendTo;
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
