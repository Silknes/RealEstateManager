package com.openclassrooms.realestatemanager.Controller.Fragments;


import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.Controller.Activities.FullScreenActivity;
import com.openclassrooms.realestatemanager.Injections.Injection;
import com.openclassrooms.realestatemanager.Injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.Model.GeocodingApi.ApiResult;
import com.openclassrooms.realestatemanager.Model.Photo;
import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.Model.User;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.ItemClickSupport;
import com.openclassrooms.realestatemanager.Util.MapsApiCalls;
import com.openclassrooms.realestatemanager.Util.Utils;
import com.openclassrooms.realestatemanager.View.PhotoAdapter;
import com.openclassrooms.realestatemanager.View.PropertyViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailPropertyFragment extends Fragment implements MapsApiCalls.CallbacksGeocoding{
    private TextView priceView, areaView, typeView, statusView, nbRoomView, descriptionView, addressView, cityView, nbHouseView, postalCodeView, poiView, saleDateView, entryDateView, textAgent;
    private LinearLayout saleDateContainer;
    private ImageView staticMap;

    private Property property;
    private PropertyViewModel propertyViewModel;

    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private List<Photo> photoList;

    private OnButtonClickedListener callback;

    public interface OnButtonClickedListener{
        void onMortgageSimulatorButtonClicked(double price);
    }

    public DetailPropertyFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_property, container, false);

        priceView = view.findViewById(R.id.fragment_detail_property_txt_price);
        areaView = view.findViewById(R.id.fragment_detail_property_txt_area);
        typeView = view.findViewById(R.id.fragment_detail_property_txt_type);
        statusView = view.findViewById(R.id.fragment_detail_property_txt_status);
        nbRoomView = view.findViewById(R.id.fragment_detail_property_txt_nb_room);
        descriptionView = view.findViewById(R.id.fragment_detail_property_txt_description);
        addressView = view.findViewById(R.id.fragment_detail_property_txt_address);
        cityView = view.findViewById(R.id.fragment_detail_property_txt_city);
        nbHouseView = view.findViewById(R.id.fragment_detail_property_txt_house_number);
        postalCodeView = view.findViewById(R.id.fragment_detail_property_txt_postal_code);
        poiView = view.findViewById(R.id.fragment_detail_property_txt_poi);
        saleDateView = view.findViewById(R.id.fragment_detail_property_txt_sale_date);
        entryDateView = view.findViewById(R.id.fragment_detail_property_txt_entry_date);
        textAgent = view.findViewById(R.id.fragment_detail_property_txt_agent);

        saleDateContainer = view.findViewById(R.id.fragment_detail_property_container_sale_date);
        LinearLayout linearStaticMap = view.findViewById(R.id.fragment_detail_property_container_static_map);

        Button mortgageBtn = view.findViewById(R.id.fragment_detail_property_btn_mortgage);

        recyclerView = view.findViewById(R.id.fragment_detail_property_recycler_view);

        staticMap = view.findViewById(R.id.fragment_detail_property_static_map);

        property = (Property) (getArguments() != null ? getArguments().getSerializable("property") : null);

        this.configureViewModel();
        this.configureRecyclerView();
        this.configureOnClickRecyclerView();

        this.getPhotos();

        this.updateViewWithPropertyData();

        if(Utils.isInternetAvailable(Objects.requireNonNull(getContext()))) {
            this.fetchLocationFromAddress();
            linearStaticMap.setVisibility(View.VISIBLE);
        }

        mortgageBtn.setOnClickListener(v -> {
            double price;
            if(priceView.getText().toString().contains("$")) price = Double.parseDouble(priceView.getText().toString().replace("$", ""));
            else price = Double.parseDouble(priceView.getText().toString());
            callback.onMortgageSimulatorButtonClicked(price);
        });

        return view;
    }

    /**********************************************
     **** Configure callback to parent activity ****
     **********************************************/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.createCallbackToParentActivity();
    }

    private void createCallbackToParentActivity(){
        callback = (OnButtonClickedListener) getActivity();
    }

    /******************
     **** Manage DB ****
     ******************/

    private void configureViewModel(){
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(getContext());
        this.propertyViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PropertyViewModel.class);
    }

    private void getAgent(){
        this.propertyViewModel.getPropertyAgent(property.getUserId()).observe(this, this::setTextAgent);
    }

    // Get all photos for this property
    private void getPhotos(){
        this.propertyViewModel.getPhotosProperty(property.getId()).observe(this, this::updateData);
    }

    // Update the recycler view with the photos get from the DB
    private void updateData(List<Photo> photoList){
        this.photoList = photoList;
        adapter.updateData(photoList);
    }

    /*******************************
     **** Configure recyclerView ****
     *******************************/

    private void configureRecyclerView(){
        this.photoList = new ArrayList<>();
        this.adapter = new PhotoAdapter(photoList, Glide.with(this));
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_property_item)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    Intent intent = new Intent(getContext(), FullScreenActivity.class);
                    intent.putExtra("uri", adapter.getPhoto(position).getUriPhoto());
                    intent.putExtra("description", adapter.getPhoto(position).getDescription());
                    startActivity(intent);
                });
    }

    /****************************************
     **** Set default value for each view ****
     ****************************************/

    // Method that update views with data get from the property
    @SuppressLint("SetTextI18n")
    private void updateViewWithPropertyData(){
        this.priceView.setText(property.getPrice() + "$");
        this.areaView.setText(property.getArea() + "m²");
        this.nbRoomView.setText("" + property.getNbRoom());
        this.descriptionView.setText(property.getDescription());
        this.addressView.setText(property.getAddress());
        this.cityView.setText(property.getCity());
        this.nbHouseView.setText("" + property.getHouseNumber());
        this.postalCodeView.setText("" + property.getPostalCode());
        this.poiView.setText(this.setPoiTxt());
        this.saleDateView.setText(Utils.getTodayDate());
        this.entryDateView.setText(Utils.formatIntDateToString(property.getEntryDate()));

        if(property.getType() == 1) typeView.setText(getString(R.string.house));
        else if(property.getType() == 2) typeView.setText(getString(R.string.apartment));
        else if(property.getType() == 3) typeView.setText(getString(R.string.loft));
        else if(property.getType() == 4) typeView.setText(getString(R.string.duplex));
        else typeView.setText("Error");

        if(property.getStatus() == 1) statusView.setText(getString(R.string.sold));
        else statusView.setText(getString(R.string.available));

        this.getAgent();
        this.setSaleDateVisibility();
    }

    private void setTextAgent(User user){
        textAgent.setText(Utils.uppercaseFirstLetter(user.getUsername()));
    }

    private String setPoiTxt(){
        String str = "";
        if(property.isCheckboxSchool() || property.isCheckboxShop() || property.isCheckboxPublicTransport() || property.isCheckboxParc()){
            if(property.isCheckboxSchool()) str = str + "- School\n";
            if(property.isCheckboxShop()) str = str + "- Shop\n";
            if(property.isCheckboxParc()) str = str + "- Parc\n";
            if(property.isCheckboxPublicTransport()) str = str + "- Public transport\n";
        } else str = "No point of interest";
        return str;
    }

    private void setSaleDateVisibility(){
        if(property.getType() == 1) saleDateContainer.setVisibility(View.VISIBLE);
    }

    /*************************
     **** Add a static map ****
     *************************/

    // Fetch the location for this property and add a static map
    private void fetchLocationFromAddress(){
        MapsApiCalls.fetchLocationFromAddress(this, getString(R.string.api_key), getPropertyAddress());
    }

    // Return a format address used to fetch the location of the property
    private String getPropertyAddress(){
        return property.getHouseNumber() + property.getAddress() + "," + property.getCity() + "," + property.getPostalCode();
    }

    // When getting the result, add a static map with a marker at the correct location
    @Override
    public void onResponseGeocoding(@Nullable ApiResult apiResult) {
        if(apiResult != null){
            String location = apiResult.getResults().get(0).getGeometry().getLocation().getLat() + "," +
                    apiResult.getResults().get(0).getGeometry().getLocation().getLng() + "&";

            String center = "center=" + location;
            String size = "size=200x200&";
            String zoom = "zoom=12&";
            String marker = "markers=color:blue%7C" + location;
            String key = "key=" + getString(R.string.api_key);

            Uri uri = Uri.parse("https://maps.googleapis.com/maps/api/staticmap?" + center + size + zoom + marker + key);
            Glide.with(this).load(uri).into(staticMap);
        }
    }

    @Override
    public void onFailureGeocoding() {}
}
