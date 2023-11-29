package com.taskmaster.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.State;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.material.snackbar.Snackbar;
import com.taskmaster.myapplication.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EditTasksActivity extends AppCompatActivity {
    public static final String TAG= "editTaskActivity";
    private CompletableFuture<Task> taskCompletableFuture=null;
    private CompletableFuture<List<Team>> teamFuture = null;
    private Task taskToEdit= null;
    private EditText titleEditText;
    private EditText descriptionEditText;

    private Spinner taskSpinner = null;

    private Spinner teamSpinner = null;

    private TextView textViewLong = null;
    private TextView textViewLat = null;

    private String s3ImageKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        taskCompletableFuture = new CompletableFuture<>();
        teamFuture = new CompletableFuture<>();

        setUpEditableUIElement();
        setUpSaveButton();
        setUpDeleteButton();
    }
    private void setUpEditableUIElement() {
        Intent callingIntent = getIntent();
        String taskId = null;

        if(callingIntent != null){
            taskId = callingIntent.getStringExtra(MainActivity.MAIN_ID_TAG);
        }

        String taskId2 = taskId; //ugly hack just to fix lambda processing

        Amplify.API.query(
                ModelQuery.list(Task.class),
                success ->
                {
                    Log.i(TAG,"Read tasks Successfully");

                    for (Task databasetasks: success.getData()){
                        if(databasetasks.getId().equals(taskId2)){
                            taskCompletableFuture.complete(databasetasks);
                        }
                    }

                    runOnUiThread(() ->
                    {
                        //Update UI element
                    });
                },
                failure -> Log.i(TAG, "Did not read task successfully")
        );

        try {
            taskToEdit = taskCompletableFuture.get();
        }catch (InterruptedException ie){
            Log.e(TAG, "InterruptedException while getting product");
            Thread.currentThread().interrupt();
        }catch (ExecutionException ee){
            Log.e(TAG, "ExecutionException while getting product");
        }

        titleEditText = ((EditText) findViewById(R.id.titleeditTextText));
        titleEditText.setText(taskToEdit.getTitle());
        descriptionEditText = ((EditText) findViewById(R.id.desceditTextText2));
        descriptionEditText.setText(taskToEdit.getBody());

        textViewLong = findViewById(R.id.textViewLong);
        textViewLong.setText(taskToEdit.getTaskLongitude());

        textViewLat = findViewById(R.id.textViewLat);
        textViewLat.setText(taskToEdit.getTaskLatitude());

        s3ImageKey = taskToEdit.getTaskImageS3Key();
        if (s3ImageKey != null && !s3ImageKey.isEmpty())
        {
            Amplify.Storage.downloadFile(
                    s3ImageKey,
                    new File(getApplication().getFilesDir(), s3ImageKey),
                    success ->
                    {
                        ImageView taskImageView = findViewById(R.id.imageView);
                        taskImageView.setImageBitmap(BitmapFactory.decodeFile(success.getFile().getPath()));
                    },
                    failure ->
                    {
                        Log.e(TAG, "Unable to get image from S3 for the Task for S3 key: " + s3ImageKey + " for reason: " + failure.getMessage());
                    }
            );
        }

        setUpSpinners();
    }

    private void setUpSpinners()
    {
        teamSpinner = (Spinner) findViewById(R.id.teamspinner);
        Amplify.API.query(
                ModelQuery.list(Team.class),
                success ->
                {
                    Log.i(TAG, "Read teamm successfully!");
                    ArrayList<String> teamNames = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();
                    for (Team team : success.getData())
                    {
                        teams.add(team);
                        teamNames.add(team.getName());
                    }
                    teamFuture.complete(teams);

                    runOnUiThread(() ->
                    {
                        teamSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                teamNames));
                        teamSpinner.setSelection(getSpinnerIndex(teamSpinner, taskToEdit.getTeamName().getName()));
                    });
                },
                failure -> {
                    teamFuture.complete(null);
                    Log.i(TAG, "Did not read teams successfully!");
                }
        );

        taskSpinner = (Spinner) findViewById(R.id.teampinner);
        taskSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                State.values()));
        taskSpinner.setSelection(getSpinnerIndex(taskSpinner, taskToEdit.getState().toString()));
    }

    private int getSpinnerIndex(Spinner spinner, String stringValueToCheck){
        for (int i = 0;i < spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(stringValueToCheck)){
                return i;
            }
        }

        return 0;
    }

    private void setUpSaveButton()
    {
        Button saveButton = (Button)findViewById(R.id.editTasktButton);
        saveButton.setOnClickListener(v ->
        {
            List<Team> teams = null;
            String teamToSaveString = teamSpinner.getSelectedItem().toString();
            try
            {
                teams = teamFuture.get();
            }
            catch (InterruptedException ie)
            {
                Log.e(TAG, "InterruptedException while getting task");
                Thread.currentThread().interrupt();
            }
            catch (ExecutionException ee)
            {
                Log.e(TAG, "ExecutionException while getting tasks");
            }
            Team teamToSave = teams.stream().filter(c -> c.getName().equals(teamToSaveString)).findAny().orElseThrow(RuntimeException::new);
            Task taskToSave = Task.builder()
                    .title(titleEditText.getText().toString())
                    .id(taskToEdit.getId())
                    .body(descriptionEditText.getText().toString())
                    .teamName(teamToSave)
                    .state(taskCategoryFromString(taskSpinner.getSelectedItem().toString()))
                    .build();

            Amplify.API.mutate(
                    ModelMutation.update(taskToSave),  // making a GraphQL request to the cloud
                    successResponse ->
                    {
                        Log.i(TAG, "EditTaskActivity.onCreate(): edited a task successfully");
                        // TODO: Display a Snackbar
                        Snackbar.make(findViewById(R.id.editTaskAcivity), "Task saved!", Snackbar.LENGTH_SHORT).show();
                    },  // success callback
                    failureResponse -> Log.i(TAG, "EditTaskActivity.onCreate(): failed with this response: " + failureResponse)  // failure callback
            );
        });
    }

    public static State taskCategoryFromString(String inputTaskCategoryText){
        for (State taskCategory : State.values()){
            if(taskCategory.toString().equals(inputTaskCategoryText)){
                return taskCategory;
            }
        }
        return null;
    }


    private void setUpDeleteButton(){
        Button deleteButton = (Button) findViewById(R.id.saveeditbutton);
        deleteButton.setOnClickListener(v ->{
            Amplify.API.mutate(
                    ModelMutation.delete(taskToEdit),
                    successResponse ->
                    {
                        Log.i(TAG, "EditTaskActivity.onCreate(): deleted a task successfully");
                        Intent goToMainListActivity = new Intent(EditTasksActivity.this, MainActivity.class);
                        startActivity(goToMainListActivity);
                    },
                    failureResponse -> Log.i(TAG,"EditTaskActivity.onCreate(): failed with this response: "+ failureResponse)
            );
        });
    }

}