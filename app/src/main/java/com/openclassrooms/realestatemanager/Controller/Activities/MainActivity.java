package com.openclassrooms.realestatemanager.Controller.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.openclassrooms.realestatemanager.Controller.Fragments.AddPropertyFragment;
import com.openclassrooms.realestatemanager.Controller.Fragments.DetailPropertyFragment;
import com.openclassrooms.realestatemanager.Controller.Fragments.PropertyFragment;
import com.openclassrooms.realestatemanager.R;

/*
 First bug : textViewMain was plug with a TextView from the second activity layout
 Resolve it by replace the wrong id by the good id from the main activity layout

 Second bug : quantity was an Integer instead of a String
 Resolve it by change the type of quantity to String and add : "" + before Utils.convert...
*/

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PropertyFragment.onItemClickedListener{
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private DetailPropertyFragment detailPropertyFragment;
    private AddPropertyFragment addPropertyFragment;
    private FragmentManager fragmentManager;

    private boolean isEditMode;
    private LinearLayout snackbarLayout;

    //private TextView textViewMain;
    //private TextView textViewQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();

        fragmentManager = getSupportFragmentManager();

        this.configureAndShowPropertyFragment();

        snackbarLayout = findViewById(R.id.activity_main_container_snackbar);
        isEditMode = false;

        /*this.textViewMain = findViewById(R.id.activity_main_activity_text_view_main);
        this.textViewQuantity = findViewById(R.id.activity_main_activity_text_view_quantity);

        this.configureTextViewMain();
        this.configureTextViewQuantity();*/
    }

    private void configureToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void configureDrawerLayout(){
        drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView(){
        NavigationView navigationView = findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_toolbar_item_add_property :
                if(isLandscape()){
                    addPropertyFragment = new AddPropertyFragment();
                    removeAndReplaceFragment(detailPropertyFragment, addPropertyFragment);
                }
                else startAddPropertyActivity();
                return true;
            case R.id.menu_toolbar_item_edit_property :
                if(fragmentManager.getFragments().contains(detailPropertyFragment)){
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
                }else{
                    Snackbar.make(snackbarLayout, "Please choose a property to edit", Snackbar.LENGTH_SHORT).show();
                }
                return true;
            case R.id.menu_toolbar_item_search_property :
                Toast.makeText(this, R.string.toolbar_item_search, Toast.LENGTH_SHORT).show();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.activity_main_drawer_map :
                Toast.makeText(this, R.string.drawer_item_map, Toast.LENGTH_SHORT).show();
                break;
            case R.id.activity_main_drawer_logout :
                Toast.makeText(this, R.string.drawer_item_logout, Toast.LENGTH_SHORT).show();
                break;
            default :
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void configureAndShowPropertyFragment(){
        if(!isLandscape()){
            PropertyFragment propertyFragment = (PropertyFragment) fragmentManager
                    .findFragmentById(R.id.activity_main_fragment_container);
            if(propertyFragment == null){
                propertyFragment = new PropertyFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.activity_main_fragment_container, propertyFragment)
                        .commit();
            }
        }
        else {
            PropertyFragment propertyFragment = (PropertyFragment) fragmentManager
                    .findFragmentById(R.id.activity_main_fragment_container);
            detailPropertyFragment = (DetailPropertyFragment) fragmentManager
                    .findFragmentById(R.id.activity_detail_property_fragment_container);
            if(propertyFragment == null && detailPropertyFragment == null){
                propertyFragment = new PropertyFragment();
                detailPropertyFragment = new DetailPropertyFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("position", 0);
                detailPropertyFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(R.id.activity_main_fragment_container, propertyFragment)
                        .add(R.id.activity_main_fragment_container, detailPropertyFragment)
                        .commit();
            }
        }
    }

    private void removeAndReplaceFragment(Fragment fragmentToRemove, Fragment fragmentToAdd){
        fragmentManager.beginTransaction().remove(fragmentToRemove).commit();
        fragmentManager.beginTransaction().add(R.id.activity_main_fragment_container, fragmentToAdd).commit();
    }

    private void startAddPropertyActivity(){
        Intent intent = new Intent(this, AddPropertyActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if(this.drawerLayout.isDrawerOpen(GravityCompat.START)){
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else super.onBackPressed();
    }

    private boolean isLandscape(){
        return getResources().getBoolean(R.bool.is_landscape);
    }

    @Override
    public void onItemClicked(int position) {
        if(!fragmentManager.getFragments().contains(detailPropertyFragment)){
            detailPropertyFragment = new DetailPropertyFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            detailPropertyFragment.setArguments(bundle);
            removeAndReplaceFragment(addPropertyFragment, detailPropertyFragment);
        }
    }

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

    /***************************/

    /*private void configureTextViewMain(){
        this.textViewMain.setTextSize(15);
        this.textViewMain.setText("Le premier bien immobilier enregistré vaut ");
    }

    private void configureTextViewQuantity(){
        String quantity = "" + Utils.convertDollarToEuro(100);
        this.textViewQuantity.setTextSize(20);
        this.textViewQuantity.setText(quantity);
    }*/
}
