package com.openclassrooms.realestatemanager.Controller.Fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddPropertyFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {
    private Spinner typeSpinner;
    private EditText editTextPrice, editTextArea, editTextNbRoom, editTextAddress, editTextDescription;
    private boolean isEditPrice, isEditArea, isEditNbRoom, isEditAddress, isEditDescription, isDateSelected;
    private Button btnSubmit;
    private int idSpinnerType;
    private LinearLayout linearEntryDate;
    private TextView txtEntryDate;
    private String stringSelectedDate;

    public AddPropertyFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_property, container, false);

        typeSpinner = view.findViewById(R.id.fragment_add_property_spinner_type);

        editTextPrice = view.findViewById(R.id.fragment_add_property_edit_txt_price);
        editTextArea = view.findViewById(R.id.fragment_add_property_edit_txt_area);
        editTextNbRoom = view.findViewById(R.id.fragment_add_property_edit_txt_nb_room);
        editTextAddress = view.findViewById(R.id.fragment_add_property_edit_txt_address);
        editTextDescription = view.findViewById(R.id.fragment_add_property_edit_txt_description);

        btnSubmit = view.findViewById(R.id.fragment_add_property_btn_submit);

        linearEntryDate = view.findViewById(R.id.fragment_add_property_container_entry_date);
        txtEntryDate = view.findViewById(R.id.fragment_add_property_txt_entry_date);

        btnSubmit.setEnabled(false);
        isDateSelected = false;

        typeSpinner.setOnItemSelectedListener(this);
        this.setSpinner(R.array.spinner_type, typeSpinner);

        this.onEditTextChanged(editTextPrice);
        this.onEditTextChanged(editTextArea);
        this.onEditTextChanged(editTextNbRoom);
        this.onEditTextChanged(editTextAddress);
        this.onEditTextChanged(editTextDescription);

        stringSelectedDate = Utils.getTodayDate();
        txtEntryDate.setText(stringSelectedDate);
        isDateSelected = true;

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),
                        isEditPrice + " " + isEditArea + " " + isEditNbRoom + " " + isEditAddress + " " + isEditDescription + " " + idSpinnerType,
                        Toast.LENGTH_SHORT).show();
            }
        });

        linearEntryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configureDatePickerDialog();
            }
        });

        return view;
    }

    private void setSpinner(int arrayId, Spinner spinner){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                arrayId,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        spinnerType(position);
    }

    // Method that is used when the user select an item for the type spinner
    private void spinnerType(int position){
        switch(position){
            case 0 :
                idSpinnerType = 0;
                isBtnEnable();
                break;
            case 1 :
                idSpinnerType = 1;
                isBtnEnable();
                break;
            case 2 :
                idSpinnerType = 2;
                isBtnEnable();
                break;
            case 3 :
                idSpinnerType = 3;
                isBtnEnable();
                break;
            case 4 :
                idSpinnerType = 4;
                isBtnEnable();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void onEditTextChanged(final EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    switch (editText.getId()){
                        case R.id.fragment_add_property_edit_txt_price:
                            isEditPrice = !charSequence.toString().isEmpty();
                            isBtnEnable();
                            break;
                        case R.id.fragment_add_property_edit_txt_area:
                            isEditArea = charSequence.length() != 0;
                            isBtnEnable();
                            break;
                        case R.id.fragment_add_property_edit_txt_nb_room:
                            isEditNbRoom = charSequence.length() != 0;
                            isBtnEnable();
                            break;
                        case R.id.fragment_add_property_edit_txt_address:
                            isEditAddress = charSequence.length() != 0;
                            isBtnEnable();
                            break;
                        case R.id.fragment_add_property_edit_txt_description:
                            if(charSequence.length() != 0)isEditDescription = true;
                            else isEditDescription = false;
                            isBtnEnable();
                            break;
                    }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void configureDatePickerDialog(){
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                stringSelectedDate = Utils.formatStringDate(year, month, day);
                txtEntryDate.setText(stringSelectedDate);
                isBtnEnable();
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

    private void isBtnEnable(){
        if(isEditPrice && isEditArea && isEditNbRoom && isEditAddress && isEditDescription)
            if(idSpinnerType != 0){
                if(Utils.convertStringToDate(stringSelectedDate).before(new Date())){
                    btnSubmit.setEnabled(true);
                    btnSubmit.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
                else {
                    btnSubmit.setEnabled(false);
                    btnSubmit.setBackgroundColor(getResources().getColor(R.color.darkGrey));
                }
            }
            else {
                btnSubmit.setEnabled(false);
                btnSubmit.setBackgroundColor(getResources().getColor(R.color.darkGrey));
            }
        else {
            btnSubmit.setEnabled(false);
            btnSubmit.setBackgroundColor(getResources().getColor(R.color.darkGrey));
        }
    }

    private boolean isLandscape(){
        return getResources().getBoolean(R.bool.is_landscape);
    }
}
