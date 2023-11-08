package com.taskmaster.myapplication.activity.model;


import com.taskmaster.myapplication.activity.enums.State;

public class Task {

    private long id;
    String title;
    String body;
    State state;

    public Task(String title, String body, State state) {
        this.title = title;
        this.body = body;
        this.state = state;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public State getState() {
        return state;
    }

    public void setState(  State state) {
        this.state = state;
    }
}