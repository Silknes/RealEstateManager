package com.openclassrooms.realestatemanager.Controller.Fragments;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.openclassrooms.realestatemanager.Controller.Activities.AuthenticationActivity;
import com.openclassrooms.realestatemanager.Controller.Activities.DetailPropertyActivity;
import com.openclassrooms.realestatemanager.Injections.Injection;
import com.openclassrooms.realestatemanager.Injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.PropertyViewModel;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.ItemClickSupport;
import com.openclassrooms.realestatemanager.View.HouseAdapter;

import java.util.List;

public class PropertyFragment extends Fragment{
    private HouseAdapter adapter;
    private RecyclerView recyclerView;

    private PropertyViewModel propertyViewModel;
    private long userId;
    private SharedPreferences.Editor editor;

    private onItemClickedListener mCallback;

    public interface onItemClickedListener{
        void onItemClicked(int position);
    }

    public PropertyFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_property, container, false);

        recyclerView = view.findViewById(R.id.fragment_property_recycler_view);

        this.configureRecyclerView();
        this.configureOnClickRecyclerView();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MY_SHARED_PREFERENCES", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userId = getContext().getSharedPreferences("MY_SHARED_PREFERENCES", Context.MODE_PRIVATE).getLong("userId", -1);
        this.configureViewModel();

        this.getAllProperties();

        return view;
    }

    private void configureRecyclerView(){
        this.adapter = new HouseAdapter();
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_property_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if(!isLandscape()){
                    Intent intent = new Intent(getContext(), DetailPropertyActivity.class);
                    intent.putExtra("property", adapter.getProperty(position));
                    startActivity(intent);
                }
                else mCallback.onItemClicked(position);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.createCallbackToParentActivity();
    }

    private void createCallbackToParentActivity(){
        mCallback = (onItemClickedListener) getActivity();
    }

    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(getContext());
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
        this.propertyViewModel.init(userId);
    }

    private void getAllProperties(){
        this.propertyViewModel.getAllProperties().observe(this, this::updatePropertiesList);
    }

    private void updatePropertiesList(List<Property> propertyList){
        this.adapter.updateData(propertyList);
    }

    private boolean isLandscape(){
        return getResources().getBoolean(R.bool.is_landscape);
    }

}
