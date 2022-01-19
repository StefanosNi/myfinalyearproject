package com.org.competition.Model;

import java.io.Serializable;

public class JoinModel implements Serializable {
    private String id;
    private String eventId;
    private String eventTitle;
    private int sport;
    private String userId;
    private String username;
    private String userImage;
    private String aboutUs;

    public JoinModel(){

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setSport(int sport) {
        this.sport = sport;
    }

    public int getSport() {
        return sport;
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

    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }

    public String getAboutUs() {
        return aboutUs;
    }
    @Override
    public boolean equals(Object model){
        return this.id.equals(((JoinModel)model).getId());
    }
}
