package com.diaby.scanexternalstorage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;


import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobTrigger;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.List;

import static com.diaby.scanexternalstorage.ScanFilesIntentService.RESULT_BUNDLE_FILE_LIST_KEY;
import static com.diaby.scanexternalstorage.ScanFilesIntentService.RESULT_BUNDLE_KEY;
import static com.diaby.scanexternalstorage.ScanFilesIntentService.SCAN_FILES_RESULT_RECEIVER;

public class MainActivity extends AppCompatActivity {
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 167;

    private RecyclerView vFilesListView;
    private ProgressBar vProgressBar;
    private ResultReceiver mResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vFilesListView = (RecyclerView) findViewById(R.id.files_list);
        vProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        if (shouldAskForPermission(PERMISSION_READ_EXTERNAL_STORAGE)){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, PERMISSION_READ_EXTERNAL_STORAGE)) {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                        R.string.storage_permission_rationale, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.okay, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestStoragePermission();
                    }
                });
                snackbar.show();
            } else {
                requestStoragePermission();
            }
        } else {
            afterPermissionGranted();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    afterPermissionGranted();
                }
            }
        }
    }

    private boolean shouldAskForPermission(String permission) {
        int permissionState = ContextCompat.checkSelfPermission(this, permission);
        return permissionState != PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{ PERMISSION_READ_EXTERNAL_STORAGE }, REQUEST_READ_EXTERNAL_STORAGE);
    }

    private void afterPermissionGranted(){
        vProgressBar.setVisibility(View.VISIBLE);

        mResultReceiver = new FileListResultReceiver();

        Intent scanFilesIntent = new Intent();
        scanFilesIntent.setClass(this, ScanFilesIntentService.class);
        scanFilesIntent.putExtra(SCAN_FILES_RESULT_RECEIVER, mResultReceiver);
        startService(scanFilesIntent);

        // TODO: schedule job in the background to retrieve results - see scheduleJob() and BackgroundScanJobService
    }

    private void scheduleJob() {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(BackgroundScanJobService.class)
                .setTag("StorageScanJob")
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(0, 20)) // TODO: set appropriate trigger - most likely once a day
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();

        dispatcher.mustSchedule(myJob);
    }

    private class FileListResultReceiver extends ResultReceiver {

        FileListResultReceiver() {
            super(new Handler(Looper.getMainLooper()));
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            vProgressBar.setVisibility(View.GONE);

            if(resultCode == RESULT_BUNDLE_KEY && resultData.containsKey(RESULT_BUNDLE_FILE_LIST_KEY)) {
                List<FileItem> fileItemList = resultData.getParcelableArrayList(RESULT_BUNDLE_FILE_LIST_KEY);
                vFilesListView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                vFilesListView.setAdapter(new FilesListAdapter(fileItemList));
            } else {
                Log.d("Clover - MainActivity", "Cannot process result - missing required data");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mResultReceiver = null; // null out reference to prevent memory leaks
    }
}
