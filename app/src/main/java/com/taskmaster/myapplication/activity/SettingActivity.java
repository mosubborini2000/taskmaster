package com.taskmaster.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.material.snackbar.Snackbar;
import com.taskmaster.myapplication.R;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {

    SharedPreferences preferences;
    public static final String USER_USERNAME_TAG = "userUsername";
    private static final String TAG = "setting";
    public static final String USER_TEAM_NAME_TAG = "teamName";
    Spinner teamSpinner = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        setUpTeamSpinner();

//        String userTeamName = preferences.getString(USER_TEAM_NAME_TAG,"");
//        String userUsername = preferences.getString(USER_USERNAME_TAG,"");
//
//            EditText userUsernameEditText = (EditText) findViewById(R.id.editTextUsername);
//            userUsernameEditText.setText(userUsername);
        Button saveButton = findViewById(R.id.buttonSaveUsername);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor preferneceEditor= preferences.edit();
                EditText userNicknameEditText = (EditText) findViewById(R.id.editTextUsername);
                String userNicknameString = userNicknameEditText.getText().toString();
                String userTeamNameString = teamSpinner.getSelectedItem().toString();


                preferneceEditor.putString(USER_USERNAME_TAG, userNicknameString);//k,v
                preferneceEditor.putString(USER_TEAM_NAME_TAG, userTeamNameString);
                preferneceEditor.apply();

                Snackbar.make(findViewById(R.id.SettingActivity), "Settings Saved", Snackbar.LENGTH_SHORT).show();
            }
        });



    }
    private void setUpTeamSpinner() {

        teamSpinner = (Spinner) findViewById(R.id.TeamSpinnerSetting);

        Amplify.API.query(
                ModelQuery.list(Team.class),
                success -> {
                    Log.i(TAG, "Read Teams Successfully!");
                    ArrayList<String> teamNames = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();
                    for(Team team : success.getData()){
                        teamNames.add(team.getName());
                        teams.add(team);
                    }
                    runOnUiThread(() -> {
                        teamSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.preference_category,
                                teamNames
                        ));
                    });
                },
                failure -> {
                    Log.i(TAG, "Failed to add team names!");
                }
        );

    }

}