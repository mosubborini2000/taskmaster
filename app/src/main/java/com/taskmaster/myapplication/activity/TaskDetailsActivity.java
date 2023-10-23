package com.taskmaster.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.taskmaster.myapplication.R;

public class TaskDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        Intent callingIntent = getIntent();
        String taskNameString;
        String taskTitleString = null;
        String taskBodyString = null;
        String taskStateEnum = null;

        if(callingIntent != null){
            taskTitleString = callingIntent.getStringExtra(MainActivity.TASK_Title_TAG);
            taskBodyString = callingIntent.getStringExtra(MainActivity.TASK_BODY_TAG);
            taskStateEnum = callingIntent.getStringExtra(MainActivity.TASK_STATE_TAG);
        }
        TextView taskViewTitleView = (TextView) findViewById(R.id.textTaskViewTitle);
        TextView taskViewBodyView = (TextView) findViewById(R.id.textTaskViewBody);
        TextView taskViewStateView = (TextView) findViewById(R.id.textTaskViewStatusName);

        if (taskTitleString != null){
            taskViewTitleView.setText(taskTitleString);
        } else {
            taskViewTitleView.setText("No Name");
        }

        if (taskBodyString != null){
            taskViewBodyView.setText(taskBodyString);
        } else {
            taskViewBodyView.setText("No Body");
        }

        if (taskStateEnum != null){
            taskViewStateView.setText(taskStateEnum);
        } else {
            taskViewStateView.setText("No State");
        }

    }



}