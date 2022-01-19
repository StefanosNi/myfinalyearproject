package com.org.competition.Model;

import java.io.Serializable;

public class EventModel implements Serializable {
    private String id;
    private String title;
    private String address;
    private String sportKey;
    private SportModel sport;
    private String price;
    private String time;
    private String duration;
    private String weekName;
    private String onceWeek;
    private String startAt;
    private String deadline;
    private double lat;
    private double lon;

    public EventModel(){

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setSportKey(String sportKey) {
        this.sportKey = sportKey;
    }

    public String getSportKey() {
        return sportKey;
    }

    public void setSport(SportModel sport) {
        this.sport = sport;
    }
    public SportModel getSport(){
        return this.sport;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDuration(){
        return this.duration;
    }

    public void setWeekName(String weekName) {
        this.weekName = weekName;
    }

    public String getWeekName() {
        return weekName;
    }

    public void setOnceWeek(String onceWeek) {
        this.onceWeek = onceWeek;
    }

    public String getOnceWeek() {
        return onceWeek;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLat() {
        return lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLon() {
        return lon;
    }
    @Override
    public boolean equals(Object model){
        return this.id.equals(((EventModel)model).getId());
    }
}
