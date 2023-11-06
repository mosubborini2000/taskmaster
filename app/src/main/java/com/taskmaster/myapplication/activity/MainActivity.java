package com.taskmaster.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.taskmaster.myapplication.R;
import com.taskmaster.myapplication.activity.adapter.TaskListRecyclerVIewAdapter;
import com.taskmaster.myapplication.activity.enums.state;
import com.taskmaster.myapplication.activity.model.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SharedPreferences preferences;
    public static final String TASK_Title_TAG = "taskName";
    public static final String TASK_BODY_TAG = "BODY";
    public static final String TASK_STATE_TAG = "STATE";
    public static final String USER_USERNAME_TAG = "userUsername";
    public static  final String DATABASE_NAME = "tasks_stuff";
    TaskListRecyclerVIewAdapter adapter;

   // List<Task> tasks=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

      //  tasks= appDatabase.taskDao().findAll();
        setUpProductListRecyclerView();
        Button addTaskButton = findViewById(R.id.btnAddTask);
        Button allTasksButton = findViewById(R.id.btnAllTasks);
        Button settingButton = findViewById(R.id.btnSetting);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });


        allTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllTaskActivity.class);
                startActivity(intent);

            }
        });


        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                Intent goToSettingIntent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(goToSettingIntent);
            }

        });}

    @Override
    protected void onResume() {
        super.onResume();

        String username = preferences.getString(SettingActivity.USER_USERNAME_TAG, "No Username");

        ((TextView)findViewById(R.id.txtUsername)).setText(getString(R.string.username_with_input, username));
//        tasks.clear();
//        tasks.addAll(appDatabase.taskDao().findAll());
//        adapter.notifyDataSetChanged();
    }


    private void setUpProductListRecyclerView(){
        List<Task> tasks=new ArrayList<>();

        RecyclerView taskListRecycleReview = (RecyclerView) findViewById(R.id.HomeListRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        taskListRecycleReview.setLayoutManager(layoutManager);
        tasks.add(new Task("challenges", "Do your daily challenges ", state.IN_PROGRESS));
        adapter = new TaskListRecyclerVIewAdapter(tasks, this);
        taskListRecycleReview.setAdapter(adapter);


    }

}