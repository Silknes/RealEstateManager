package com.openclassrooms.realestatemanager.Controller.Fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.openclassrooms.realestatemanager.Injections.Injection;
import com.openclassrooms.realestatemanager.Injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.PropertyViewModel;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.Utils;

import java.util.Calendar;
import java.util.Date;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

public class AddPropertyFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {
    private Spinner typeSpinner;
    private LinearLayout containerPhoto;
    private ScrollView snackbarContainer;
    private Button btnSubmit;
    private int idSpinnerType;
    private TextView txtEntryDate;
    private String stringSelectedDate, valuePrice, valueArea, valueNbRoom, valueAddress, valueDescription;
    private boolean isEditPrice, isEditArea, isEditNbRoom, isEditAddress, isEditDescription;

    private PropertyViewModel propertyViewModel;
    private long userId;
    boolean checkboxSchool, checkboxShop, checkboxParc, checkboxPublicTransport;

    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;
    private int nbOfPhoto;
    private View dialogView;
    private EditText photoDescriptionEdit;
    private String photoDescriptionStr;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private SharedPreferences.Editor editor;

    public AddPropertyFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_property, container, false);

        typeSpinner = view.findViewById(R.id.fragment_add_property_spinner_type);
        EditText editTextPrice = view.findViewById(R.id.fragment_add_property_edit_txt_price);
        EditText editTextArea = view.findViewById(R.id.fragment_add_property_edit_txt_area);
        EditText editTextNbRoom = view.findViewById(R.id.fragment_add_property_edit_txt_nb_room);
        EditText editTextAddress = view.findViewById(R.id.fragment_add_property_edit_txt_address);
        EditText editTextDescription = view.findViewById(R.id.fragment_add_property_edit_txt_description);
        btnSubmit = view.findViewById(R.id.fragment_add_property_btn_submit);
        LinearLayout linearEntryDate = view.findViewById(R.id.fragment_add_property_container_entry_date);
        txtEntryDate = view.findViewById(R.id.fragment_add_property_txt_entry_date);
        containerPhoto = view.findViewById(R.id.fragment_add_property_container_photo);
        snackbarContainer = view.findViewById(R.id.fragment_add_property_snackbar);
        LinearLayout linearAddPhoto = view.findViewById(R.id.fragment_add_property_linear_add_photo);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MY_SHARED_PREFERENCES", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userId = getContext().getSharedPreferences("MY_SHARED_PREFERENCES", Context.MODE_PRIVATE).getLong("userId", -1);

        this.configureViewModel();

        btnSubmit.setEnabled(false);

        this.addTextWatcher(editTextPrice);
        this.addTextWatcher(editTextArea);
        this.addTextWatcher(editTextNbRoom);
        this.addTextWatcher(editTextAddress);
        this.addTextWatcher(editTextDescription);

        typeSpinner.setOnItemSelectedListener(this);
        this.setSpinner();

        stringSelectedDate = Utils.getTodayDate();
        txtEntryDate.setText(stringSelectedDate);
        linearEntryDate.setOnClickListener(view12 -> configureDatePickerDialog());

        linearAddPhoto.setOnClickListener(view13 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Do you want to take a picture or pick a picture from gallery ?")
                    .setPositiveButton("Take picture", (dialog, which) -> takePictureFromCamera())
                    .setNegativeButton("From gallery", (dialog, which) -> onClickAddFile())
                    .show();
        });

        btnSubmit.setOnClickListener(view1 -> {
            createPropertyInDB();
            Snackbar.make(snackbarContainer, "The property has been registered in the database", Snackbar.LENGTH_LONG).show();
        });

        return view;
    }

    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(getContext());
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
        this.propertyViewModel.init(userId);
    }

    private void createPropertyInDB(){
        Property property = new Property(userId, idSpinnerType, valueAddress, valueDescription,
                stringSelectedDate, valuePrice, valueArea, valueNbRoom, checkboxSchool,
                checkboxShop, checkboxParc, checkboxPublicTransport);
        this.propertyViewModel.createProperty(property);
    }

    private void addTextWatcher(final EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                switch (editText.getId()){
                    case R.id.fragment_add_property_edit_txt_price:
                        if(charSequence.toString().trim().length() != 0) {
                            valuePrice = charSequence.toString().trim();
                            isEditPrice = true;
                        } else isEditPrice = false;
                        break;
                    case R.id.fragment_add_property_edit_txt_area:
                        if(charSequence.toString().trim().length() != 0) {
                            valueArea = charSequence.toString().trim();
                            isEditArea = true;
                        } else isEditArea = false;
                        break;
                    case R.id.fragment_add_property_edit_txt_nb_room:
                        if(charSequence.toString().trim().length() != 0) {
                            valueNbRoom = charSequence.toString().trim();
                            isEditNbRoom = true;
                        } else isEditNbRoom = false;
                        break;
                    case R.id.fragment_add_property_edit_txt_address:
                        if(charSequence.toString().trim().length() != 0) {
                            valueAddress = charSequence.toString().trim();
                            isEditAddress = true;
                        } else isEditAddress = false;
                        break;
                    case R.id.fragment_add_property_edit_txt_description:
                        if(charSequence.toString().trim().length() != 0) {
                            valueDescription = charSequence.toString().trim();
                            isEditDescription = true;
                        } else isEditDescription = false;
                        break;
                } isBtnEnable();
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void setSpinner(){
        if(getContext() != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.spinner_type,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            typeSpinner.setAdapter(adapter);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        idSpinnerType = position;
        isBtnEnable();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    private void configureDatePickerDialog(){
        DatePickerDialog.OnDateSetListener onDateSetListener = (datePicker, year, month, day) -> {
            stringSelectedDate = Utils.formatStringDate(year, month, day);
            txtEntryDate.setText(stringSelectedDate);
            isBtnEnable();
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        if(getContext() != null){
            DatePickerDialog dialog = new DatePickerDialog(
                    getContext(),
                    onDateSetListener,
                    year, month, day);
            dialog.show();
        }
    }

    public void updatePoi(boolean[] pois){
        checkboxSchool = pois[0];
        checkboxShop = pois[1];
        checkboxParc = pois[2];
        checkboxPublicTransport = pois[3];
    }

    private void isBtnEnable(){
        Date selectedDate = Utils.convertStringToDate(stringSelectedDate);
        if(isEditAddress && isEditArea && isEditDescription && isEditNbRoom && isEditPrice
                && idSpinnerType != 0
                && Utils.compareDate(selectedDate, new Date())
                && nbOfPhoto > 0){
            btnSubmit.setEnabled(true);
            btnSubmit.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            btnSubmit.setEnabled(false);
            btnSubmit.setBackgroundColor(getResources().getColor(R.color.darkGrey));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onClickAddFile(){
        this.chooseImageFromPhone();
    }

    private void chooseImageFromPhone(){
        if(getContext() != null) if(!EasyPermissions.hasPermissions(getContext(), PERMS)){
            EasyPermissions.requestPermissions(this, "Test", RC_IMAGE_PERMS, PERMS);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_CHOOSE_PHOTO);
    }

    private void takePictureFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        this.handleResponse(requestCode, resultCode, data);
    }

    private void handleResponse(int requestCode, int resultCode, Intent data){
        dialogView = getLayoutInflater().inflate(R.layout.custom_alert_builder, null);
        photoDescriptionEdit = dialogView.findViewById(R.id.alert_dialog_builder_edit_text);
        if(requestCode == RC_CHOOSE_PHOTO){
            if(resultCode == RESULT_OK){
                AlertDialog.Builder builderDescription = new AlertDialog.Builder(getContext());
                builderDescription.setMessage("Enter a description to the photo")
                        .setView(dialogView)
                        .setPositiveButton("Validate", (dialog, which) -> {
                            photoDescriptionStr = photoDescriptionEdit.getText().toString();
                            Glide.with(getContext())
                                    .load(data.getData())
                                    .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(Utils.convertDpToPx(20))))
                                    .into(createNewPhoto());
                            nbOfPhoto++;
                            isBtnEnable();
                        })
                        .show();
            } else {
                Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            AlertDialog.Builder builderDescription = new AlertDialog.Builder(getContext());
            builderDescription.setMessage("Enter a description to the photo")
                    .setView(dialogView)
                    .setPositiveButton("Validate", (dialog, which) -> {
                        photoDescriptionStr = photoDescriptionEdit.getText().toString();
                        Glide.with(getContext())
                                .load(data.getExtras().get("data"))
                                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(Utils.convertDpToPx(20))))
                                .into(createNewPhoto());
                        nbOfPhoto++;
                        isBtnEnable();
                    })
                    .show();
        }
    }

    private ImageView createNewPhoto(){
        ImageView imageView = new ImageView(getContext());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                Utils.convertDpToPx(80),
                Utils.convertDpToPx(80));
        layoutParams.setMarginEnd(8);
        imageView.setLayoutParams(layoutParams);
        imageView.setContentDescription(photoDescriptionStr);

        imageView.setLongClickable(true);
        imageView.setOnLongClickListener(v -> {
            AlertDialog.Builder builderLongClick = new AlertDialog.Builder(getContext());
            builderLongClick.setMessage("Do you want to remove this photo ?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        containerPhoto.removeView(imageView);
                        nbOfPhoto--;
                        isBtnEnable();
                    })
                    .setNegativeButton("No", (dialog, which) -> { })
                    .show();
            return true;
        });
        containerPhoto.addView(imageView);

        return imageView;
    }
}
