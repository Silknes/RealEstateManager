package com.openclassrooms.realestatemanager.Controller.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.openclassrooms.realestatemanager.Controller.Fragments.DetailPropertyFragment;
import com.openclassrooms.realestatemanager.R;

public class DetailPropertyActivity extends AppCompatActivity {
    private DetailPropertyFragment detailPropertyFragment;
    private boolean isEditMode;
    private LinearLayout snackbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_property);

        this.configureToolbar();
        this.configureAndShowDetailPropertyFragment();

        snackbarLayout = findViewById(R.id.activity_detail_property_fragment_container);

        isEditMode = false;
    }

    // Method that configure the fragment of this activity
    private void configureAndShowDetailPropertyFragment(){
        detailPropertyFragment = (DetailPropertyFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_detail_property_fragment_container);
        if(detailPropertyFragment == null){
            detailPropertyFragment = new DetailPropertyFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", getIntent().getExtras().getInt("position"));
            detailPropertyFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_detail_property_fragment_container, detailPropertyFragment)
                    .commit();
        }
    }

    // Method that configure the toolbar
    private void configureToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    // Method that inflate a menu to the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_detail_property, menu);
        return true;
    }

    // Method that manage the click on each item of the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            // If the user click on the item "edit" on the toolbar he is allowed to edit the property
            // On second click, our user have to choose to save or not the changes
            case R.id.menu_toolbar_item_edit_property :
                if(isEditMode){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Save changes ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    detailPropertyFragment.updateDetailProperty(true);
                                    isEditMode = false;
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    detailPropertyFragment.updateDetailProperty(false);
                                    isEditMode = false;
                                }
                            })
                            .show();
                }else{
                    Snackbar.make(snackbarLayout, "Enter edit mode", Snackbar.LENGTH_SHORT).show();
                    detailPropertyFragment.updateDetailProperty(false);
                    isEditMode = true;
                }
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

}
