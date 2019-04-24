package com.openclassrooms.realestatemanager.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.openclassrooms.realestatemanager.Model.Photo;
import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.R;

import java.util.ArrayList;
import java.util.List;

public class HouseAdapter extends RecyclerView.Adapter<HouseViewHolder> {
    private List<Property> propertyList;
    private List<Photo> photoList;
    private RequestManager glide;

    public HouseAdapter(List<Property> propertyList, List<Photo> photoList, RequestManager glide) {
        this.propertyList = propertyList;
        this.photoList = photoList;
        this.glide = glide;
    }

    @Override
    public HouseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_property_item, parent, false);

        return new HouseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HouseViewHolder holder, int position) {
        holder.updateWithPropertiesData(this.propertyList.get(position), this.photoList.get(position), this.glide);
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public void updateData(List<Property> propertyList, List<Photo> photoList){
        this.propertyList = propertyList;
        this.photoList = photoList;
        this.notifyDataSetChanged();
    }

    public Property getProperty(int position){
        return this.propertyList.get(position);
    }
}
