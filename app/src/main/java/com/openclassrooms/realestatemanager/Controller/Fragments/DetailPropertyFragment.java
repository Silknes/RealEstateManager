package com.openclassrooms.realestatemanager.Controller.Fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.Utils;

import java.util.ArrayList;
import java.util.Calendar;

public class DetailPropertyFragment extends Fragment{
    private TextView txtDescription, txtPrice, txtArea, txtNbRoom, txtAddress, txtType, txtStatus, txtPointInterest, txtEntryDate, txtSaleDateTitle, txtSaleDate, txtSaleDateEditMode;
    private EditText editDescription, editPrice, editArea, editNbRoom, editAddress;
    private ViewSwitcher viewSwitcherDescription, viewSwitcherPrice, viewSwitcherArea, viewSwitcherNbRoom, viewSwitcherAddress , viewSwitcherType, viewSwitcherStatus, viewSwitcherPointInterest, viewSwitcherSaleDate;
    private Spinner spinnerType, spinnerStatus;
    private LinearLayout linearPointInterest, linearSaleDate;
    private CheckBox checkBoxSchool, checkBoxShop, checkBoxParc, checkBoxPublicTransport;

    private String valueAddress, valueDescription, valueType, valueStatus, valueEntryDate, valueSaleDate, stringSelectedDate;
    private double valuePrice;
    private int valueArea, valueNbRoom;
    private boolean boolCheckBoxSchool, boolCheckBoxShop, boolCheckBoxParc, boolCheckBoxPublicTransport;

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

        this.getDataFromDatabase();
        this.updateViewWithData();

        this.setSpinner(R.array.spinner_type, spinnerType);
        this.setSpinner(R.array.spinner_status, spinnerStatus);

        this.setPointOfInterestString();
        this.addListenerToCheckbox(checkBoxSchool);
        this.addListenerToCheckbox(checkBoxShop);
        this.addListenerToCheckbox(checkBoxParc);
        this.addListenerToCheckbox(checkBoxPublicTransport);

        this.setSaleDateVisibility();
        linearSaleDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configureDatePickerDialog();
            }
        });

        return view;
    }

    // Public method call in parent activity to switch between our different according to if we are in edit mode or not
    public void updateDetailProperty(boolean saveChanges){
        updateViewSwitcher(viewSwitcherDescription, txtDescription, editDescription, saveChanges);
        updateViewSwitcher(viewSwitcherPrice, txtPrice, editPrice, saveChanges);
        updateViewSwitcher(viewSwitcherArea, txtArea, editArea, saveChanges);
        updateViewSwitcher(viewSwitcherNbRoom, txtNbRoom, editNbRoom, saveChanges);
        updateViewSwitcher(viewSwitcherAddress, txtAddress, editAddress, saveChanges);
        updateViewSwitcherForSpinner(viewSwitcherType, txtType, spinnerType, saveChanges);
        updateViewSwitcherForSpinner(viewSwitcherStatus, txtStatus, spinnerStatus, saveChanges);
        updateViewSwitcherForCheckbox(viewSwitcherPointInterest, txtPointInterest, linearPointInterest, saveChanges);
        updateViewSwitcherForDate(viewSwitcherSaleDate, txtSaleDate, txtSaleDateEditMode, linearSaleDate, saveChanges);
        this.setSaleDateVisibility();
    }

    // Method that configure viewswitcher for couple textview/edittext
    private void updateViewSwitcher(ViewSwitcher viewSwitcher, TextView textView, EditText editText, boolean saveChanges){
        if(viewSwitcher.getCurrentView() == editText) {
            if(saveChanges) textView.setText(editText.getText());
            viewSwitcher.showNext();
        }
        else {
            editText.setText(textView.getText());
            viewSwitcher.showNext();
        }
    }

    // Method that configure the view switcher for each spinner
    private void updateViewSwitcherForSpinner(ViewSwitcher viewSwitcher, TextView textView, Spinner spinner, boolean saveChanges){
        if(viewSwitcher.getCurrentView() == spinner) {
            String lastItem = textView.getText().toString();
            if(saveChanges)
                if(spinner.getSelectedItemPosition() != 0) textView.setText(spinner.getSelectedItem().toString());
                else textView.setText(lastItem);
            viewSwitcher.showNext();
        }
        else {
            viewSwitcher.showNext();
        }
    }

    // Method that set the different item for each spinner
    private void setSpinner(int arrayId, Spinner spinner){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                arrayId,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    // Method that configure the ViewSwitcher for checkbox
    private void updateViewSwitcherForCheckbox(ViewSwitcher viewSwitcher, TextView textView, LinearLayout linearLayout, boolean saveChanges){
        if(viewSwitcher.getCurrentView() == linearLayout) {
            if(saveChanges){
                textView.setText(setPointOfInterestString());
            }
            viewSwitcher.showNext();
        }
        else {
            this.setPointOfInterestCheckbox();
            viewSwitcher.showNext();
        }
    }

    // Method that add a listener for each checkbox and save the current state of the checkbox
    private void addListenerToCheckbox(final CheckBox checkBox){
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                switch(compoundButton.getId()){
                    case R.id.fragment_detail_property_checkbox_school:
                        if(isChecked) boolCheckBoxSchool = true;
                        else boolCheckBoxSchool = false;
                        break;
                    case R.id.fragment_detail_property_checkbox_shop:
                        if(isChecked) boolCheckBoxShop = true;
                        else boolCheckBoxShop = false;
                        break;
                    case R.id.fragment_detail_property_checkbox_parc:
                        if(isChecked) boolCheckBoxParc = true;
                        else boolCheckBoxParc = false;
                        break;
                    case R.id.fragment_detail_property_checkbox_public_transport:
                        if(isChecked) boolCheckBoxPublicTransport = true;
                        else boolCheckBoxPublicTransport = false;
                        break;
                }
            }
        });
    }

    // Method that build our string according to the values get from the database
    private String setPointOfInterestString(){
        String finalStr = "";
        if(boolCheckBoxSchool) finalStr = finalStr + "- School" + "\n";
        if(boolCheckBoxShop) finalStr = finalStr + "- Shop" + "\n";
        if(boolCheckBoxParc) finalStr = finalStr + "- Parc" + "\n";
        if(boolCheckBoxPublicTransport) finalStr = finalStr + "- Public Transport";
        return finalStr;
    }

    // Method that check or not our checkbox according to the values get from the database
    private void setPointOfInterestCheckbox(){
        checkBoxSchool.setChecked(boolCheckBoxSchool);
        checkBoxShop.setChecked(boolCheckBoxShop);
        checkBoxParc.setChecked(boolCheckBoxParc);
        checkBoxPublicTransport.setChecked(boolCheckBoxPublicTransport);
    }

    // Method that configure viewswitcher for the date
    private void updateViewSwitcherForDate(ViewSwitcher viewSwitcher, TextView textView, TextView textViewEditMode, LinearLayout linearLayout, boolean saveChanges){
        if(viewSwitcher.getCurrentView() == linearLayout) {
            if(saveChanges){
                textView.setText(textViewEditMode.getText());
            }
            viewSwitcher.showNext();
        }
        else {
            textViewEditMode.setText(textView.getText());
            viewSwitcher.showNext();
        }
    }

    // Method that update the visibility of the sale date if the property already available
    private void setSaleDateVisibility(){
        if(valueStatus.equals("Available")) {
            txtSaleDateTitle.setVisibility(View.GONE);
            viewSwitcherSaleDate.setVisibility(View.GONE);
        }
        else {
            txtSaleDateTitle.setVisibility(View.VISIBLE);
            viewSwitcherSaleDate.setVisibility(View.VISIBLE);
        }
    }

    // Method that configure an alert dialog to pick a date
    private void configureDatePickerDialog(){
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                stringSelectedDate = Utils.formatStringDate(year, month, day);
                txtSaleDateEditMode.setText(stringSelectedDate);
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

    // Example of data get from database
    private void getDataFromDatabase(){
        valuePrice =  253242.25;
        valueAddress = "28 avenue de la ferme aux poules, 59684 Paris";
        valueArea = 158;
        valueDescription = "Une description bien faites du bien est primordiale pour la vente de ce dernier.";
        valueNbRoom = 8;
        valueStatus = "Sold";
        valueType = "House";
        boolCheckBoxSchool = false;
        boolCheckBoxShop = true;
        boolCheckBoxParc = true;
        boolCheckBoxPublicTransport = false;
        valueEntryDate = "20/02/2018";
        valueSaleDate = "12/03/2019";
    }

    // Now we update the view with the data get from db
    private void updateViewWithData(){
        txtPrice.setText("" + valuePrice);
        txtStatus.setText(valueStatus);
        txtType.setText(valueType);
        txtAddress.setText(valueAddress);
        txtNbRoom.setText("" + valueNbRoom);
        txtArea.setText("" + valueArea);
        txtDescription.setText(valueDescription);
        txtEntryDate.setText(valueEntryDate);
        txtSaleDate.setText(valueSaleDate);
        this.setPointOfInterestCheckbox();
    }

    // Then we update our database with the new data
    private void updateDatabase(){
        // A remplir plus tard !
    }
}
