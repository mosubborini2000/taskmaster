package com.taskmaster.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.taskmaster.myapplication.R;

public class SettingActivity extends AppCompatActivity {

    SharedPreferences preferences;
    public static final String USER_USERNAME_TAG = "userUsername";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userUsername = preferences.getString(USER_USERNAME_TAG,"");

            EditText userUsernameEditText = (EditText) findViewById(R.id.editTextUsername);
            userUsernameEditText.setText(userUsername);
        Button saveButton = findViewById(R.id.buttonSaveUsername);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor preferneceEditor= preferences.edit();
                EditText userNicknameEditText = (EditText) findViewById(R.id.editTextUsername);
                String userNicknameString = userNicknameEditText.getText().toString();

                preferneceEditor.putString(USER_USERNAME_TAG, userNicknameString);//k,v
                preferneceEditor.apply();

                Snackbar.make(findViewById(R.id.SettingActivity), "Settings Saved", Snackbar.LENGTH_SHORT).show();
            }
        });



    }
}