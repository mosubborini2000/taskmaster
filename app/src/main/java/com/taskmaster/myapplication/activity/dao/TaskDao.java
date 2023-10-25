package com.taskmaster.myapplication.activity.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.taskmaster.myapplication.activity.model.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    public void insertTask(Task task);
    @Query("select * from Task")
    public List<Task> findAll();

    @Query("select * from Task ORDER BY title ASC")
    public List<Task> findAllSortedByName();

    @Query("select * from Task where id = :id")
    Task findByAnId(long id);


}
