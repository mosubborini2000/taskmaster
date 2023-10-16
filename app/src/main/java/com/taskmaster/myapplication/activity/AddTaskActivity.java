package com.taskmaster.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.taskmaster.myapplication.R;

public class AddTaskActivity extends AppCompatActivity {
    int count = 0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

//        final TextView titleText = findViewById(R.id.titleText);
//        final TextView bodyText = findViewById(R.id.bodyText);
        TextView totalView =(TextView) findViewById(R.id.totalView);
        Button submitButton = findViewById(R.id.submitButton);

        Toast toast = Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT);


        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                totalView.setText(String.valueOf(count++));
                toast.show();
            }
        });

    }
}