package com.openclassrooms.realestatemanager.Controller.Activities;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.openclassrooms.realestatemanager.Injections.Injection;
import com.openclassrooms.realestatemanager.Injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.Model.GeocodingApi.ApiResult;
import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.View.PropertyViewModel;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.MapsApiCalls;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MapsApiCalls.CallbacksGeocoding {
    private GoogleMap mMap;
    private PropertyViewModel propertyViewModel;
    private List<String> addressList;
    private List<Property> propertyList;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        this.configurePropertyViewModel();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker at the last known location,
        // add a marker for each property,
        // add a listener to each marker to display property detail
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng userPosition = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(userPosition).title(getString(R.string.maps_user_position)));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(userPosition).zoom(10).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            this.addMarkerForEachProperty();

            mMap.setOnMarkerClickListener(marker -> {
                if (!marker.getTitle().equals("Your position")){
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("property", propertyList.get(Integer.parseInt(marker.getSnippet())));
                    setResult(RESULT_OK, intent);
                    finish();
                }
                return false;
            });
        } else Toast.makeText(this, getString(R.string.maps_warning_message_no_location), Toast.LENGTH_SHORT).show();
    }

    /*********************************
    **** Get all property address ****
    *********************************/

    private void configurePropertyViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
    }

    // Method that get all property and calling method that get and format the address for each property
    private void addMarkerForEachProperty(){
        this.propertyViewModel.getAllProperties().observe(this, this::getAddresses);
    }

    // Method that get and format the address for each property
    private void getAddresses(List<Property> propertyList){
        if(!propertyList.isEmpty()){
            this.propertyList = propertyList;
            addressList = new ArrayList<>();
            for (int i = 0; i < propertyList.size(); i++) {
                Property property = propertyList.get(i);
                String address = property.getHouseNumber() + property.getAddress() + "," + property.getCity() + "," + property.getPostalCode();
                addressList.add(address);
            }
            this.fetchLocationFromAddress();
        }
    }

    /****************************************
    **** Format String address to LatLng ****
    ****************************************/

    // Method that fetch the location for each property according to is format address
    private void fetchLocationFromAddress(){
        MapsApiCalls.fetchLocationFromAddress(this, getString(R.string.api_key), addressList.get(position));
    }

    // Get the result of the fetchLocation and add a marker for this property
    @Override
    public void onResponseGeocoding(@Nullable ApiResult apiResult) {
        if(apiResult != null){
            LatLng propertyPosition = new LatLng(
                    apiResult.getResults().get(0).getGeometry().getLocation().getLat(),
                    apiResult.getResults().get(0).getGeometry().getLocation().getLng());
            String address = apiResult.getResults().get(0).getFormattedAddress();

            mMap.addMarker(new MarkerOptions().position(propertyPosition).title(address).snippet("" + position));

            position = position + 1;

            if(position < addressList.size())
                MapsApiCalls.fetchLocationFromAddress(this, getString(R.string.api_key), addressList.get(position));
        }
    }

    @Override
    public void onFailureGeocoding() { }
}
