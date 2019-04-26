package com.openclassrooms.realestatemanager.Controller.Fragments;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.Controller.Activities.DetailPropertyActivity;
import com.openclassrooms.realestatemanager.Injections.Injection;
import com.openclassrooms.realestatemanager.Injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.Model.Photo;
import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.PropertyViewModel;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.ItemClickSupport;
import com.openclassrooms.realestatemanager.Util.Utils;
import com.openclassrooms.realestatemanager.View.HouseAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SearchPropertyFragment extends Fragment{
    private Button btnSearch;
    private EditText minPriceView, maxPriceView, minAreaView, maxAreaView , minNbRoomView, maxNbRoomView, cityView;
    private CheckBox checkBoxSchool, checkBoxShop, checkBoxParc, checkBoxPublicTransport;
    private Spinner spinnerType;
    private LinearLayout formVisibility, minEntryDateView, maxEntryDateView, minSaleDateView, maxSaleDateView;
    private TextView minEntryDateTxt, maxEntryDateTxt, minSaleDateTxt, maxSaleDateTxt;
    private int priceMinValue, areaMinValue, nbRoomMinValue;
    private int priceMaxValue = 2000000000, areaMaxValue = 2000000000, nbRoomMaxValue = 2000000000;
    private int choosenType;
    private int entryDateOperator, saleDateOperator;
    private int entryDateMinValue, saleDateMinValue;
    private int entryDateMaxValue = Utils.formatStringDateToInt(Utils.getTodayDate());
    private int saleDateMaxValue = Utils.formatStringDateToInt(Utils.getTodayDate());
    private String cityValue;
    private boolean schoolValue, shopValue, parcValue, transportValue;
    private List<Property> propertyList = new ArrayList<>();
    private List<Photo> photoList = new ArrayList<>();
    private PropertyViewModel propertyViewModel;
    private HouseAdapter adapter;
    private RecyclerView recyclerView;

    public SearchPropertyFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_property, container, false);

        btnSearch = view.findViewById(R.id.fragment_search_property_btn_search);

        minPriceView = view.findViewById(R.id.fragment_search_property_min_price);
        maxPriceView = view.findViewById(R.id.fragment_search_property_max_price);
        minAreaView = view.findViewById(R.id.fragment_search_property_min_area);
        maxAreaView = view.findViewById(R.id.fragment_search_property_max_area);
        minNbRoomView = view.findViewById(R.id.fragment_search_property_min_nbroom);
        maxNbRoomView = view.findViewById(R.id.fragment_search_property_max_nbroom);
        cityView = view.findViewById(R.id.fragment_search_property_city);

        Spinner priceOperatorView = view.findViewById(R.id.fragment_search_property_price_operator);
        Spinner areaOperatorView = view.findViewById(R.id.fragment_search_property_area_operator);
        Spinner nbRoomOperatorView = view.findViewById(R.id.fragment_search_property_nbroom_operator);
        Spinner entryDateOperatorView = view.findViewById(R.id.fragment_search_property_entry_date_operator);
        Spinner saleDateOperatorView = view.findViewById(R.id.fragment_search_property_sale_date_operator);
        spinnerType = view.findViewById(R.id.fragment_search_property_spinner_type);

        checkBoxSchool = view.findViewById(R.id.fragment_search_property_checkbox_school);
        checkBoxShop = view.findViewById(R.id.fragment_search_property_checkbox_shop);
        checkBoxParc = view.findViewById(R.id.fragment_search_property_checkbox_parc);
        checkBoxPublicTransport = view.findViewById(R.id.fragment_search_property_checkbox_transport);

        formVisibility = view.findViewById(R.id.fragment_search_property_form_visibility);
        minEntryDateView = view.findViewById(R.id.fragment_search_property_choose_min_entry_date);
        maxEntryDateView = view.findViewById(R.id.fragment_search_property_choose_max_entry_date);
        minSaleDateView = view.findViewById(R.id.fragment_search_property_choose_min_sale_date);
        maxSaleDateView = view.findViewById(R.id.fragment_search_property_choose_max_sale_date);

        minEntryDateTxt = view.findViewById(R.id.fragment_search_property_min_entry_date);
        maxEntryDateTxt = view.findViewById(R.id.fragment_search_property_max_entry_date);
        minSaleDateTxt = view.findViewById(R.id.fragment_search_property_min_sale_date);
        maxSaleDateTxt = view.findViewById(R.id.fragment_search_property_max_sale_date);

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

        this.setSpinner(priceOperatorView);
        this.setSpinner(areaOperatorView);
        this.setSpinner(nbRoomOperatorView);
        this.setSpinner(entryDateOperatorView);
        this.setSpinner(saleDateOperatorView);
        this.setSpinnerType();

        this.setCheckboxListener(checkBoxSchool);
        this.setCheckboxListener(checkBoxShop);
        this.setCheckboxListener(checkBoxParc);
        this.setCheckboxListener(checkBoxPublicTransport);

        this.setOnClickListenerOnDateView(minEntryDateView);
        this.setOnClickListenerOnDateView(maxEntryDateView);
        this.setOnClickListenerOnDateView(minSaleDateView);
        this.setOnClickListenerOnDateView(maxSaleDateView);

        btnSearch.setOnClickListener(v -> {
            this.sendRequestToDB();
        });

        return view;
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
                .setOnItemClickListener((recyclerView, position, v) -> {
            Intent intent = new Intent(getActivity(), DetailPropertyActivity.class);
            intent.putExtra("property", adapter.getProperty(position));
            startActivity(intent);
        });
    }

    /*************************************************
    **** Configure access to DB and request to it ****
    *************************************************/

    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(getContext());
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
    }

    private void sendRequestToDB(){
        this.propertyViewModel.getRequestProperty(priceMinValue, priceMaxValue,
                areaMinValue, areaMaxValue,
                nbRoomMinValue, nbRoomMaxValue,
                schoolValue, shopValue,
                parcValue, transportValue,
                choosenType, entryDateMinValue,
                entryDateMaxValue, saleDateMinValue,
                saleDateMaxValue)
                .observe(this, this::updateRecyclerView);
    }

    private void updateRecyclerView(List<Property> propertyList){
        this.propertyList = propertyList;
        if(isListEmpty(this.propertyList)){
            this.askForPropertyMainPhoto();
            this.displayRecyclerView();
        } else Toast.makeText(getContext(), "No property found", Toast.LENGTH_SHORT).show();
    }

    private void askForPropertyMainPhoto(){
        this.propertyViewModel.getMainPhotos().observe(this, this::setPhotoList);
    }

    private void setPhotoList(List<Photo> photoList){
        this.photoList = photoList;
        adapter.updateData(this.propertyList, this.photoList);
    }

    private boolean isListEmpty(List<Property> propertyList){
        return !propertyList.isEmpty();
    }

    /***********************************************
    **** Add methods to views to get user input ****
    ***********************************************/

    private void addTextWatcher(EditText inputValueView){
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

    private void setInputValue(EditText inputValueView, String value){
        if(value.length() != 0) {
            switch(inputValueView.getId()){
                case R.id.fragment_search_property_min_price:
                    priceMinValue = Integer.parseInt(value);
                    break;
                case R.id.fragment_search_property_max_price:
                    priceMaxValue = Integer.parseInt(value);
                    break;
                case R.id.fragment_search_property_min_area:
                    areaMinValue = Integer.parseInt(value);
                    break;
                case R.id.fragment_search_property_max_area:
                    areaMaxValue = Integer.parseInt(value);
                    break;
                case R.id.fragment_search_property_min_nbroom:
                    nbRoomMinValue = Integer.parseInt(value);
                    break;
                case R.id.fragment_search_property_max_nbroom:
                    nbRoomMaxValue = Integer.parseInt(value);
                    break;
                case R.id.fragment_search_property_city:
                    cityValue = value;
                    break;
            }
        }
    }

    private void setSpinner(Spinner operatorSpinner){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.spinner_operator,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        operatorSpinner.setAdapter(adapter);

        this.setOnItemSelectedListener(operatorSpinner);
    }

    private void setSpinnerType(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.spinner_type,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        this.setOnItemSelectedListener(spinnerType);
    }

    private void setOnItemSelectedListener(Spinner operatorSpinner){
        operatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                displayInputView(parent, position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void displayInputView(AdapterView<?> spinner, int position){
        switch(spinner.getId()){
            case R.id.fragment_search_property_price_operator:
                this.setVisibility(minPriceView, maxPriceView, position);
                break;
            case R.id.fragment_search_property_area_operator:
                this.setVisibility(minAreaView, maxAreaView, position);
                break;
            case R.id.fragment_search_property_nbroom_operator:
                this.setVisibility(minNbRoomView, maxNbRoomView, position);
                break;
            case R.id.fragment_search_property_entry_date_operator:
                entryDateOperator = position;
                this.setVisibility(minEntryDateView, maxEntryDateView, position);
                break;
            case R.id.fragment_search_property_sale_date_operator:
                saleDateOperator = position;
                this.setVisibility(minSaleDateView, maxSaleDateView, position);
                break;
            case R.id.fragment_search_property_spinner_type:
                choosenType = position;
                break;
        }
    }

    private void setVisibility(View minValue, View maxValue, int position){
        if(position >= 0 && position <= 1){
            minValue.setVisibility(View.VISIBLE);
            if(position == 0) maxValue.setVisibility(View.GONE);
            else maxValue.setVisibility(View.VISIBLE);
        } else {
            minValue.setVisibility(View.GONE);
            maxValue.setVisibility(View.VISIBLE);
        }
    }

    private void setCheckboxListener(CheckBox checkBox){
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateCheckBoxStateValue(checkBox));
    }

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

    private void setOnClickListenerOnDateView(LinearLayout dateView){
        dateView.setOnClickListener(v -> configureDatePickerDialog(dateView));
    }

    private void configureDatePickerDialog(LinearLayout dateView){
        DatePickerDialog.OnDateSetListener onDateSetListener = (datePicker, year, month, day) -> {
            switch (dateView.getId()){
                case R.id.fragment_search_property_choose_min_entry_date:
                    entryDateMinValue = Utils.formatIntDate(year, month, day);
                    minEntryDateTxt.setText(Utils.formatStringDate(year, month, day));
                    break;
                case R.id.fragment_search_property_choose_max_entry_date:
                    entryDateMaxValue = Utils.formatIntDate(year, month, day);
                    maxEntryDateTxt.setText(Utils.formatStringDate(year, month, day));
                    break;
                case R.id.fragment_search_property_choose_min_sale_date:
                    saleDateMinValue = Utils.formatIntDate(year, month, day);
                    minSaleDateTxt.setText(Utils.formatStringDate(year, month, day));
                    break;
                case R.id.fragment_search_property_choose_max_sale_date:
                    saleDateMaxValue = Utils.formatIntDate(year, month, day);
                    maxSaleDateTxt.setText(Utils.formatStringDate(year, month, day));
                    break;
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                getContext(),
                onDateSetListener,
                year, month, day);
        dialog.show();
    }

    private void displayRecyclerView(){
        recyclerView.setVisibility(View.VISIBLE);

        formVisibility.setVisibility(View.GONE);
        btnSearch.setVisibility(View.GONE);
    }
}
