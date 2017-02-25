package com.diaby.scanexternalstorage;

import java.io.File;
import java.util.List;

/**
 * Created by abhijitnukalapati on 2/8/17.
 */

public class FileUtil {

    // reworked from http://stackoverflow.com/questions/7131930/get-the-file-size-in-android-sdk
    public static long getSize(File file, List<FileItem> files, List<FileItem> dirs) {
        long size = 0;
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                size += getSize(f, files, dirs);
            }
            dirs.add(new FileItem(file.getName(), size));
        } else {
            size = file.length();
            files.add(new FileItem(file.getName(), size));
        }
        return size;
    }
}
