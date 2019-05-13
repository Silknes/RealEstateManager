package com.openclassrooms.realestatemanager.Controller.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.openclassrooms.realestatemanager.Injections.Injection;
import com.openclassrooms.realestatemanager.Injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.Model.Photo;
import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.View.PropertyViewModel;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class AddPropertyFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {
    private static final int RC_IMAGE_CAPTURE = 101; // RequestCode when take picture from camera
    private static final int RC_CHOOSE_PHOTO = 102; // RequestCode when choose photo from gallery

    private LinearLayout containerPhoto;
    private ScrollView snackbarContainer;
    private Button btnSubmit;
    private int idSpinnerType;
    private TextView txtEntryDate;
    private EditText photoDescriptionEdit;

    private String stringSelectedDate, valueAddress = "", valueDescription = "", valueCity = "", photoPath, photoDescriptionStr;
    private int valueSelectedDate, valueArea, valueNbRoom, valuePostalCode, valueHouseNumber;
    private double valuePrice;
    private boolean valueCheckboxSchool, valueCheckboxShop, valueCheckboxParc, valueCheckboxTransport;

    private SharedPreferences sharedPreferences;
    private PropertyViewModel propertyViewModel;
    private long userId;
    private long propertyId; //
    private List<Photo> photoList; // Contain the list of photo create by the user

    private int nbOfPhoto; // Count the number of photo create by the user

    private OnButtonClickedListener callback;

    public interface OnButtonClickedListener{
        void onAddPropertyButtonClicked(Property property);
    }

    public AddPropertyFragment() {}

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_property, container, false);

        Spinner typeSpinner = view.findViewById(R.id.fragment_add_property_spinner_type);
        TextInputEditText editTextPrice = view.findViewById(R.id.fragment_add_property_input_edit_price);
        TextInputEditText editTextArea = view.findViewById(R.id.fragment_add_property_input_edit_area);
        TextInputEditText editTextNbRoom = view.findViewById(R.id.fragment_add_property_input_edit_nb_room);
        TextInputEditText editTextAddress = view.findViewById(R.id.fragment_add_property_input_edit_address);
        TextInputEditText editTextDescription = view.findViewById(R.id.fragment_add_property_input_edit_description);
        TextInputEditText editPostalCode = view.findViewById(R.id.fragment_add_property_input_edit_postal_code);
        TextInputEditText editHouseNumber = view.findViewById(R.id.fragment_add_property_input_edit_house_nb);
        TextInputEditText editCity = view.findViewById(R.id.fragment_add_property_input_edit_city);
        btnSubmit = view.findViewById(R.id.fragment_add_property_btn_submit);
        RelativeLayout linearEntryDate = view.findViewById(R.id.fragment_add_property_container_entry_date);
        txtEntryDate = view.findViewById(R.id.fragment_add_property_txt_entry_date);
        containerPhoto = view.findViewById(R.id.fragment_add_property_container_photo);
        snackbarContainer = view.findViewById(R.id.fragment_add_property_snackbar);
        LinearLayout linearAddPhoto = view.findViewById(R.id.fragment_add_property_linear_add_photo);

        sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("MY_SHARED_PREFERENCES", Context.MODE_PRIVATE);

        // Initialized for variables which need it
        userId = sharedPreferences.getLong("userId", -1);
        propertyId = sharedPreferences.getLong("propertyId", 1);
        nbOfPhoto = 1;
        photoList = new ArrayList<>();
        stringSelectedDate = Utils.getTodayDate();
        valueSelectedDate = Utils.formatCurrentDateToInt();

        this.configureViewModel();

        btnSubmit.setEnabled(false);

        this.addTextWatcher(editTextPrice);
        this.addTextWatcher(editTextArea);
        this.addTextWatcher(editTextNbRoom);
        this.addTextWatcher(editTextAddress);
        this.addTextWatcher(editTextDescription);
        this.addTextWatcher(editPostalCode);
        this.addTextWatcher(editHouseNumber);
        this.addTextWatcher(editCity);

        this.addOnViewClickListener(linearEntryDate);
        this.addOnViewClickListener(linearAddPhoto);
        btnSubmit.setEnabled(true);
        this.addOnViewClickListener(btnSubmit);

        this.setSpinner(typeSpinner);
        typeSpinner.setOnItemSelectedListener(this);

        txtEntryDate.setText(stringSelectedDate);

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
        callback = (OnButtonClickedListener) getActivity();
    }

    /******************
    **** Manage DB ****
    ******************/

    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(getContext());
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
        this.propertyViewModel.init(userId);
    }

    // Method that send a new property and new photos to DB
    private void sendNewDataToDb(){
        createPropertyInDB();
        createPhotoInDb();
        propertyId++;
        sharedPreferences.edit().putLong("propertyId", propertyId).apply();
        Snackbar.make(snackbarContainer, getString(R.string.add_property_adding_message), Snackbar.LENGTH_LONG).show();
    }

    // Add a new property to DB
    private void createPropertyInDB(){
        Property property = new Property(userId, idSpinnerType, valueAddress, valueCity, valueHouseNumber,
                valuePostalCode, valueDescription, valueSelectedDate, valuePrice, valueArea, valueNbRoom,
                valueCheckboxSchool, valueCheckboxShop, valueCheckboxParc, valueCheckboxTransport);
        this.propertyViewModel.createProperty(property);
        callback.onAddPropertyButtonClicked(property);
    }

    // Add all photos to DB
    private void createPhotoInDb(){
        for (int i = 0; i < photoList.size() ; i++) {
            photoList.get(i).setPropertyId(propertyId);
            this.propertyViewModel.createPhoto(photoList.get(i));
        }
    }

    /***************************
    **** Add action to view ****
    ***************************/

    // Method that add a TextWatcher to an EditText
    private void addTextWatcher(final TextInputEditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence inputValue, int i, int i1, int i2) {
                getUserInput(editText, inputValue);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    // Define what to do with user input for each EditText
    private void getUserInput(TextInputEditText editText, CharSequence inputValue){
        switch (editText.getId()){
            case R.id.fragment_add_property_input_edit_price:
                if(inputValue.toString().trim().length() != 0) valuePrice = Double.parseDouble(inputValue.toString());
                else valuePrice = 0;
                break;
            case R.id.fragment_add_property_input_edit_area:
                if(inputValue.toString().trim().length() != 0) valueArea = Integer.parseInt(inputValue.toString());
                else valueArea = 0;
                break;
            case R.id.fragment_add_property_input_edit_nb_room:
                if(inputValue.toString().trim().length() != 0) valueNbRoom = Integer.parseInt(inputValue.toString());
                else valueNbRoom = 0;
                break;
            case R.id.fragment_add_property_input_edit_address:
                if(inputValue.toString().trim().length() != 0) valueAddress = inputValue.toString().trim();
                else valueAddress = "";
                break;
            case R.id.fragment_add_property_input_edit_description:
                if(inputValue.toString().trim().length() != 0) valueDescription = inputValue.toString().trim();
                else valueDescription = "";
                break;
            case R.id.fragment_add_property_input_edit_city:
                if(inputValue.toString().trim().length() != 0) valueCity = inputValue.toString().trim();
                else valueCity = "";
                break;
            case R.id.fragment_add_property_input_edit_postal_code:
                if(inputValue.toString().trim().length() != 0) valuePostalCode = Integer.parseInt(inputValue.toString().trim());
                else valuePostalCode = 0;
                break;
            case R.id.fragment_add_property_input_edit_house_nb:
                if(inputValue.toString().trim().length() != 0) valueHouseNumber = Integer.parseInt(inputValue.toString().trim());
                else valueHouseNumber = 0;
                break;
        } isBtnEnable();
    }

    // Method that add a listener to a view
    private void addOnViewClickListener(View view){
        view.setOnClickListener(v -> actionForEachView(view));
    }

    // Define actions to do for each view which implements an OnClickListener
    private void actionForEachView(View view){
        switch(view.getId()){
            case R.id.fragment_add_property_container_entry_date:
                this.configureDatePickerDialog();
                break;
            case R.id.fragment_add_property_linear_add_photo:
                AlertDialog.Builder builderAddPhoto = new AlertDialog.Builder(getContext());
                builderAddPhoto.setMessage(getString(R.string.add_property_adding_photo_message))
                        .setPositiveButton(getString(R.string.add_property_adding_photo_camera), (dialog, which) -> this.takePictureFromCamera())
                        .setNegativeButton(getString(R.string.add_property_adding_photo_gallery), (dialog, which) -> this.chooseImageFromPhone())
                        .show();
                break;
            case R.id.fragment_add_property_btn_submit:
                this.sendNewDataToDb();
                break;
        }
    }

    // Method that configure a DatePickerDialog when correct view is clicked
    private void configureDatePickerDialog(){
        DatePickerDialog.OnDateSetListener onDateSetListener = (datePicker, year, month, day) -> {
            stringSelectedDate = Utils.formatStringDate(year, month, day);
            valueSelectedDate = Utils.formatIntDate(year, month, day);
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

    // Method that configure the spinner
    private void setSpinner(Spinner spinner){
        if(getContext() != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.spinner_type,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }

    // Method that add action when spinner item is selected
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        idSpinnerType = position;
        isBtnEnable();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    // Method that get the state of a checkbox when this one has been checked
    public void updatePoi(boolean[] pois){
        valueCheckboxSchool = pois[0];
        valueCheckboxShop = pois[1];
        valueCheckboxParc = pois[2];
        valueCheckboxTransport = pois[3];
    }

    // Method that unable button when all necessary params are correct
    private void isBtnEnable(){
        Date selectedDate = Utils.convertStringToDate(stringSelectedDate);
        if(valuePrice != 0 && valueArea != 0 && valueNbRoom != 0 && valueHouseNumber != 0 && valuePostalCode != 0
                && valueCity.length() != 0
                && valueDescription.length() != 0
                && valueAddress.length() != 0
                && idSpinnerType != 0
                && Utils.compareDate(selectedDate, new Date())
                && nbOfPhoto > 1){
            btnSubmit.setEnabled(true);
            btnSubmit.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            btnSubmit.setEnabled(false);
            btnSubmit.setBackgroundColor(getResources().getColor(R.color.darkGrey));
        }
    }

    /*************************
    **** Create new photo ****
    *************************/

    // Method that start a new activity where user can choose a photo in his gallery
    private void chooseImageFromPhone(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_CHOOSE_PHOTO);
    }

    // Method that start a new activity where user can take a picture from the camera and save it in the external storage
    private void takePictureFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            } catch (IOException e){
                Log.e("FileError", e.toString());
            }
            if(photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(
                        Objects.requireNonNull(getContext()),
                        "com.openclassrooms.realestatemanager.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, RC_IMAGE_CAPTURE);
            }
        }
    }

    // Method that create a new path for the photo to save it in the external storage
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRENCH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Objects.requireNonNull(getContext()).getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        photoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        this.handleResponse(requestCode, resultCode, data);
    }

    // Creating an AlertDialog when onActivityResult to allow user to enter a description for the photo
    @SuppressLint("InflateParams")
    private void handleResponse(int requestCode, int resultCode, Intent data){
        View dialogView = getLayoutInflater().inflate(R.layout.custom_alert_builder, null);
        photoDescriptionEdit = dialogView.findViewById(R.id.alert_dialog_builder_edit_text);
        if(resultCode == RESULT_OK){
            AlertDialog.Builder builderDescription = new AlertDialog.Builder(getContext());
            builderDescription.setMessage(getString(R.string.add_property_adding_photo_description))
                    .setView(dialogView)
                    .setPositiveButton(getString(R.string.add_property_adding_photo_description_positive), (dialog, which) -> {
                        photoDescriptionStr = photoDescriptionEdit.getText().toString();
                        if(requestCode == RC_CHOOSE_PHOTO) this.glideNewPhoto(data, 1);
                        else if(requestCode == RC_IMAGE_CAPTURE) this.glideNewPhoto(data, 2);
                    })
                    .show();
        }
    }

    // Method that display in an ImageView the new photo
    private void glideNewPhoto(Intent data, int id){
        Photo photo = new Photo();
        if(id == 1){
            Glide.with(Objects.requireNonNull(getContext()))
                    .load(data.getData())
                    .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(Utils.convertDpToPx(20))))
                    .into(createNewPhoto());
            photo = new Photo(propertyId, photoDescriptionStr, Objects.requireNonNull(data.getData()).toString(), nbOfPhoto);
        } if(id == 2){
            File f = new File(photoPath);
            Uri uri = Uri.fromFile(f);
            Glide.with(Objects.requireNonNull(getContext()))
                    .load(uri)
                    .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(Utils.convertDpToPx(20))))
                    .into(createNewPhoto());
            photo = new Photo(propertyId, photoDescriptionStr, uri.toString(), nbOfPhoto);
        }
        photoList.add(photo);
        nbOfPhoto++;
        isBtnEnable();
    }

    // Create the ImageView which will receive the new Photo
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
            builderLongClick.setMessage(getString(R.string.add_property_remove_photo_message))
                    .setPositiveButton(getString(R.string.alert_dialog_default_positive_message), (dialog, which) -> {
                        for (int i = 0; i < containerPhoto.getChildCount(); i++) {
                            if(containerPhoto.getChildAt(i) == imageView) {
                                photoList.remove(i - 1);
                                for(int j = i - 1; j < photoList.size(); j++){
                                    photoList.get(j).setPosition(j + 1);
                                }
                            }
                        }
                        containerPhoto.removeView(imageView);
                        nbOfPhoto--;
                        isBtnEnable();
                    })
                    .setNegativeButton(getString(R.string.alert_dialog_default_negative_message), (dialog, which) -> { })
                    .show();
            return true;
        });
        containerPhoto.addView(imageView);

        return imageView;
    }
}
