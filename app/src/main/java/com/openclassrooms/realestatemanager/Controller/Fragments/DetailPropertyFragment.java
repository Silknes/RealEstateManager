package com.openclassrooms.realestatemanager.Controller.Fragments;


import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.openclassrooms.realestatemanager.Injections.Injection;
import com.openclassrooms.realestatemanager.Injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.Model.User;
import com.openclassrooms.realestatemanager.PropertyViewModel;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.Utils;

import java.util.Calendar;

public class DetailPropertyFragment extends Fragment{
    private TextView txtDescription, txtPrice, txtArea, txtNbRoom, txtAddress, txtType, txtStatus, txtPointInterest, txtEntryDate, txtSaleDateTitle, txtSaleDate, txtSaleDateEditMode, txtAgent;
    private EditText editDescription, editPrice, editArea, editNbRoom, editAddress;
    private ViewSwitcher viewSwitcherDescription, viewSwitcherPrice, viewSwitcherArea, viewSwitcherNbRoom, viewSwitcherAddress , viewSwitcherType, viewSwitcherStatus, viewSwitcherPointInterest, viewSwitcherSaleDate;
    private Spinner spinnerType, spinnerStatus;
    private LinearLayout linearPointInterest, linearSaleDate;
    private CheckBox checkBoxSchool, checkBoxShop, checkBoxParc, checkBoxPublicTransport;
    private boolean valueSchool, valueShop, valueParc, valuePublicTransport;

    private String stringSelectedDate;

    private Property property;
    private PropertyViewModel propertyViewModel;

    private long userId;
    private SharedPreferences.Editor editor;

    public DetailPropertyFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_property, container, false);

        viewSwitcherDescription = view.findViewById(R.id.fragment_detail_property_view_switcher_description);
        viewSwitcherPrice = view.findViewById(R.id.fragment_detail_property_view_switcher_price);
        viewSwitcherArea = view.findViewById(R.id.fragment_detail_property_view_switcher_area);
        viewSwitcherNbRoom = view.findViewById(R.id.fragment_detail_property_view_switcher_nb_room);
        viewSwitcherAddress = view.findViewById(R.id.fragment_detail_property_view_switcher_address);
        viewSwitcherType = view.findViewById(R.id.fragment_detail_property_view_switcher_type);
        viewSwitcherStatus = view.findViewById(R.id.fragment_detail_property_view_switcher_status);
        viewSwitcherPointInterest = view.findViewById(R.id.fragment_detail_property_view_switcher_point_interest);
        viewSwitcherSaleDate = view.findViewById(R.id.fragment_detail_property_view_switcher_date_sale);

        txtDescription = view.findViewById(R.id.fragment_detail_property_txt_description);
        txtPrice = view.findViewById(R.id.fragment_detail_property_txt_price);
        txtArea = view.findViewById(R.id.fragment_detail_property_txt_area);
        txtNbRoom = view.findViewById(R.id.fragment_detail_property_txt_nb_room);
        txtAddress = view.findViewById(R.id.fragment_detail_property_txt_address);
        txtType = view.findViewById(R.id.fragment_detail_property_txt_type);
        txtStatus = view.findViewById(R.id.fragment_detail_property_txt_status);
        txtPointInterest = view.findViewById(R.id.fragment_detail_property_txt_point_interest);
        txtEntryDate = view.findViewById(R.id.fragment_detail_property_txt_date_entry);
        txtSaleDateTitle = view.findViewById(R.id.fragment_detail_property_txt_date_sale_title);
        txtSaleDate = view.findViewById(R.id.fragment_detail_property_txt_date_sale);
        txtSaleDateEditMode = view.findViewById(R.id.fragment_detail_property_txt_date_sale_edit_mode);
        txtAgent = view.findViewById(R.id.fragment_detail_property_txt_agent);

        editDescription = view.findViewById(R.id.fragment_detail_property_edit_description);
        editPrice = view.findViewById(R.id.fragment_detail_property_edit_price);
        editArea = view.findViewById(R.id.fragment_detail_property_edit_area);
        editNbRoom = view.findViewById(R.id.fragment_detail_property_edit_nb_room);
        editAddress = view.findViewById(R.id.fragment_detail_property_edit_address);

        spinnerType = view.findViewById(R.id.fragment_detail_property_spinner_type);
        spinnerStatus = view.findViewById(R.id.fragment_detail_property_spinner_status);

        linearPointInterest = view.findViewById(R.id.fragment_detail_property_container_checkbox_point_interest);
        linearSaleDate = view.findViewById(R.id.fragment_detail_property_container_date_sale);

        checkBoxSchool = view.findViewById(R.id.fragment_detail_property_checkbox_school);
        checkBoxShop = view.findViewById(R.id.fragment_detail_property_checkbox_shop);
        checkBoxParc = view.findViewById(R.id.fragment_detail_property_checkbox_parc);
        checkBoxPublicTransport = view.findViewById(R.id.fragment_detail_property_checkbox_public_transport);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MY_SHARED_PREFERENCES", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userId = getContext().getSharedPreferences("MY_SHARED_PREFERENCES", Context.MODE_PRIVATE).getLong("userId", -1);

        property = (Property) getArguments().getSerializable("property");

        this.configureViewModel();
        this.setViewWithPropertyData(property);

        this.setSpinner(R.array.spinner_type, spinnerType);
        this.setSpinner(R.array.spinner_status, spinnerStatus);

        this.addListenerToCheckbox(checkBoxSchool);
        this.addListenerToCheckbox(checkBoxShop);
        this.addListenerToCheckbox(checkBoxParc);
        this.addListenerToCheckbox(checkBoxPublicTransport);

        this.setSaleDateVisibility();
        this.updateSaleDateVisibility();
        linearSaleDate.setOnClickListener(view1 -> configureDatePickerDialog());

        return view;
    }

    private void configureViewModel(){
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(getContext());
        this.propertyViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PropertyViewModel.class);
        this.propertyViewModel.init(userId);
    }

    // Public method call in parent activity to switch between our different according to if we are in edit mode or not
    public void updateDetailProperty(boolean saveChanges){
        updateViewSwitcherForEditTxt(viewSwitcherDescription, txtDescription, editDescription, saveChanges);
        updateViewSwitcherForEditTxt(viewSwitcherPrice, txtPrice, editPrice, saveChanges);
        updateViewSwitcherForEditTxt(viewSwitcherArea, txtArea, editArea, saveChanges);
        updateViewSwitcherForEditTxt(viewSwitcherNbRoom, txtNbRoom, editNbRoom, saveChanges);
        updateViewSwitcherForEditTxt(viewSwitcherAddress, txtAddress, editAddress, saveChanges);
        updateViewSwitcherForSpinner(viewSwitcherType, txtType, spinnerType, saveChanges);
        updateViewSwitcherForSpinner(viewSwitcherStatus, txtStatus, spinnerStatus, saveChanges);
        updateViewSwitcherForCheckbox(viewSwitcherPointInterest, txtPointInterest, linearPointInterest, saveChanges);
        updateViewSwitcherForDate(viewSwitcherSaleDate, txtSaleDate, txtSaleDateEditMode, linearSaleDate, saveChanges);

        if(saveChanges) updateProperty(property);
        this.setSaleDateVisibility();

    }

    // Method that configure viewswitcher for couple textview/edittext
    private void updateViewSwitcherForEditTxt(ViewSwitcher viewSwitcher, TextView textView, EditText editText, boolean saveChanges){
        if(viewSwitcher.getCurrentView() == editText) {
            if(saveChanges) {
                textView.setText(editText.getText());
                switch(editText.getId()){
                    case R.id.fragment_detail_property_edit_description:
                        property.setDescription(editText.getText().toString());
                        break;
                    case R.id.fragment_detail_property_edit_price:
                        property.setPrice(editText.getText().toString());
                        break;
                    case R.id.fragment_detail_property_edit_area:
                        property.setArea(editText.getText().toString());
                        break;
                    case R.id.fragment_detail_property_edit_nb_room:
                        property.setNbRoom(editText.getText().toString());
                        break;
                    case R.id.fragment_detail_property_edit_address:
                        property.setAddress(editText.getText().toString());
                        break;
                }
            }
        }
        else editText.setText(textView.getText());
        viewSwitcher.showNext();
    }

    // Method that configure the view switcher for each spinner
    private void updateViewSwitcherForSpinner(ViewSwitcher viewSwitcher, TextView textView, Spinner spinner, boolean saveChanges){
        if(viewSwitcher.getCurrentView() == spinner) {
            if(saveChanges)
                if(spinner.getSelectedItemPosition() != 0) {
                    textView.setText(spinner.getSelectedItem().toString());
                    switch(spinner.getId()){
                        case R.id.fragment_detail_property_spinner_status :
                            if(spinner.getSelectedItemId() == 1){
                                property.setStatus(1);
                            }
                            else if(spinner.getSelectedItemId() == 2){
                                property.setStatus(2);
                            }
                            break;
                        case R.id.fragment_detail_property_spinner_type :
                            if(spinner.getSelectedItem().equals(getResources().getString(R.string.house)))
                                property.setType(1);
                            else if(spinner.getSelectedItem().equals(getResources().getString(R.string.apartment)))
                                property.setType(2);
                            if(spinner.getSelectedItem().equals(getResources().getString(R.string.loft)))
                                property.setType(3);
                            else if(spinner.getSelectedItem().equals(getResources().getString(R.string.duplex)))
                                property.setType(4);
                            break;
                    }
                }
                else textView.setText(textView.getText().toString());
        }
        viewSwitcher.showNext();
    }

    // Method that configure the ViewSwitcher for checkbox
    private void updateViewSwitcherForCheckbox(ViewSwitcher viewSwitcher, TextView textView, LinearLayout linearLayout, boolean saveChanges){
        if(viewSwitcher.getCurrentView() == linearLayout) {
            if(saveChanges) {
                saveCheckboxState();
                textView.setText(formatPoiStr());
            }
        } else this.setPoiCheckboxState();
        viewSwitcher.showNext();
    }

    // Method that configure viewswitcher for the date
    private void updateViewSwitcherForDate(ViewSwitcher viewSwitcher, TextView textView, TextView textViewEditMode, LinearLayout linearLayout, boolean saveChanges){
        if(viewSwitcher.getCurrentView() == linearLayout) {
            if(saveChanges){
                textView.setText(textViewEditMode.getText());
                property.setSaleDate(textViewEditMode.getText().toString());
            }
        } else textViewEditMode.setText(textView.getText());
        viewSwitcher.showNext();
    }

    // Method that set the different item for each spinner
    private void setSpinner(int arrayId, Spinner spinner){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                arrayId,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    // Method that add a listener for each checkbox and save the current state of the checkbox
    private void addListenerToCheckbox(final CheckBox checkBox){
        checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            switch(compoundButton.getId()){
                case R.id.fragment_detail_property_checkbox_school:
                    valueSchool = isChecked;
                    break;
                case R.id.fragment_detail_property_checkbox_shop:
                    valueShop = isChecked;
                    break;
                case R.id.fragment_detail_property_checkbox_parc:
                    valueParc = isChecked;
                    break;
                case R.id.fragment_detail_property_checkbox_public_transport:
                    valuePublicTransport = isChecked;
                    break;
            }
        });
    }

    // Method that build our string for checkboxes textview
    private String formatPoiStr(){
        String finalStr = "";
        if(property.isCheckboxSchool()) finalStr = finalStr + "- School" + "\n";
        if(property.isCheckboxShop()) finalStr = finalStr + "- Shop" + "\n";
        if(property.isCheckboxParc()) finalStr = finalStr + "- Parc" + "\n";
        if(property.isCheckboxPublicTransport()) finalStr = finalStr + "- Public Transport";
        return finalStr;
    }

    // Method that set the state for each checkbox
    private void setPoiCheckboxState(){
        checkBoxSchool.setChecked(property.isCheckboxSchool());
        checkBoxShop.setChecked(property.isCheckboxShop());
        checkBoxParc.setChecked(property.isCheckboxParc());
        checkBoxPublicTransport.setChecked(property.isCheckboxPublicTransport());
    }

    // Method that save the state for each checkbox
    private void saveCheckboxState(){
        property.setCheckboxSchool(valueSchool);
        property.setCheckboxShop(valueShop);
        property.setCheckboxParc(valueParc);
        property.setCheckboxPublicTransport(valuePublicTransport);
    }

    // Method that update the visibility of the sale date if the property already available
    private void updateSaleDateVisibility(){
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch(position){
                    case 1 :
                        txtSaleDateTitle.setVisibility(View.VISIBLE);
                        viewSwitcherSaleDate.setVisibility(View.VISIBLE);
                        txtSaleDateEditMode.setText(property.getSaleDate());
                        break;
                    case 2 :
                        txtSaleDateTitle.setVisibility(View.GONE);
                        viewSwitcherSaleDate.setVisibility(View.GONE);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void setSaleDateVisibility(){
        if(property.getStatus() == 1){
            txtSaleDateTitle.setVisibility(View.VISIBLE);
            viewSwitcherSaleDate.setVisibility(View.VISIBLE);
            if(property.getSaleDate().isEmpty()) txtSaleDateEditMode.setText(Utils.getTodayDate());
            else txtSaleDateEditMode.setText(property.getSaleDate());
        } else {
            txtSaleDateTitle.setVisibility(View.GONE);
            viewSwitcherSaleDate.setVisibility(View.GONE);
        }
    }

    // Method that configure an alert dialog to pick a date
    private void configureDatePickerDialog(){
        DatePickerDialog.OnDateSetListener onDateSetListener = (datePicker, year, month, day) -> {
            stringSelectedDate = Utils.formatStringDate(year, month, day);
            txtSaleDateEditMode.setText(stringSelectedDate);
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

    /* When user click on a property in PropertyFragment, we pass the property to DetailPropertyFragment
    Then update the view with data get from the property */
    private void setViewWithPropertyData(Property property){
        if(property != null){
            txtPrice.setText(property.getPrice());
            txtAddress.setText(property.getAddress());
            txtArea.setText(property.getArea());
            txtDescription.setText(property.getDescription());
            txtNbRoom.setText(property.getNbRoom());
            txtEntryDate.setText(property.getEntryDate());
            txtSaleDate.setText(property.getSaleDate());
            switch(property.getStatus()){
                case 1 :
                    txtStatus.setText(getResources().getString(R.string.sold));
                    break;
                case 2 :
                    txtStatus.setText(getResources().getString(R.string.available));
                    break;
            }
            switch (property.getType()){
                case 1 :
                    txtType.setText(getResources().getString(R.string.house));
                    break;
                case 2 :
                    txtType.setText(getResources().getString(R.string.apartment));
                    break;
                case 3 :
                    txtType.setText(getResources().getString(R.string.loft));
                    break;
                case 4 :
                    txtType.setText(getResources().getString(R.string.duplex));
                    break;
            }
            txtPointInterest.setText(formatPoiStr());
            this.getPropertyAgent(property.getUserId());
        }
    }

    // Then we update our database with the new data
    private void updateProperty(Property property){
        this.propertyViewModel.updateProperty(property);
    }

    private void getPropertyAgent(long userId){
        this.propertyViewModel.getPropertyAgent(userId).observe(this, this::setTxtAgent);
    }

    private void setTxtAgent(User user){
        txtAgent.setText(user.getUsername());
    }
}
