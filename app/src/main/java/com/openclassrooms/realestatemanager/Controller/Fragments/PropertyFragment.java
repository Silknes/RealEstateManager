package com.openclassrooms.realestatemanager.Controller.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.Injections.Injection;
import com.openclassrooms.realestatemanager.Injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.Model.Photo;
import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.View.PropertyViewModel;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.ItemClickSupport;
import com.openclassrooms.realestatemanager.View.HouseAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PropertyFragment extends Fragment{
    private HouseAdapter adapter;
    private RecyclerView recyclerView;

    private PropertyViewModel propertyViewModel;
    private long userId;
    private List<Property> propertyList;
    private List<Photo> photoList;

    private RelativeLayout linearNoProperty;

    private onItemClickedListener mCallback;

    public interface onItemClickedListener{
        void onItemClicked(Property property);
        void onDataReceived(boolean isNotEmpty);
    }

    public PropertyFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_property, container, false);

        recyclerView = view.findViewById(R.id.fragment_property_recycler_view);
        linearNoProperty = view.findViewById(R.id.fragment_property_empty_list);

        this.configureRecyclerView();
        this.configureOnClickRecyclerView();

        userId = Objects.requireNonNull(getContext()).getSharedPreferences("MY_SHARED_PREFERENCES", Context.MODE_PRIVATE).getLong("userId", -1);
        this.configureViewModel();

        this.getAllProperties();

        return view;
    }

    /*********************************
    **** Configuring recyclerView ****
    *********************************/

    private void configureRecyclerView(){
        this.propertyList = new ArrayList<>();
        this.photoList = new ArrayList<>();
        this.adapter = new HouseAdapter(this.propertyList, this.photoList, Glide.with(this));
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_property_item)
                .setOnItemClickListener((recyclerView, position, v) -> mCallback.onItemClicked(adapter.getProperty(position)));
    }

    /*******************************************
    **** Manage callback to parent activity ****
    *******************************************/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.createCallbackToParentActivity();
    }

    private void createCallbackToParentActivity(){
        mCallback = (onItemClickedListener) getActivity();
    }

    /******************
    **** Manage DB ****
    ******************/

    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(getContext());
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
        this.propertyViewModel.init(userId);
    }

    // Method that return all properties in DB
    public void getAllProperties(){
        this.propertyViewModel.getAllProperties().observe(this, this::getPropertiesMainPhoto);
    }

    // Get main photos for each property
    private void getPropertiesMainPhoto(List<Property> propertyList){
        if(!propertyList.isEmpty()){
            this.propertyList = propertyList;
            this.propertyViewModel.getMainPhotos().observe(this, this::updateAdapter);
            if(isLandscape()) mCallback.onItemClicked(this.propertyList.get(0));
        } else {
            linearNoProperty.setVisibility(View.VISIBLE);
            mCallback.onDataReceived(false);
        }
    }

    // Updating our recyclerView with data get from DB
    private void updateAdapter(List<Photo> photoList){
        this.photoList = photoList;
        adapter.updateData(propertyList, photoList);
    }

    // Get a value that define if phone is in portrait or not
    private boolean isLandscape(){
        return getResources().getBoolean(R.bool.is_landscape);
    }

    public void test(List<Property> propertyList, List<Photo> photoList){
        this.propertyList = propertyList;
        this.photoList = photoList;
        adapter.updateData(this.propertyList, this.photoList);
    }
}
