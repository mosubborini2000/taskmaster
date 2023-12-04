package com.taskmaster.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.State;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.taskmaster.myapplication.R;
import com.taskmaster.myapplication.activity.adapter.TaskListRecyclerVIewAdapter;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SharedPreferences preferences;
    public static final String TASK_Title_TAG = "taskName";
    public static final String TASK_BODY_TAG = "BODY";
    public static final String TASK_STATE_TAG = "STATE";
    public static final String MAIN_ID_TAG = "Main ID Tag";

    //    public static final String USER_USERNAME_TAG = "userUsername";
//    public static  final String DATABASE_NAME = "tasks_stuff";
    public final String TAG = "taskActivity";


    List<Task> tasks=null;
    TaskListRecyclerVIewAdapter adapter;

    private void init(){
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        tasks = new ArrayList<>();
        AnalyticsEvent event = AnalyticsEvent.builder()
                .name("openedApp")
                .addProperty("time", Long.toString(new Date().getTime()))
                .addProperty("trackingEvent", " main activity opened")
                .build();

        Amplify.Analytics.recordEvent(event);
    }

    // List<Task> tasks=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//                Team team1=
//                Team.builder()
//                        .name("Mosuab")
//                       .build();
//
//        Team team2=
//                Team.builder()
//                        .name("ahmad")
//                        .build();
//
//        Team team3=
//                Team.builder()
//                        .name("mohammad")
//                        .build();
//
//        Amplify.API.mutate(
//                ModelMutation.create(team1),
//                successResponse -> Log.i(TAG, "MainActivity.onCreate(): made a team successfully"),
//                failureResponse -> Log.i(TAG, "MainActivity.onCreate(): team failed with this response: "+failureResponse)
//        );
//        Amplify.API.mutate(
//                ModelMutation.create(team2),
//                successResponse -> Log.i(TAG, "MainActivity.onCreate(): made a team successfully"),
//                failureResponse -> Log.i(TAG, "MainActivity.onCreate(): team failed with this response: "+failureResponse)
//        );
//
//        Amplify.API.mutate(
//                ModelMutation.create(team3),
//                successResponse -> Log.i(TAG, "MainActivity.onCreate(): made a team successfully"),
//                failureResponse -> Log.i(TAG, "MainActivity.onCreate(): team failed with this response: "+failureResponse)
//        );

        init();
        Amplify.API.query(
                ModelQuery.list(Task.class),
                success ->
                {
                    Log.i(TAG, "Read Task successfully");
                    //products = new ArrayList<>();
                    tasks.clear();
                    for (Task databaseTask : success.getData()){
                        tasks.add(databaseTask);
                    }
                    //adapter.notifyDataSetChanged();
                    runOnUiThread(() ->{
                        adapter.notifyDataSetChanged();
                    });
                },
                failure -> Log.i(TAG, "Did not read task successfully")
        );


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

        });
        setUpLoginAndLogoutButton();
    }

//    private void analysis() {
//        try {
//
//            Amplify.configure(getApplicationContext());
//
//            Log.i("MyAmplifyApp", "Initialized Amplify");
//        } catch (AmplifyException error) {
//            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
//        }
//    }


    @Override
    protected void onResume() {
        super.onResume();

//        String username = preferences.getString(SettingActivity.USER_USERNAME_TAG, "No Username");
//
//        ((TextView)findViewById(R.id.txtUsername)).setText(getString(R.string.username_with_input, username));
        AuthUser authUser = Amplify.Auth.getCurrentUser();
        String username="";
        if (authUser == null){
            Button loginButton = (Button) findViewById(R.id.taskListLoginButton);
            loginButton.setVisibility(View.VISIBLE);
            Button logoutButton = (Button) findViewById(R.id.taskListLogoutButton);
            logoutButton.setVisibility(View.INVISIBLE);
        }else{
            username = authUser.getUsername();
            Log.i(TAG, "Username is: "+ username);
            Button loginButton = (Button) findViewById(R.id.taskListLoginButton);
            loginButton.setVisibility(View.INVISIBLE);
            Button logoutButton = (Button) findViewById(R.id.taskListLogoutButton);
            logoutButton.setVisibility(View.VISIBLE);

            String username2 = username; // ugly way for lambda hack
            Amplify.Auth.fetchUserAttributes(
                    success ->
                    {
                        Log.i(TAG, "Fetch user attributes succeeded for username: "+username2);
                        for (AuthUserAttribute userAttribute: success){
                            if(userAttribute.getKey().getKeyString().equals("email")){
                                String userEmail = userAttribute.getValue();
                                runOnUiThread(() ->
                                {
                                    ((TextView)findViewById(R.id.txtUsername)).setText(userEmail);
                                });
                            }
                        }
                    },
                    failure ->
                    {
                        Log.i(TAG, "Fetch user attributes failed: "+failure.toString());
                    }
            );
        }

        String userTeamName = preferences.getString(SettingActivity.USER_TEAM_NAME_TAG, "No Team");
        ((TextView) findViewById(R.id.textViewTeam)).setText(getString(R.string.teamname_with_input, userTeamName));

        Amplify.API.query(
                ModelQuery.list(Task.class),
                success -> {
                    Log.i(TAG, "Updated Tasks Successfully!");
                    tasks.clear();
                    for(Task task : success.getData()){
                        if (userTeamName.equals("No Team")){
                            tasks.add(task);
                        }
                        else if (task.getTeamName().getName().equals(userTeamName)) {
                            tasks.add(task);
                        }
                    }
                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                    });
                },

                failure -> Log.i(TAG, "failed with this response: ")
        );



//        tasks.clear();
//        tasks.addAll(appDatabase.taskDao().findAll());
//        adapter.notifyDataSetChanged();
    }


    private void setUpProductListRecyclerView(){

        RecyclerView taskListRecycleReview = (RecyclerView) findViewById(R.id.HomeListRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        taskListRecycleReview.setLayoutManager(layoutManager);
//        tasks.add(new Task("challenges", "Do your daily challenges ", State.IN_PROGRESS));
        adapter = new TaskListRecyclerVIewAdapter(tasks, this);
        taskListRecycleReview.setAdapter(adapter);


    }
    private void setUpLoginAndLogoutButton() {
        Button loginButton = (Button) findViewById(R.id.taskListLoginButton);
        loginButton.setOnClickListener(v ->
        {
            Intent goToLogInIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(goToLogInIntent);
        });

        Button logoutButton = (Button) findViewById(R.id.taskListLogoutButton);
        logoutButton.setOnClickListener(v ->
        {
            Amplify.Auth.signOut(
                    () ->
                    {
                        Log.i(TAG, "Logout succeeded");
                        runOnUiThread(() ->
                        {
                            ((TextView) findViewById(R.id.txtUsername)).setText("");
                        });
                        Intent goToLogInIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(goToLogInIntent);
                    },
                    failure ->
                    {
                        Log.i(TAG, "Logout failed");
                        runOnUiThread(() ->
                        {
                            Toast.makeText(MainActivity.this, "Log out failed", Toast.LENGTH_LONG);
                        });
                    }
            );
        });
    }
}