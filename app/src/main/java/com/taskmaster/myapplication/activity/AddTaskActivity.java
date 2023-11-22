package com.taskmaster.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.State;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.taskmaster.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AddTaskActivity extends AppCompatActivity {

    public static final String TAG = "AddTaskActivity";
    Spinner taskCategorySpinner = null;
    Spinner teamSpinner = null;
    CompletableFuture<List<Team>> teamsFuture = new CompletableFuture<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        teamsFuture = new CompletableFuture<>();

        setUpSpinners();
        setUpSaveButton();
        Spinner taskCategorySpinner = (Spinner) findViewById(R.id.spinner);
        taskCategorySpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                State.values()));


    }

    private void setUpSpinners() {
        teamSpinner= (Spinner) findViewById(R.id.spinnerteam);
        Amplify.API.query(
                ModelQuery.list(Team.class),
                success ->
                {
                    Log.i(TAG, "add teams Successfully");
                    ArrayList<String> teamNames = new ArrayList<>();
                    ArrayList<Team> teams  = new ArrayList<>();
                    for(Team team: success.getData()){
                        teams .add(team);
                        teamNames.add(team.getName());
                    }
                    teamsFuture.complete(teams );

                    runOnUiThread(() ->
                    {
                        teamSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                (android.R.layout.simple_spinner_item),
                                teamNames
                        ));
                    });
                },
                failure-> {
                    teamsFuture.complete(null);
                    Log.i(TAG, "Failed to add teams successfully");
                }
        );
        taskCategorySpinner = (Spinner) findViewById(R.id.spinner);
        taskCategorySpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                State.values()
        ));
    }

    private void setUpSaveButton(){

        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedTaskString = teamSpinner.getSelectedItem().toString();

                String title = ((EditText)findViewById(R.id.titleText)).getText().toString();
                String body = ((EditText)findViewById(R.id.bodyText)).getText().toString();
                List<Team> teams=null;
                try {
                    teams=teamsFuture.get();
                }catch (InterruptedException ie){
                    Log.e(TAG, " InterruptedException while getting teams");
                }catch (ExecutionException ee){
                    Log.e(TAG," ExecutionException while getting teams");
                }

                Team selectedTeam = teams.stream().filter(c -> c.getName().equals(selectedTaskString)).findAny().orElseThrow(RuntimeException::new);



                Task newTask = Task.builder()
                        .title(title)
                        .body(body)
                        .state((State) taskCategorySpinner.getSelectedItem())
                        .teamName(selectedTeam)
                        .build();

                Amplify.API.mutate(
                        ModelMutation.create(newTask),
                        successResponse -> Log.i(TAG, "AddTaskActivity.onCreate(): made a Task successfully"),//success response
                        failureResponse -> Log.e(TAG, "AddTaskActivity.onCreate(): failed with this response" + failureResponse)// in case we have a failed response
                );
            }
        });
    }


}