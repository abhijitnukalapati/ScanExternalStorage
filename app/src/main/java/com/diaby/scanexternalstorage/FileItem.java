package com.diaby.scanexternalstorage;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by abhijitnukalapati on 2/8/17.
 */

public class FileItem implements Parcelable {

    private String name;
    private long size; // size in bytes

    public FileItem(String name, long size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public static class DescendingSizeComparator implements Comparator<FileItem> {
        @Override
        public int compare(FileItem f1, FileItem f2) {
            return (f1.getSize() < f2.getSize()) ? 1 : ((f1.getSize() == f2.getSize()) ? 0 : -1);
        }
    }

    protected FileItem(Parcel in) {
        name = in.readString();
        size = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(size);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FileItem> CREATOR = new Parcelable.Creator<FileItem>() {
        @Override
        public FileItem createFromParcel(Parcel in) {
            return new FileItem(in);
        }

        @Override
        public FileItem[] newArray(int size) {
            return new FileItem[size];
        }
    };
}
