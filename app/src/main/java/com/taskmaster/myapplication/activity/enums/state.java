package com.taskmaster.myapplication.activity.enums;

import androidx.annotation.NonNull;

public enum state {
    NEW("New"),
    ASSIGNED("Assigned"),
    IN_PROGRESS("In Progress"),
    COMPLETE("Complete");

    private final String taskStatus;

    state(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public static state fromString(String possibleStatus){
        for (state task : state.values())
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

