package com.org.trophy.admin.Model;

import java.io.Serializable;

public class SportModel implements Serializable {
    private int key;
    private String name;

    public SportModel(){

    }
    public SportModel(int key, String name){
        this.key = key;
        this.name = name;
    }
    public void setKey(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
