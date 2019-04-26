package com.openclassrooms.realestatemanager.Controller.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.openclassrooms.realestatemanager.Controller.Fragments.PropertyFragment;
import com.openclassrooms.realestatemanager.Controller.Fragments.SearchPropertyFragment;
import com.openclassrooms.realestatemanager.Model.Photo;
import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.R;

import java.util.List;

public class SearchPropertyActivity extends AppCompatActivity {
    private SearchPropertyFragment searchPropertyFragment;
    private boolean isRecyclerViewVisible = false;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_property);

        this.configureToolbar();
        this.configureAndShowSearchPropertyFragment();
    }

    // Method that configure the Toolbar
    private void configureToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab != null) ab.setDisplayHomeAsUpEnabled(true);
    }

    private void configureAndShowSearchPropertyFragment(){
        searchPropertyFragment = (SearchPropertyFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_search_property_fragment_container);
        if(searchPropertyFragment == null){
            searchPropertyFragment = new SearchPropertyFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_search_property_fragment_container, searchPropertyFragment)
                    .commit();
        }
    }
}
