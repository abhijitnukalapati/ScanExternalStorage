package com.diaby.scanexternalstorage;

import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by abhijitnukalapati on 2/8/17.
 */

public class FilesListAdapter extends RecyclerView.Adapter<FilesListAdapter.FileItemViewHolder> {

    private List<FileItem> fileItems;

    public FilesListAdapter(List<FileItem> fileItems) {
        this.fileItems = fileItems;
    }

    @Override
    public FileItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        return new FileItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FileItemViewHolder holder, int position) {
        FileItem fileItem = fileItems.get(position);
        holder.name.setText(fileItem.getName());
        holder.size.setText(Formatter.formatFileSize(holder.itemView.getContext(), fileItem.getSize()));
    }

    @Override
    public int getItemCount() {
        return fileItems.size();
    }

    public static class FileItemViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView size;

        public FileItemViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.size = (TextView) itemView.findViewById(R.id.size);
        }
    }
}
