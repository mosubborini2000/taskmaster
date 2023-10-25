package com.taskmaster.myapplication.activity.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.taskmaster.myapplication.activity.dao.TaskDao;
import com.taskmaster.myapplication.activity.model.Task;

@Database(entities = {Task.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
