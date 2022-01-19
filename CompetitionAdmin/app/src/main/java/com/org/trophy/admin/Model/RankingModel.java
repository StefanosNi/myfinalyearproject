package com.org.trophy.admin.Model;

public class RankingModel {
    private String name;
    private int icon;

    public RankingModel(String name, int icon){
        this.name = name;
        this.icon = icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }
}
