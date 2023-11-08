package com.taskmaster.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.State;
import com.amplifyframework.datastore.generated.model.Task;
import com.taskmaster.myapplication.R;

public class AddTaskActivity extends AppCompatActivity {
    int count = 0 ;
//    public static  final String DATABASE_NAME = "tasks_stuff";
public static final String TAG = "AddTaskActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Spinner taskCategorySpinner = (Spinner) findViewById(R.id.spinner);
        taskCategorySpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                State.values()));



        TextView totalView =(TextView) findViewById(R.id.totalView);
        Button submitButton = findViewById(R.id.submitButton);

        Toast toast = Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT);


        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                totalView.setText(String.valueOf(count++));
                toast.show();

//                Task newTask=new Task(
//                        ((EditText) findViewById(R.id.titleText)).getText().toString(),
//                        ((EditText) findViewById(R.id.bodyText)).getText().toString(),
//                        State.fromString(taskCategorySpinner.getSelectedItem().toString())
//                );
                String title = ((EditText)findViewById(R.id.titleText)).getText().toString();
                String body = ((EditText)findViewById(R.id.bodyText)).getText().toString();
                Task newTask = Task.builder()
                        .title(title)
                        .body(body)
                        .state((State) taskCategorySpinner.getSelectedItem()).build();

                Amplify.API.mutate(
                        ModelMutation.create(newTask),
                        successResponse -> Log.i(TAG, "AddTaskActivity.onCreate(): made a Task successfully"),//success response
                        failureResponse -> Log.e(TAG, "AddTaskActivity.onCreate(): failed with this response" + failureResponse)// in case we have a failed response
                );
                //to do
             //   appDatabase.taskDao().insertTask(newTask);
            }
        });


    }
}