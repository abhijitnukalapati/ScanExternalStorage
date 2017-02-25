package com.diaby.scanexternalstorage;

/**
 * Created by abhijitnukalapati on 2/8/17.
 */

import android.os.Environment;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BackgroundScanJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {

        Thread backgroundThread = new Thread(new ScanRunnable());
        backgroundThread.start();

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    private class ScanRunnable implements Runnable {

        @Override
        public void run() {
            ArrayList<FileItem> fileItemList = new ArrayList<>();

            List<FileItem> files = new ArrayList<>();
            List<FileItem> dirs = new ArrayList<>();

            File storageDirectory = Environment.getExternalStorageDirectory();
            for(File file: storageDirectory.listFiles()) {
                FileUtil.getSize(file, files, dirs);
            }

            fileItemList.addAll(dirs);
            fileItemList.addAll(files);

            Collections.sort(fileItemList, new FileItem.DescendingSizeComparator());

            // TODO: save items to disk
        }
    }
}