package com.example.ruslan.curs2project.model.pojo;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("to")
    private String to;

    @SerializedName("notification")
    private Notification notification;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
