package com.openclassrooms.realestatemanager.View;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.openclassrooms.realestatemanager.Model.Photo;
import com.openclassrooms.realestatemanager.R;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder> {
    private List<Photo> photoList;
    private RequestManager glide;

    public PhotoAdapter(List<Photo> photoList, RequestManager glide) {
        this.photoList = photoList;
        this.glide = glide;
    }


    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.detail_property_fragment_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder photoViewHolder, int position) {
        photoViewHolder.updateWithPhoto(this.photoList.get(position), this.glide);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public void updateData(List<Photo> photoList){
        this.photoList = photoList;
        this.notifyDataSetChanged();
    }

    public Photo getPhoto(int position){
        return this.photoList.get(position);
    }
}
