package com.unwire.todaysmenu;

import io.realm.RealmObject;

public class Opinion extends RealmObject {

    private int state; // 0 = Not voted yet, 1 = Liked, 2 = Disliked
    private String id;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}