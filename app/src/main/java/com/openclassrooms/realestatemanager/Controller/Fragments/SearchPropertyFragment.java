package com.openclassrooms.realestatemanager.Controller.Fragments;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.Injections.Injection;
import com.openclassrooms.realestatemanager.Injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.Model.Photo;
import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.View.PropertyViewModel;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.ItemClickSupport;
import com.openclassrooms.realestatemanager.Util.Utils;
import com.openclassrooms.realestatemanager.View.HouseAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class SearchPropertyFragment extends Fragment{
    private Button btnSearch;
    private Spinner spinnerType;
    private LinearLayout formVisibility;
    private TextView minEntryDateTxt, maxEntryDateTxt, minSaleDateTxt, maxSaleDateTxt;
    private int priceMinValue, areaMinValue, nbRoomMinValue;
    private int priceMaxValue = 2000000000, areaMaxValue = 2000000000, nbRoomMaxValue = 2000000000;
    private int choosenType;
    private int entryDateMinValue, saleDateMinValue;
    private int entryDateMaxValue = Utils.formatCurrentDateToInt();
    private int saleDateMaxValue = Utils.formatCurrentDateToInt();
    private String cityValue = "% %";
    private boolean schoolValue, shopValue, parcValue, transportValue;
    private List<Property> propertyList = new ArrayList<>();
    private List<Photo> photoList = new ArrayList<>();
    private PropertyViewModel propertyViewModel;
    private HouseAdapter adapter;
    private RecyclerView recyclerView;

    private OnItemClickedListener mCallback;

    public interface OnItemClickedListener{
        void onItemClicked(Property property);
        void onSearchButtonClicked(List<Property> propertyList, List<Photo> photoList);
    }

    public SearchPropertyFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_property, container, false);

        btnSearch = view.findViewById(R.id.fragment_search_property_btn_search);

        TextInputEditText minPriceView = view.findViewById(R.id.fragment_search_property_input_edit_min_price);
        TextInputEditText maxPriceView = view.findViewById(R.id.fragment_search_property_input_edit_max_price);
        TextInputEditText minAreaView = view.findViewById(R.id.fragment_search_property_input_edit_min_area);
        TextInputEditText maxAreaView = view.findViewById(R.id.fragment_search_property_input_edit_max_area);
        TextInputEditText minNbRoomView = view.findViewById(R.id.fragment_search_property_input_edit_min_nb_room);
        TextInputEditText maxNbRoomView = view.findViewById(R.id.fragment_search_property_input_edit_max_nb_room);
        TextInputEditText cityView = view.findViewById(R.id.fragment_search_property_input_edit_city);

        spinnerType = view.findViewById(R.id.fragment_search_property_spinner_type);

        CheckBox checkBoxSchool = view.findViewById(R.id.fragment_search_property_checkbox_school);
        CheckBox checkBoxShop = view.findViewById(R.id.fragment_search_property_checkbox_shop);
        CheckBox checkBoxParc = view.findViewById(R.id.fragment_search_property_checkbox_parc);
        CheckBox checkBoxPublicTransport = view.findViewById(R.id.fragment_search_property_checkbox_transport);

        formVisibility = view.findViewById(R.id.fragment_search_property_form_visibility);
        RelativeLayout minEntryDateView = view.findViewById(R.id.fragment_search_property_container_min_entry_date);
        RelativeLayout maxEntryDateView = view.findViewById(R.id.fragment_search_property_container_max_entry_date);
        RelativeLayout minSaleDateView = view.findViewById(R.id.fragment_search_property_container_min_sale_date);
        RelativeLayout maxSaleDateView = view.findViewById(R.id.fragment_search_property_container_max_sale_date);

        minEntryDateTxt = view.findViewById(R.id.fragment_search_property_txt_min_entry_date);
        maxEntryDateTxt = view.findViewById(R.id.fragment_search_property_txt_max_entry_date);
        minSaleDateTxt = view.findViewById(R.id.fragment_search_property_txt_min_sale_date);
        maxSaleDateTxt = view.findViewById(R.id.fragment_search_property_txt_max_sale_date);

        recyclerView = view.findViewById(R.id.fragment_search_property_recycler_view);

        this.configureViewModel();
        this.configureRecyclerView();
        this.configureOnClickRecyclerView();

        this.addTextWatcher(minPriceView);
        this.addTextWatcher(maxPriceView);
        this.addTextWatcher(minAreaView);
        this.addTextWatcher(maxAreaView);
        this.addTextWatcher(minNbRoomView);
        this.addTextWatcher(maxNbRoomView);
        this.addTextWatcher(cityView);

        this.setSpinnerType();

        this.setCheckboxListener(checkBoxSchool);
        this.setCheckboxListener(checkBoxShop);
        this.setCheckboxListener(checkBoxParc);
        this.setCheckboxListener(checkBoxPublicTransport);

        this.setOnClickListenerOnDateView(minEntryDateView);
        this.setOnClickListenerOnDateView(maxEntryDateView);
        this.setOnClickListenerOnDateView(minSaleDateView);
        this.setOnClickListenerOnDateView(maxSaleDateView);

        btnSearch.setOnClickListener(v -> this.sendRequestToDB());

        return view;
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
        mCallback = (OnItemClickedListener) getActivity();
    }

    /*******************************
    **** Configure recyclerView ****
    *******************************/

    private void configureRecyclerView(){
        this.adapter = new HouseAdapter(this.propertyList, this.photoList, Glide.with(this));
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_property_item)
                .setOnItemClickListener((recyclerView, position, v) -> mCallback.onItemClicked(adapter.getProperty(position)));
    }

    /*************************************************
    **** Configure access to DB and request to it ****
    *************************************************/

    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(getContext());
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
    }

    // Method that request all properties that match with values
    private void sendRequestToDB(){
        this.propertyViewModel.getRequestProperty(priceMinValue, priceMaxValue, areaMinValue,
                areaMaxValue, nbRoomMinValue, nbRoomMaxValue, schoolValue, shopValue, parcValue,
                transportValue, choosenType, entryDateMinValue, entryDateMaxValue, saleDateMinValue,
                saleDateMaxValue, cityValue).observe(this, this::updateRecyclerView);
    }

    // Method that update the recycler and display it if our request get at least one property
    private void updateRecyclerView(List<Property> propertyList){
        this.propertyList = propertyList;
        if(!this.propertyList.isEmpty()){
            this.askForPropertyMainPhoto();
        } else Toast.makeText(getContext(), getString(R.string.search_property_no_property_found), Toast.LENGTH_SHORT).show();
    }

    // Asking for the main photo for each property
    private void askForPropertyMainPhoto(){
        this.propertyViewModel.getMainPhotos().observe(this, this::setPhotoList);
    }

    // Update the recycler view with data getting from DB
    private void setPhotoList(List<Photo> photoList){
        this.photoList = photoList;

        if(isLandscape()) mCallback.onSearchButtonClicked(this.propertyList, this.photoList);
        else {
            this.displayRecyclerView();
            adapter.updateData(this.propertyList, this.photoList);
        }
    }

    /***********************************************
    **** Add methods to views to get user input ****
    ***********************************************/

    // Add a text watcher for each textinputedittext
    private void addTextWatcher(TextInputEditText inputValueView){
        inputValueView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {
                setInputValue(inputValueView, str.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Update variables according to textinputedittext id
    private void setInputValue(TextInputEditText inputValueView, String value){
        if(value.length() != 0) {
            switch(inputValueView.getId()){
                case R.id.fragment_search_property_input_edit_min_price:
                    priceMinValue = Integer.parseInt(value);
                    break;
                case R.id.fragment_search_property_input_edit_max_price:
                    priceMaxValue = Integer.parseInt(value);
                    break;
                case R.id.fragment_search_property_input_edit_min_area:
                    areaMinValue = Integer.parseInt(value);
                    break;
                case R.id.fragment_search_property_input_edit_max_area:
                    areaMaxValue = Integer.parseInt(value);
                    break;
                case R.id.fragment_search_property_input_edit_min_nb_room:
                    nbRoomMinValue = Integer.parseInt(value);
                    break;
                case R.id.fragment_search_property_input_edit_max_nb_room:
                    nbRoomMaxValue = Integer.parseInt(value);
                    break;
                case R.id.fragment_search_property_input_edit_city:
                    cityValue = "%" + value + "%";
                    break;
            }
        }
    }

    // Bind the spinner
    private void setSpinnerType(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),
                R.array.spinner_type,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        this.setOnItemSelectedListener(spinnerType);
    }

    // Add a listener to the spinner
    private void setOnItemSelectedListener(Spinner operatorSpinner){
        operatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                choosenType = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Add a listener on each checkbox
    private void setCheckboxListener(CheckBox checkBox){
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateCheckBoxStateValue(checkBox));
    }

    // Update variables according to checkbox id
    private void updateCheckBoxStateValue(CheckBox checkBox){
        switch(checkBox.getId()){
            case R.id.fragment_search_property_checkbox_school:
                schoolValue = checkBox.isChecked();
                break;
            case R.id.fragment_search_property_checkbox_shop:
                shopValue = checkBox.isChecked();
                break;
            case R.id.fragment_search_property_checkbox_parc:
                parcValue = checkBox.isChecked();
                break;
            case R.id.fragment_search_property_checkbox_transport:
                transportValue = checkBox.isChecked();
                break;
        }
        checkBox.setChecked(checkBox.isChecked());
    }

    // Add a listener for each relative layout that manage date
    private void setOnClickListenerOnDateView(RelativeLayout dateView){
        dateView.setOnClickListener(v -> configureDatePickerDialog(dateView));
    }

    // Add a date picker dialog for each relative layout that manage date
    private void configureDatePickerDialog(RelativeLayout dateView){
        DatePickerDialog.OnDateSetListener onDateSetListener = (datePicker, year, month, day) -> this.actionForEachDatePickerDialog(dateView, year, month, day);

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                Objects.requireNonNull(getContext()),
                onDateSetListener,
                year, month, day);
        dialog.show();
    }

    // Configure which variables to update according to relative layout id
    private void actionForEachDatePickerDialog(RelativeLayout dateView, int year, int month, int day){
        switch (dateView.getId()){
            case R.id.fragment_search_property_container_min_entry_date:
                entryDateMinValue = Utils.formatIntDate(year, month, day);
                minEntryDateTxt.setText(Utils.formatStringDate(year, month, day));
                break;
            case R.id.fragment_search_property_container_max_entry_date:
                entryDateMaxValue = Utils.formatIntDate(year, month, day);
                maxEntryDateTxt.setText(Utils.formatStringDate(year, month, day));
                break;
            case R.id.fragment_search_property_container_min_sale_date:
                saleDateMinValue = Utils.formatIntDate(year, month, day);
                minSaleDateTxt.setText(Utils.formatStringDate(year, month, day));
                break;
            case R.id.fragment_search_property_container_max_sale_date:
                saleDateMaxValue = Utils.formatIntDate(year, month, day);
                maxSaleDateTxt.setText(Utils.formatStringDate(year, month, day));
                break;
        }
    }

    /**********************
    **** Other Methods ****
    **********************/

    // Method that display the recyclerview and mask the form
    private void displayRecyclerView(){
        recyclerView.setVisibility(View.VISIBLE);

        formVisibility.setVisibility(View.GONE);
        btnSearch.setVisibility(View.GONE);
    }

    // Get a value that define if phone is in portrait or not
    private boolean isLandscape(){
        return getResources().getBoolean(R.bool.is_landscape);
    }
}
