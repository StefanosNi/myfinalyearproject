package com.org.competition.Model;

import java.io.Serializable;

public class Follow implements Serializable {
    private String mainId;
    private String userId;

    public Follow(){

    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setMainId(String mainId) {
        this.mainId = mainId;
    }

    public String getMainId() {
        return mainId;
    }
}
