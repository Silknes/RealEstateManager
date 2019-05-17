package com.openclassrooms.realestatemanager.Controller.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;
import android.widget.Toast;

import com.openclassrooms.realestatemanager.Controller.Fragments.AddPropertyFragment;
import com.openclassrooms.realestatemanager.Controller.Fragments.DetailPropertyFragment;
import com.openclassrooms.realestatemanager.Controller.Fragments.EditPropertyFragment;
import com.openclassrooms.realestatemanager.Controller.Fragments.MortgageSimulatorFragment;
import com.openclassrooms.realestatemanager.Controller.Fragments.PropertyFragment;
import com.openclassrooms.realestatemanager.Controller.Fragments.SearchPropertyFragment;
import com.openclassrooms.realestatemanager.Injections.Injection;
import com.openclassrooms.realestatemanager.Injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.Model.Photo;
import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.Model.User;
import com.openclassrooms.realestatemanager.View.PropertyViewModel;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.Utils;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/*
 * First bug : textViewMain was plug with a TextView from the second activity layout
 * Resolve it by replace the wrong id by the good id from the main activity layout
 * Second bug : quantity was an Integer instead of a String
 * Resolve it by change the type of quantity to String and add : "" + before Utils.convert...
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PropertyFragment.onItemClickedListener, AddPropertyFragment.OnButtonClickedListener, SearchPropertyFragment.OnItemClickedListener, DetailPropertyFragment.OnButtonClickedListener{
    private String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int RC_MAPS_ACTIVITY = 1;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private FragmentManager fragmentManager;
    private PropertyFragment propertyFragment;
    private AddPropertyFragment addPropertyFragment;
    private EditPropertyFragment editPropertyFragment;
    private DetailPropertyFragment detailPropertyFragment;

    private NavigationView navigationView;
    private TextView txtUserMail, txtUsername;

    private boolean[] pois = new boolean[]{false, false, false, false};

    private PropertyViewModel propertyViewModel;

    private Property property;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!EasyPermissions.hasPermissions(this, perms)){
            Intent intent = new Intent(this, PermissionsActivity.class);
            startActivity(intent);
        } else {
            long userId = getSharedPreferences("MY_SHARED_PREFERENCES", Context.MODE_PRIVATE).getLong("userId", -1);
            fragmentManager = getSupportFragmentManager();

            if(userId <= 0){
                Intent intent = new Intent(this, AuthenticationActivity.class);
                startActivity(intent);
            } else {
                this.configureAndShowDefaultFragment();

                this.configureToolbar();
                this.configureDrawerLayout();
                this.configureNavigationView();

                txtUserMail = navigationView.getHeaderView(0).findViewById(R.id.navigation_header_txt_user_mail);
                txtUsername = navigationView.getHeaderView(0).findViewById(R.id.navigation_header_txt_username);

                this.configureViewModel();
                this.getCurrentUser(userId);
            }
        }
    }

    /**********************************************
    **** Configure menus and menu item actions ****
    **********************************************/

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
        navigationView = findViewById(R.id.activity_main_nav_view);
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
                addPropertyFragment = new AddPropertyFragment();
                this.configureAndShowFragment(addPropertyFragment);
                return true;
            case R.id.menu_toolbar_item_edit_property :
                this.configureClickOnItemEdit();
                return true;
            case R.id.menu_toolbar_item_search_property :
                SearchPropertyFragment searchPropertyFragment = new SearchPropertyFragment();
                this.configureAndShowFragment(searchPropertyFragment);
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    // Onclick item edit on toolbar, create an alert dialog to know if user add to save changes or not
    private void configureClickOnItemEdit(){
        if(fragmentManager.getFragments().contains(editPropertyFragment)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.save_changes))
                    .setPositiveButton(getString(R.string.alert_dialog_default_positive_message), (dialogInterface, i) -> {
                        editPropertyFragment.updateDB();
                        detailPropertyFragment = new DetailPropertyFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("property", editPropertyFragment.getNewProperty());
                        detailPropertyFragment.setArguments(bundle);
                        this.configureAndShowFragment(detailPropertyFragment);
                    })
                    .setNegativeButton(getString(R.string.alert_dialog_default_negative_message), (dialogInterface, i) -> {
                        this.configureAndShowFragment(propertyFragment);
                    })
                    .show();
        } else if(fragmentManager.getFragments().contains(detailPropertyFragment)){
            editPropertyFragment = new EditPropertyFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("property", property);
            editPropertyFragment.setArguments(bundle);
            this.configureAndShowFragment(editPropertyFragment);
        } else Toast.makeText(this, getString(R.string.warning_message_edit_button), Toast.LENGTH_SHORT).show();
    }

    // Configuring action to do when user select an item on the navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.activity_main_drawer_map :
                if(Utils.isInternetAvailable(this)){
                    Intent intent = new Intent(this, MapsActivity.class);
                    startActivityForResult(intent, RC_MAPS_ACTIVITY);
                } else Toast.makeText(this, getString(R.string.warning_message_no_internet), Toast.LENGTH_SHORT).show();
                break;
            case R.id.activity_main_drawer_logout :
                SharedPreferences sharedPreferences = this.getSharedPreferences("MY_SHARED_PREFERENCES", Context.MODE_PRIVATE);
                sharedPreferences.edit().putLong("userId", -1).apply();
                Intent intentAuth = new Intent(this, AuthenticationActivity.class);
                startActivity(intentAuth);
                break;
            default :
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Configure action when checkbox state change
    public void onCheckboxClicked(View view){
        boolean isChecked = ((CheckBox) view).isChecked();
        switch(view.getId()){
            case R.id.fragment_add_property_checkbox_school:
                pois[0] = isChecked;
                break;
            case R.id.fragment_add_property_checkbox_shop:
                pois[1] = isChecked;
                break;
            case R.id.fragment_add_property_checkbox_parc:
                pois[2] = isChecked;
                break;
            case R.id.fragment_add_property_checkbox_public_transport:
                pois[3] = isChecked;
                break;
        }
        addPropertyFragment.updatePoi(pois);
    }

    /***********************************************
    **** Manage DB and update view if necessary ****
    ***********************************************/

    // Configuring the view model, necessary to get user data
    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
    }

    // Method that get the current user
    private void getCurrentUser(long userId){
        this.propertyViewModel.updateCurrentUser(userId);
        this.propertyViewModel.getCurrentUser().observe(this, this::updateNavHeaderTxt);
    }

    // Method that update 2 views after getting user data
    private void updateNavHeaderTxt(User user){
        txtUserMail.setText(user.getEmail());
        txtUsername.setText(Utils.uppercaseFirstLetter(user.getUsername()));
    }

    /***************************
    **** Managing fragments ****
    ***************************/

    // Method that init fragment
    private void configureAndShowDefaultFragment(){
        if(fragmentManager.getFragments().isEmpty()){
            propertyFragment = (PropertyFragment) fragmentManager
                    .findFragmentById(R.id.activity_main_fragment_container);
            if(propertyFragment == null) {
                propertyFragment = new PropertyFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.activity_main_fragment_container, propertyFragment)
                        .commit();
            }
        } else {
            propertyFragment = new PropertyFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.activity_main_fragment_container, propertyFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    // Method used to swap to an other fragment
    private void configureAndShowFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if(isLandscape()){
            transaction.replace(R.id.activity_main_fragment_container, propertyFragment)
                    .add(R.id.activity_main_fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            transaction.replace(R.id.activity_main_fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    // Method that manage the click on recyclerView items from PropertyFragment (used when phone is in landscape)
    @Override
    public void onItemClicked(Property property) {
        this.property = property;
        detailPropertyFragment = new DetailPropertyFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("property", property);
        detailPropertyFragment.setArguments(bundle);
        this.configureAndShowFragment(detailPropertyFragment);
    }

    @Override
    public void onSearchButtonClicked(List<Property> propertyList, List<Photo> photoList) {
        propertyFragment.test(propertyList, photoList);
    }

    @Override
    public void onDataReceived(boolean isNotEmpty) {
        if(isLandscape()){
            if(!isNotEmpty) {
                addPropertyFragment = new AddPropertyFragment();
                this.configureAndShowFragment(addPropertyFragment);
            }
        }
    }

    // Method that manage the click on the button in AddPropertyFragment
    @Override
    public void onAddPropertyButtonClicked(Property property) {
        if(!isLandscape()){
            propertyFragment = new PropertyFragment();
            this.configureAndShowFragment(propertyFragment);
        } else {
            propertyFragment.getAllProperties();
            editPropertyFragment = new EditPropertyFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("property", property);
            editPropertyFragment.setArguments(bundle);
            this.configureAndShowFragment(editPropertyFragment);
        }
    }

    // Method that manage the click on the button in EditPropertyFragment
    @Override
    public void onMortgageSimulatorButtonClicked(double price) {
        MortgageSimulatorFragment mortgageSimulatorFragment = new MortgageSimulatorFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble("amount", price);
        mortgageSimulatorFragment.setArguments(bundle);
        this.configureAndShowFragment(mortgageSimulatorFragment);
    }

    /**********************
    **** Other methods ****
    **********************/

    // Get a value that define if phone is in portrait or not
    private boolean isLandscape(){
        return getResources().getBoolean(R.bool.is_landscape);
    }

    @Override
    public void onBackPressed() {
        if(this.drawerLayout.isDrawerOpen(GravityCompat.START)){
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else super.onBackPressed();
    }

    // Getting the result of MapsActivity that return a property
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_MAPS_ACTIVITY && resultCode == RESULT_OK){
            editPropertyFragment = new EditPropertyFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("property", data.getSerializableExtra("property"));
            editPropertyFragment.setArguments(bundle);
            this.configureAndShowFragment(editPropertyFragment);
        }
    }
}
