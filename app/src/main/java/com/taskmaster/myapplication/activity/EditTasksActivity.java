package com.taskmaster.myapplication.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.State;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.material.snackbar.Snackbar;
import com.taskmaster.myapplication.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
    //34mins

    private Spinner teamSpinner = null;
    ActivityResultLauncher<Intent> activityResultLauncher;

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
        setUpAddImageButton();
        setUpDeleteImageButton();
        updateImageButtons();
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
        s3ImageKey = taskToEdit.getTaskImageS3Key();
        if (s3ImageKey != null && !s3ImageKey.isEmpty())
        {
            Amplify.Storage.downloadFile(
                    s3ImageKey,
                    new File(getApplication().getFilesDir(), s3ImageKey),
                    success ->
                    {
                        ImageView taskImageView = findViewById(R.id.edittaskImageImageView);
                        taskImageView.setImageBitmap(BitmapFactory.decodeFile(success.getFile().getPath()));
                    },
                    failure ->
                    {
                        Log.e(TAG, "Unable to get image from S3 for the task for S3 key: " + s3ImageKey + " for reason: " + failure.getMessage());
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
        Button saveButton = (Button)findViewById(R.id.edieeditbutton);
        saveButton.setOnClickListener(v ->
        {
            saveTask(s3ImageKey);
        });
    }

    private void saveTask(String imageS3Key)
    {
        List<Team> teams = null;
        String teamToSaveString = teamSpinner.getSelectedItem().toString();
        try
        {
            teams = teamFuture.get();
        }
        catch (InterruptedException ie)
        {
            Log.e(TAG, "InterruptedException while getting product");
            Thread.currentThread().interrupt();
        }
        catch (ExecutionException ee)
        {
            Log.e(TAG, "ExecutionException while getting product");
        }
        Team teamToSave = teams.stream().filter(c -> c.getName().equals(teamToSaveString)).findAny().orElseThrow(RuntimeException::new);
            Task taskToSave = Task.builder()
                    .title(titleEditText.getText().toString())
                    .id(taskToEdit.getId())
                    .body(descriptionEditText.getText().toString())
                    .teamName(teamToSave)
                    .state(taskCategoryFromString(taskSpinner.getSelectedItem().toString()))
                    .taskImageS3Key(s3ImageKey)
                    .build();

        Amplify.API.mutate(
                ModelMutation.update(taskToSave),  // making a GraphQL request to the cloud
                successResponse ->
                {
                    Log.i(TAG, "EditProductActivity.onCreate(): edited a product successfully");
                    Snackbar.make(findViewById(R.id.editTaskAcivity), "Product saved!", Snackbar.LENGTH_SHORT).show();
                },  // success callback
                failureResponse -> Log.i(TAG, "EditProductActivity.onCreate(): failed with this response: " + failureResponse)  // failure callback
        );
    }

//    private void setUpSaveButton()
//    {
//        Button saveButton = (Button)findViewById(R.id.edieeditbutton);
//        saveButton.setOnClickListener(v ->
//        {
//            List<Team> teams = null;
//            String teamToSaveString = teamSpinner.getSelectedItem().toString();
//            try
//            {
//                teams = teamFuture.get();
//            }
//            catch (InterruptedException ie)
//            {
//                Log.e(TAG, "InterruptedException while getting task");
//                Thread.currentThread().interrupt();
//            }
//            catch (ExecutionException ee)
//            {
//                Log.e(TAG, "ExecutionException while getting tasks");
//            }
//            Team teamToSave = teams.stream().filter(c -> c.getName().equals(teamToSaveString)).findAny().orElseThrow(RuntimeException::new);
//            Task taskToSave = Task.builder()
//                    .title(titleEditText.getText().toString())
//                    .id(taskToEdit.getId())
//                    .body(descriptionEditText.getText().toString())
//                    .teamName(teamToSave)
//                    .state(taskCategoryFromString(taskSpinner.getSelectedItem().toString()))
//                    .build();
//
//            Amplify.API.mutate(
//                    ModelMutation.update(taskToSave),  // making a GraphQL request to the cloud
//                    successResponse ->
//                    {
//                        Log.i(TAG, "EditTaskActivity.onCreate(): edited a task successfully");
//                        // TODO: Display a Snackbar
//                        Snackbar.make(findViewById(R.id.editTaskAcivity), "Task saved!", Snackbar.LENGTH_SHORT).show();
//                    },  // success callback
//                    failureResponse -> Log.i(TAG, "EditTaskActivity.onCreate(): failed with this response: " + failureResponse)  // failure callback
//            );
//        });
//    }

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
    private void setUpAddImageButton()
    {
        Button addImageButton = (Button) findViewById(R.id.edittaskAddImageButton);
        addImageButton.setOnClickListener(b ->
        {
            launchImageSelectionIntent();
        });

    }

    private void launchImageSelectionIntent()
    {
        // Part 1: Launch activity to pick file

        Intent imageFilePickingIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageFilePickingIntent.setType("*/*");  // only allow one kind or category of file; if you don't have this, you get a very cryptic error about "No activity found to handle Intent"
        imageFilePickingIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});
        // Below is simple version for testing
        //startActivity(imageFilePickingIntent);

        // Part 2: Create an image picking activity result launcher
        activityResultLauncher.launch(imageFilePickingIntent);

    }

    private ActivityResultLauncher<Intent> getImagePickingActivityResultLauncher()
    {
        // Part 2: Create an image picking activity result launcher
        ActivityResultLauncher<Intent> imagePickingActivityResultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>()
                        {
                            @Override
                            public void onActivityResult(ActivityResult result)
                            {
                                Button addImageButton = findViewById(R.id.edittaskAddImageButton);
                                if (result.getResultCode() == Activity.RESULT_OK)
                                {
                                    if (result.getData() != null)
                                    {
                                        Uri pickedImageFileUri = result.getData().getData();
                                        try
                                        {
                                            InputStream pickedImageInputStream = getContentResolver().openInputStream(pickedImageFileUri);
                                            String pickedImageFilename = getFileNameFromUri(pickedImageFileUri);
                                            Log.i(TAG, "Succeeded in getting input stream from file on phone! Filename is: " + pickedImageFilename);
                                            // Part 3: Use our InputStream to upload file to S3
                                            switchFromAddButtonToDeleteButton(addImageButton);
                                            uploadInputStreamToS3(pickedImageInputStream, pickedImageFilename,pickedImageFileUri);

                                        } catch (FileNotFoundException fnfe)
                                        {
                                            Log.e(TAG, "Could not get file from file picker! " + fnfe.getMessage(), fnfe);
                                        }
                                    }
                                }
                                else
                                {
                                    Log.e(TAG, "Activity result error in ActivityResultLauncher.onActivityResult");
                                }
                            }
                        }
                );

        return imagePickingActivityResultLauncher;
    }

    private void uploadInputStreamToS3(InputStream pickedImageInputStream, String pickedImageFilename,Uri pickedImageFileUri)
    {
        Amplify.Storage.uploadInputStream(
                pickedImageFilename,  // S3 key
                pickedImageInputStream,
                success ->
                {
                    Log.i(TAG, "Succeeded in getting file uploaded to S3! Key is: " + success.getKey());
                    // Part 4: Update/save our Product object to have an image key
                    saveTask(success.getKey());
                    updateImageButtons();
                    ImageView productImageView = findViewById(R.id.edittaskImageImageView);
                    InputStream pickedImageInputStreamCopy = null;  // need to make a copy because InputStreams cannot be reused!
                    try
                    {
                        pickedImageInputStreamCopy = getContentResolver().openInputStream(pickedImageFileUri);
                    }
                    catch (FileNotFoundException fnfe)
                    {
                        Log.e(TAG, "Could not get file stream from URI! " + fnfe.getMessage(), fnfe);
                    }
                    productImageView.setImageBitmap(BitmapFactory.decodeStream(pickedImageInputStreamCopy));

                },
                failure ->
                {
                    Log.e(TAG, "Failure in uploading file to S3 with filename: " + pickedImageFilename + " with error: " + failure.getMessage());
                }
        );
    }

    private void setUpDeleteImageButton()
    {
        Button deleteImageButton = (Button)findViewById(R.id.editTaskDeleteImageButton);
        String s3ImageKey = this.s3ImageKey;
        deleteImageButton.setOnClickListener(v ->
        {
            Amplify.Storage.remove(
                    s3ImageKey,
                    success ->
                    {
                        Log.i(TAG, "Succeeded in deleting file on S3! Key is: " + success.getKey());

                    },
                    failure ->
                    {
                        Log.e(TAG, "Failure in deleting file on S3 with key: " + s3ImageKey + " with error: " + failure.getMessage());
                    }
            );
            ImageView productImageView = findViewById(R.id.edittaskImageImageView);
            productImageView.setImageResource(android.R.color.transparent);

            saveTask("");
            switchFromDeleteButtonToAddButton(deleteImageButton);
        });
    }

    private void updateImageButtons() {
        Button addImageButton = findViewById(R.id.edittaskAddImageButton);
        Button deleteImageButton = findViewById(R.id.editTaskDeleteImageButton);
        runOnUiThread(() -> {
            if (s3ImageKey.isEmpty()) {
                deleteImageButton.setVisibility(View.INVISIBLE);
                addImageButton.setVisibility(View.VISIBLE);
            } else {
                deleteImageButton.setVisibility(View.VISIBLE);
                addImageButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void switchFromDeleteButtonToAddButton(Button deleteImageButton) {
        Button addImageButton = findViewById(R.id.edittaskAddImageButton);
        deleteImageButton.setVisibility(View.INVISIBLE);
        addImageButton.setVisibility(View.VISIBLE);
    }

    private void switchFromAddButtonToDeleteButton(Button addImageButton) {
        Button deleteImageButton = findViewById(R.id.editTaskDeleteImageButton);
        deleteImageButton.setVisibility(View.VISIBLE);
        addImageButton.setVisibility(View.INVISIBLE);
    }
    // Taken from https://stackoverflow.com/a/25005243/16889809
    @SuppressLint("Range")
    public String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}