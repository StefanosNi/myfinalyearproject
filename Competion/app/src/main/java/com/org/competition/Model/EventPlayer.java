package com.org.competition.Model;

import java.io.Serializable;

public class EventPlayer implements Serializable {
    private String eventId;
    private String userId;
    private String username;
    private String userImage;

    public EventPlayer(){

    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserImage() {
        return userImage;
    }
}
