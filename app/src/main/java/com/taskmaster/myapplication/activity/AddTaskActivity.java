package com.taskmaster.myapplication.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
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

import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.State;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.snackbar.Snackbar;
import com.taskmaster.myapplication.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AddTaskActivity extends AppCompatActivity {

    public static final String TAG = "addTaskActivity";

    static final int LOCATION_POLLING_INTERVAL = 5 * 1000;

    private CompletableFuture<List<Team>> teamFuture = null;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private Spinner taskCategorySpinner = null;
    private Spinner teamNameSpinner = null;

    ActivityResultLauncher<Intent> activityResultLauncher;

    FusedLocationProviderClient locationProviderClient = null;
    Geocoder geocoder = null;



    private String s3ImageKey = "";
    private void analytic() {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .name("taskDetails")
                .addProperty("time", Long.toString(new Date().getTime()))
                .addProperty("trackingEvent", " main activity opened")
                .build();

        Amplify.Analytics.recordEvent(event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        analytic();

        teamFuture = new CompletableFuture<>();

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        locationProviderClient.flushLocations();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                Log.e(TAG, "Location CallBack was null");
            }
            String currentLatitude = Double.toString(location.getLatitude());
            String currentLongitude = Double.toString(location.getLongitude());
            Log.i(TAG, "Our userLatitude: " + location.getLatitude());
            Log.i(TAG, "Our userLongitude: " + location.getLongitude());
        });

        locationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        });

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_POLLING_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                new Thread(() -> {
                    try {
                        String address = geocoder.getFromLocation(
                                        locationResult.getLastLocation().getLatitude(),
                                        locationResult.getLastLocation().getLongitude(),
                                        1)
                                .get(0)
                                .getAddressLine(0);
                        Log.i(TAG, "Repeating current location is: " + address);
                    } catch (IOException ioe) {
                        Log.e(TAG, "Could not get subscribed location: " + ioe.getMessage(), ioe);
                    }
                }).start();
            }
        };


        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());

        activityResultLauncher = getImagePickingActivityResultLauncher();

        setUpEditableUIElement();
        setUpSaveButton();
        setUpAddImageButton();
        updateImageButtons();
    }



    @Override
    protected void onResume()
    {
        super.onResume();

        Intent callingIntent = getIntent();
        if (callingIntent != null && callingIntent.getType() != null && callingIntent.getType().equals("text/plain")) {
            String callingText = callingIntent.getStringExtra(Intent.EXTRA_TEXT);

            if (callingText != null) {
                String cleanedText = cleanText(callingText);

                ((EditText) findViewById(R.id.titleText)).setText(cleanedText);
            }
        }

        if(callingIntent != null && callingIntent.getType() != null && callingIntent.getType().startsWith("image") ){
            Uri incomingImageFileUri= callingIntent.getParcelableExtra(Intent.EXTRA_STREAM);

            if (incomingImageFileUri != null){
                InputStream incomingImageFileInputStream = null;

                try {
                    incomingImageFileInputStream = getContentResolver().openInputStream(incomingImageFileUri);

                    ImageView productImageView = findViewById(R.id.taskImageImageView);

                    if (productImageView != null) {

                        productImageView.setImageBitmap(BitmapFactory.decodeStream(incomingImageFileInputStream));
                    }else {
                        Log.e(TAG, "ImageView is null for some reasons");
                    }
                }catch (FileNotFoundException fnfe){
                    Log.e(TAG," Could not get file stram from the URI "+fnfe.getMessage(),fnfe);
                }
            }
        }

    }

    private void setUpEditableUIElement() {
        titleEditText = findViewById(R.id.titleText);
        descriptionEditText = findViewById(R.id.bodyText);

        setUpSpinners();
    }

    private void setUpSpinners() {
        teamNameSpinner = findViewById(R.id.spinnerteam);

        Amplify.API.query(
                ModelQuery.list(Team.class),
                success -> {
                    Log.i(TAG, "Read Team Name successfully!");
                    ArrayList<String> teamNames = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();
                    for (Team team : success.getData()) {
                        teams.add(team);
                        teamNames.add(team.getName());
                    }
                    teamFuture.complete(teams);

                    runOnUiThread(() -> {
                        teamNameSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                teamNames));
                    });
                },
                failure -> {
                    teamFuture.complete(null);
                    Log.i(TAG, "Did not read Team Name successfully!");
                }
        );

        taskCategorySpinner = findViewById(R.id.spinner);
        taskCategorySpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                State.values()));
    }

    private void setUpSaveButton() {
        Button saveButton = findViewById(R.id.submitButton);
        saveButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            locationProviderClient.getLastLocation().addOnSuccessListener(location ->
                            {
                                if (location == null) {
                                    Log.e(TAG, "Location CallBack was null");
                                }
                                String currentLatitude = Double.toString(location.getLatitude());
                                String currentLongitude = Double.toString(location.getLongitude());
                                Log.i(TAG, "Our userLatitude: " + location.getLatitude());
                                Log.i(TAG, "Our userLongitude: " + location.getLongitude());
                                saveTask(s3ImageKey, currentLatitude, currentLongitude);

                            }

                    ).addOnCanceledListener(() ->
                    {
                        Log.e(TAG, "Location request was Canceled");
                    })
                    .addOnFailureListener(failure ->
                    {
                        Log.e(TAG, "Location request failed, Error was: " + failure.getMessage(), failure.getCause());
                    })
                    .addOnCompleteListener(complete ->
                    {
                        Log.e(TAG, "Location request Completed");
                    });

//            saveTask(s3ImageKey);
        });
    }

    private void saveTask(String imageS3Key , String latitude, String longitude) {
        List<Team> teams = null;
        String teamToSaveString = teamNameSpinner.getSelectedItem().toString();
        try {
            teams = teamFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Team teamToSave = teams.stream().filter(c -> c.getName().equals(teamToSaveString)).findAny().orElseThrow(RuntimeException::new);


        Task taskToSave = Task.builder()
                .title(titleEditText.getText().toString())
                .body(descriptionEditText.getText().toString())
                .teamName(teamToSave)
                .state(TaskCategoryFromString(taskCategorySpinner.getSelectedItem().toString()))
                .taskLatitude(latitude)
                .taskLongitude(longitude)
                .taskImageS3Key(imageS3Key)
                .build();

        Amplify.API.mutate(
                ModelMutation.create(taskToSave),
                successResponse -> {
                    Log.i(TAG, "AddTaskActivity.onCreate(): added a Task successfully");
                    Snackbar.make(findViewById(R.id.addTaskActivity), "Task saved!", Snackbar.LENGTH_SHORT).show();
                    finish(); // Close the activity after saving the task
                },
                failureResponse -> Log.i(TAG, "AddTaskActivity.onCreate(): failed with this response: " + failureResponse)
        );
    }

    private int getSpinnerIndex(Spinner spinner, String stringValueToCheck) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(stringValueToCheck)) {
                return i;
            }
        }
        return 0;
    }

    private void setUpAddImageButton() {
        Button addImageButton = findViewById(R.id.addTaskAddImageButton);
        addImageButton.setOnClickListener(b -> {
            launchImageSelectionIntent();
        });
    }

    private void launchImageSelectionIntent() {
        Intent imageFilePickingIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageFilePickingIntent.setType("*/*");
        imageFilePickingIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});

        activityResultLauncher.launch(imageFilePickingIntent);
    }

    private void updateImageButtons() {
        Button addImageButton = findViewById(R.id.addTaskAddImageButton);
        Button deleteImageButton = findViewById(R.id.addTaskDeleteImageButton);

        runOnUiThread(() -> {
            if (s3ImageKey == null || s3ImageKey.isEmpty()) {
                deleteImageButton.setVisibility(View.INVISIBLE);
                addImageButton.setVisibility(View.VISIBLE);
            } else {
                deleteImageButton.setVisibility(View.VISIBLE);
                addImageButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void switchFromDeleteButtonToAddButton(Button deleteImageButton) {
        Button addImageButton = findViewById(R.id.addTaskAddImageButton);
        deleteImageButton.setVisibility(View.INVISIBLE);
        addImageButton.setVisibility(View.VISIBLE);
    }

    private void switchFromAddButtonToDeleteButton(Button addImageButton) {
        Button deleteImageButton = findViewById(R.id.addTaskDeleteImageButton);
        deleteImageButton.setVisibility(View.VISIBLE);
        addImageButton.setVisibility(View.INVISIBLE);
    }

    private ActivityResultLauncher<Intent> getImagePickingActivityResultLauncher() {
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Button addImageButton = findViewById(R.id.addTaskAddImageButton);
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            if (result.getData() != null) {
                                Uri pickedImageFileUri = result.getData().getData();
                                try {
                                    InputStream pickedImageInputStream = getContentResolver().openInputStream(pickedImageFileUri);
                                    String pickedImageFilename = getFileNameFromUri(pickedImageFileUri);
                                    Log.i(TAG, "Succeeded in getting input stream from file on phone! Filename is: " + pickedImageFilename);
                                    switchFromAddButtonToDeleteButton(addImageButton);
                                    uploadInputStreamToS3(pickedImageInputStream, pickedImageFilename, pickedImageFileUri);

                                } catch (FileNotFoundException fnfe) {
                                    Log.e(TAG, "Could not get file from file picker! " + fnfe.getMessage(), fnfe);
                                }
                            }
                        } else {
                            Log.e(TAG, "Activity result error in ActivityResultLauncher.onActivityResult");
                        }
                    }
                }
        );
    }

    private void uploadInputStreamToS3(InputStream pickedImageInputStream, String pickedImageFilename, Uri pickedImageFileUri) {
        Amplify.Storage.uploadInputStream(
                pickedImageFilename,
                pickedImageInputStream,
                success -> {
                    Log.i(TAG, "Succeeded in getting file uploaded to S3! Key is: " + success.getKey());
                    s3ImageKey = success.getKey();
                    updateImageButtons();
                    ImageView taskImageView = findViewById(R.id.taskImageImageView);
                    InputStream pickedImageInputStreamCopy = null;

                    try {
                        pickedImageInputStreamCopy = getContentResolver().openInputStream(pickedImageFileUri);
                    } catch (FileNotFoundException fnfe) {
                        Log.e(TAG, "Could not get file stream from URI! " + fnfe.getMessage(), fnfe);
                    }

                    taskImageView.setImageBitmap(BitmapFactory.decodeStream(pickedImageInputStreamCopy));
                },
                failure -> {
                    Log.e(TAG, "Failure in uploading file to S3 with filename: " + pickedImageFilename + " with error: " + failure.getMessage());
                }
        );
    }

    private void setUpDeleteImageButton() {
        Button deleteImageButton = findViewById(R.id.addTaskDeleteImageButton);
        deleteImageButton.setOnClickListener(v -> {
            Amplify.Storage.remove(
                    s3ImageKey,
                    success -> {
                        Log.i(TAG, "Succeeded in deleting file on S3! Key is: " + success.getKey());
                    },
                    failure -> {
                        Log.e(TAG, "Failure in deleting file on S3 with key: " + s3ImageKey + " with error: " + failure.getMessage());
                    }
            );

            ImageView taskImageView = findViewById(R.id.taskImageImageView);
            taskImageView.setImageResource(android.R.color.transparent);

            switchFromDeleteButtonToAddButton(deleteImageButton);
        });
    }

    // The rest of the code remains unchanged

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

    public static State TaskCategoryFromString(String inputTaskStateEnumText){
        for (State taskStatusEnum : State.values()){
            if(taskStatusEnum.toString().equals(inputTaskStateEnumText)){
                return taskStatusEnum;
            }
        }
        return null;
    }
    private String cleanText(String text) {
        // Remove links
        text = text.replaceAll("\\b(?:https?|ftp):\\/\\/\\S+\\b", "");

        // Remove double quotes
        text = text.replaceAll("\"", "");

        return text;
    }
}