package com.taskmaster.myapplication.activity.enums;

import androidx.annotation.NonNull;

public enum State {
    NEW("New"),
    ASSIGNED("Assigned"),
    IN_PROGRESS("In Progress"),
    COMPLETE("Complete");

    private final String taskStatus;

    State(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public static State fromString(String possibleStatus){
        for (State task : State.values())
            if(task.taskStatus.equals(possibleStatus)){
                return task;
            }
        return null;
    }


    @NonNull
    @Override
    public String toString(){
        if (taskStatus == null){
            return "";
        }
        return taskStatus;
    }
}

