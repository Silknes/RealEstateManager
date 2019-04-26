package com.openclassrooms.realestatemanager.Controller.Fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
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
import com.openclassrooms.realestatemanager.Model.Photo;
import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.Model.User;
import com.openclassrooms.realestatemanager.PropertyViewModel;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

public class AddPropertyFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {
    private Spinner typeSpinner;
    private LinearLayout containerPhoto;
    private ScrollView snackbarContainer;
    private Button btnSubmit;
    private int idSpinnerType;
    private TextView txtEntryDate, txtAgent;
    private String stringSelectedDate, valueAddress, valueDescription, photoPath;
    private int valueSelectedDate;
    private double valuePrice;
    private int valueArea, valueNbRoom;
    private boolean isEditPrice, isEditArea, isEditNbRoom, isEditAddress, isEditDescription;

    private PropertyViewModel propertyViewModel;
    private long userId;
    boolean checkboxSchool, checkboxShop, checkboxParc, checkboxPublicTransport;

    private static final String PERMS = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;
    private int nbOfPhoto;
    private View dialogView;
    private EditText photoDescriptionEdit;
    private String photoDescriptionStr;

    private Photo photo;
    private List<Photo> photoList;
    private long propertyId;
    private Property property;
    private int positionPhoto;
    private SharedPreferences.Editor editor;
    private int nbOfCreation;

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
        txtAgent = view.findViewById(R.id.fragment_add_property_txt_agent);
        containerPhoto = view.findViewById(R.id.fragment_add_property_container_photo);
        snackbarContainer = view.findViewById(R.id.fragment_add_property_snackbar);
        LinearLayout linearAddPhoto = view.findViewById(R.id.fragment_add_property_linear_add_photo);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MY_SHARED_PREFERENCES", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userId = sharedPreferences.getLong("userId", -1);
        propertyId = sharedPreferences.getLong("propertyId", 1);

        this.configureViewModel();
        this.propertyViewModel.getCurrentUser().observe(this, this::setTxtAgent);

        btnSubmit.setEnabled(false);

        this.addTextWatcher(editTextPrice);
        this.addTextWatcher(editTextArea);
        this.addTextWatcher(editTextNbRoom);
        this.addTextWatcher(editTextAddress);
        this.addTextWatcher(editTextDescription);

        typeSpinner.setOnItemSelectedListener(this);
        this.setSpinner();

        stringSelectedDate = Utils.getTodayDate();
        valueSelectedDate = Utils.formatStringDateToInt(stringSelectedDate);
        txtEntryDate.setText(stringSelectedDate);
        linearEntryDate.setOnClickListener(view12 -> configureDatePickerDialog());

        this.photoList = new ArrayList<>();
        this.positionPhoto = 1;
        linearAddPhoto.setOnClickListener(view13 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Do you want to take a picture or pick a picture from gallery ?")
                    .setPositiveButton("Take picture", (dialog, which) -> writeOnStorage(2))
                    .setNegativeButton("From gallery", (dialog, which) -> writeOnStorage(1))
                    .show();
        });

        nbOfCreation = 0;
        btnSubmit.setOnClickListener(view1 -> {
            if(nbOfCreation < 1){
                registerTheProperty();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure to create this property again ? (You'll not be able to delete it)")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            registerTheProperty();
                        })
                        .setNegativeButton("No", (dialog, which) -> { })
                        .show();
            }
        });

        return view;
    }

    private void registerTheProperty(){
        createPropertyInDB();
        createPhotoInDb();
        propertyId++;
        editor.putLong("propertyId", propertyId).apply();
        Snackbar.make(snackbarContainer, "The property has been registered in the database", Snackbar.LENGTH_LONG).show();
        nbOfCreation++;
    }

    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(getContext());
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
        this.propertyViewModel.init(userId);
    }

    private void createPropertyInDB(){
        property = new Property(userId, idSpinnerType, valueAddress, valueDescription,
                valueSelectedDate, valuePrice, valueArea, valueNbRoom, checkboxSchool,
                checkboxShop, checkboxParc, checkboxPublicTransport);
        this.propertyViewModel.createProperty(property);
    }

    private void createPhotoInDb(){
        for (int i = 0; i < photoList.size() ; i++) {
            photoList.get(i).setPropertyId(propertyId);
            this.propertyViewModel.createPhoto(photoList.get(i));
        }
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
                            valuePrice = Double.parseDouble(charSequence.toString());
                            isEditPrice = true;
                        } else isEditPrice = false;
                        break;
                    case R.id.fragment_add_property_edit_txt_area:
                        if(charSequence.toString().trim().length() != 0) {
                            valueArea = Integer.parseInt(charSequence.toString());
                            isEditArea = true;
                        } else isEditArea = false;
                        break;
                    case R.id.fragment_add_property_edit_txt_nb_room:
                        if(charSequence.toString().trim().length() != 0) {
                            valueNbRoom = Integer.parseInt(charSequence.toString());
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
    public void writeOnStorage(int id){
        if(!EasyPermissions.hasPermissions(getContext(), PERMS)){
            EasyPermissions.requestPermissions(this, "Test", RC_IMAGE_PERMS, PERMS);
            return;
        }
        switch(id){
            case 1 :
                this.chooseImageFromPhone();
                break;
            case 2 :
                 takePictureFromCamera();
                 break;
        }
    }

    private void chooseImageFromPhone(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_CHOOSE_PHOTO);
    }

    private void takePictureFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            } catch (IOException e){
                Toast.makeText(getContext(), "error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            if(photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(
                        getContext(),
                        "com.openclassrooms.realestatemanager.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
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
        AlertDialog.Builder builderDescription = new AlertDialog.Builder(getContext());
        builderDescription.setMessage("Enter a description to the photo")
                .setView(dialogView)
                .setPositiveButton("Validate", (dialog, which) -> {
                    if(resultCode == RESULT_OK){
                        photoDescriptionStr = photoDescriptionEdit.getText().toString();
                        if(requestCode == RC_CHOOSE_PHOTO) this.glideNewPhoto(data, 1);
                        else if(requestCode == REQUEST_IMAGE_CAPTURE) this.glideNewPhoto(data, 2);
                        nbOfPhoto++;
                        isBtnEnable();
                    } else  Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void glideNewPhoto(Intent data, int id){
        if(id == 1){
            Glide.with(getContext())
                    .load(data.getData())
                    .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(Utils.convertDpToPx(20))))
                    .into(createNewPhoto());
            photo = new Photo(propertyId, photoDescriptionStr, data.getData().toString(), positionPhoto);
            photoList.add(photo);
            positionPhoto++;
        } if(id == 2){
            File f = new File(photoPath);
            Uri uri = Uri.fromFile(f);
            Glide.with(getContext())
                    .load(uri)
                    .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(Utils.convertDpToPx(20))))
                    .into(createNewPhoto());
            photo = new Photo(propertyId, photoDescriptionStr, uri.toString(), positionPhoto);
            photoList.add(photo);
            positionPhoto++;
        }
    }

    private File createImageFile() throws IOException {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
            photoPath = image.getAbsolutePath();
            return image;
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
                        for (int i = 0; i < containerPhoto.getChildCount(); i++) {
                            if(containerPhoto.getChildAt(i) == imageView) {
                                photoList.remove(i - 1);
                                for(int j = i - 1; j < photoList.size(); j++){
                                    photoList.get(j).setPosition(j + 1);
                                }
                            }
                        }
                        positionPhoto--;
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

    private void setTxtAgent(User user){
        txtAgent.setText(Utils.uppercaseFirstLetter(user.getUsername()));
    }
}
