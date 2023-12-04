package com.taskmaster.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;
import com.taskmaster.myapplication.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TaskDetailsActivity extends AppCompatActivity {
    private final String TAG = "TEXT SPECK";
    private MediaPlayer mp = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        Intent callingIntent = getIntent();

        String taskTitleString = null;
        String taskBodyString = null;
        String taskStateEnum = null;
        mp= new MediaPlayer();

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
        setUpSpeakButton();
    }

    private void setUpSpeakButton(){
        Button speakButton = (Button) findViewById(R.id.speachButton);
        speakButton.setOnClickListener(b ->
        {
            String taskDescription= ((TextView) findViewById(R.id.textTaskViewBody)).getText().toString();

            Amplify.Predictions.convertTextToSpeech(
                    taskDescription,
                    result -> playAudio(result.getAudioData()),
                    error -> Log.e(TAG, "Conversion failed: " + error.getMessage(), error)
            );
        });
    }
    // Taken from https://stackoverflow.com/a/25005243/16889809

    private void playAudio(InputStream data) {
        File mp3File = new File(getCacheDir(), "audio.mp3");

        try (OutputStream out = new FileOutputStream(mp3File)) {
            byte[] buffer = new byte[8 * 1_024];
            int bytesRead;
            while ((bytesRead = data.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            mp.reset();
            mp.setOnPreparedListener(MediaPlayer::start);
            mp.setDataSource(new FileInputStream(mp3File).getFD());
            mp.prepareAsync();
        } catch (IOException error) {
            Log.e("MyAmplifyApp", "Error writing audio file", error);
        }
    }
}