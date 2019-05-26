package com.example.furnishar.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.furnishar.R;

import java.util.ArrayList;
import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> photosList;

    public PhotosAdapter() {
        photosList = new ArrayList<>();
    }

    public void updatePhotoList(List<String> photosList) {
        this.photosList = photosList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());

        View view = layoutInflater.inflate(R.layout.photo_item, viewGroup, false);
        viewHolder = new PhotosItemViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((PhotosItemViewHolder) holder).populateItem(position);
    }

    @Override
    public int getItemCount() {
        return photosList.size();
    }

    private class PhotosItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView photo;

        private PhotosItemViewHolder(View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
        }

        public void populateItem(final int position) {
//            photo.setImageDrawable(photosList.get(position));
        }
    }
}
