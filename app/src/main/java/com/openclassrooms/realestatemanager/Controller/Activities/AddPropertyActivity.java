package com.openclassrooms.realestatemanager.Controller.Activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.openclassrooms.realestatemanager.Controller.Fragments.AddPropertyFragment;
import com.openclassrooms.realestatemanager.R;

public class AddPropertyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);

        this.configureToolbar();
        this.configureAndShowAddPropertyFragment();
    }

    // Method that configure the fragment for this activity
    private void configureAndShowAddPropertyFragment(){
        AddPropertyFragment addPropertyFragment = (AddPropertyFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_add_property_fragment_container);
        if(addPropertyFragment == null){
            addPropertyFragment = new AddPropertyFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_add_property_fragment_container, addPropertyFragment)
                    .commit();
        }
    }

    // Method that configure the Toolbar
    private void configureToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    // Method that manage the click on each Checkbox of the fragment
    public void onCheckboxClicked(View view){
        boolean isChecked = ((CheckBox) view).isChecked();
        switch(view.getId()){
            case R.id.fragment_add_property_checkbox_school:
                if(isChecked) Toast.makeText(this, "School Checked", Toast.LENGTH_SHORT).show();
                else Toast.makeText(this, "School Unchecked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_add_property_checkbox_shop:
                if(isChecked) Toast.makeText(this, "Shop Checked", Toast.LENGTH_SHORT).show();
                else Toast.makeText(this, "Shop Unchecked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_add_property_checkbox_parc:
                if(isChecked) Toast.makeText(this, "Parc Checked", Toast.LENGTH_SHORT).show();
                else Toast.makeText(this, "Parc Unchecked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_add_property_checkbox_public_transport:
                if(isChecked) Toast.makeText(this, "Public Transport Checked", Toast.LENGTH_SHORT).show();
                else Toast.makeText(this, "Public Transport Unchecked", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
