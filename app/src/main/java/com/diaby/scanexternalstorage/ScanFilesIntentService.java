package com.diaby.scanexternalstorage;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by abhijitnukalapati on 2/8/17.
 */

public class ScanFilesIntentService extends IntentService {

    public static final String SCAN_FILES_RESULT_RECEIVER  = "ScanFilesResultReceiver";
    public static final String RESULT_BUNDLE_FILE_LIST_KEY  = "ResultBundleFileListKey";
    public static final int RESULT_BUNDLE_KEY = 324;

    public ScanFilesIntentService() {
        super("ScanFilesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
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
        ResultReceiver receiver = intent.getParcelableExtra(SCAN_FILES_RESULT_RECEIVER);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(RESULT_BUNDLE_FILE_LIST_KEY, fileItemList);

        receiver.send(RESULT_BUNDLE_KEY, bundle);

        // TODO: save items to disk
    }
}
