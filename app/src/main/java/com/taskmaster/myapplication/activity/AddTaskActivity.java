package com.taskmaster.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.taskmaster.myapplication.R;
import com.taskmaster.myapplication.activity.adapter.TaskListRecyclerVIewAdapter;
import com.taskmaster.myapplication.activity.enums.state;
import com.taskmaster.myapplication.activity.model.Task;

import java.util.List;

public class AddTaskActivity extends AppCompatActivity {
    int count = 0 ;
//    public static  final String DATABASE_NAME = "tasks_stuff";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Spinner taskCategorySpinner = (Spinner) findViewById(R.id.spinner);
        taskCategorySpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                state.values()));



        TextView totalView =(TextView) findViewById(R.id.totalView);
        Button submitButton = findViewById(R.id.submitButton);

        Toast toast = Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT);


        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                totalView.setText(String.valueOf(count++));
                toast.show();

                Task newTask=new Task(
                        ((EditText) findViewById(R.id.titleText)).getText().toString(),
                        ((EditText) findViewById(R.id.bodyText)).getText().toString(),
                        state.fromString(taskCategorySpinner.getSelectedItem().toString())
                );
                //to do
             //   appDatabase.taskDao().insertTask(newTask);
            }
        });


    }
}