package com.openclassrooms.realestatemanager.Controller.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.Controller.Activities.FullScreenActivity;
import com.openclassrooms.realestatemanager.Injections.Injection;
import com.openclassrooms.realestatemanager.Injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.Model.GeocodingApi.ApiResult;
import com.openclassrooms.realestatemanager.Model.Photo;
import com.openclassrooms.realestatemanager.Model.Property;
import com.openclassrooms.realestatemanager.Model.User;
import com.openclassrooms.realestatemanager.View.PropertyViewModel;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.ItemClickSupport;
import com.openclassrooms.realestatemanager.Util.MapsApiCalls;
import com.openclassrooms.realestatemanager.Util.Utils;
import com.openclassrooms.realestatemanager.View.PhotoAdapter;

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

public class EditPropertyFragment extends Fragment implements AdapterView.OnItemSelectedListener, MapsApiCalls.CallbacksGeocoding {
    private Spinner spinnerType, spinnerStatus;
    private TextInputEditText priceView, descriptionView, addressView, areaView, nbRoomView, nbHouseView, cityView, postalCodeView;
    private EditText photoDescriptionEdit;
    private TextView saleDateView, entryDateView, textAgent, saleDateTitle;
    private RelativeLayout relativeSaleDate, relativeEntryDate;
    private CheckBox checkBoxSchool, checkBoxShop, checkBoxParc, checkBoxTransport;

    private ImageView staticMap;

    private RecyclerView recyclerView;
    private PhotoAdapter adapter;

    private PropertyViewModel propertyViewModel;

    private Property property;
    private int valueSaleDate, valueEntryDate;
    private boolean valueSchool, valueShop, valueParc, valueTransport;

    private static final int RC_CHOOSE_PHOTO = 101;
    private static final int RC_IMAGE_CAPTURE = 102;
    private String photoPath, photoDescriptionStr;
    private Photo photo;
    private List<Photo> photoList;
    private List<Photo> photoListToAddInDb = new ArrayList<>();
    private List<Photo> photoListToRemoveInDb = new ArrayList<>();

    private OnButtonClickedListener callback;

    public interface OnButtonClickedListener{
        void onMortgageSimulatorButtonClicked(double price);
    }

    public EditPropertyFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_property, container, false);

        spinnerType = view.findViewById(R.id.fragment_edit_property_spinner_type);
        spinnerStatus = view.findViewById(R.id.fragment_edit_property_spinner_status);

        priceView = view.findViewById(R.id.fragment_edit_property_input_edit_price);
        descriptionView = view.findViewById(R.id.fragment_edit_property_input_edit_description);
        addressView = view.findViewById(R.id.fragment_edit_property_input_edit_address);
        areaView = view.findViewById(R.id.fragment_edit_property_input_edit_area);
        nbRoomView = view.findViewById(R.id.fragment_edit_property_input_edit_nb_room);
        postalCodeView = view.findViewById(R.id.fragment_edit_property_input_edit_postal_code);
        nbHouseView = view.findViewById(R.id.fragment_edit_property_input_edit_house_number);
        cityView = view.findViewById(R.id.fragment_edit_property_input_edit_city);

        saleDateView = view.findViewById(R.id.fragment_edit_property_txt_date_sale);
        entryDateView = view.findViewById(R.id.fragment_edit_property_txt_entry_sale);
        textAgent = view.findViewById(R.id.fragment_edit_property_txt_agent);
        saleDateTitle = view.findViewById(R.id.fragment_edit_property_sale_date_title);

        LinearLayout linearAddPhoto = view.findViewById(R.id.fragment_edit_property_linear_add_photo);
        relativeSaleDate = view.findViewById(R.id.fragment_edit_property_container_date_sale);
        relativeEntryDate = view.findViewById(R.id.fragment_edit_property_container_entry_date);
        LinearLayout linearStaticMap = view.findViewById(R.id.fragment_edit_property_container_static_map);

        checkBoxSchool = view.findViewById(R.id.fragment_edit_property_checkbox_school);
        checkBoxShop = view.findViewById(R.id.fragment_edit_property_checkbox_shop);
        checkBoxParc = view.findViewById(R.id.fragment_edit_property_checkbox_parc);
        checkBoxTransport = view.findViewById(R.id.fragment_edit_property_checkbox_public_transport);

        Button mortgageBtn = view.findViewById(R.id.fragment_edit_property_btn_mortgage);

        recyclerView = view.findViewById(R.id.fragment_edit_property_recycler_view);

        staticMap = view.findViewById(R.id.fragment_edit_property_static_map);

        property = (Property) (getArguments() != null ? getArguments().getSerializable("property") : null);

        this.configureViewModel();

        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
        this.removePhotoOnLongClick();
        this.getPhotos();

        this.addOnViewClickListener(linearAddPhoto);
        this.addOnViewClickListener(relativeSaleDate);
        this.addOnViewClickListener(relativeEntryDate);

        this.setSpinner(spinnerType, R.array.spinner_type);
        this.setSpinner(spinnerStatus, R.array.spinner_status);
        this.spinnerStatus.setOnItemSelectedListener(this);

        this.addListenerToCheckbox(checkBoxSchool);
        this.addListenerToCheckbox(checkBoxShop);
        this.addListenerToCheckbox(checkBoxParc);
        this.addListenerToCheckbox(checkBoxTransport);

        this.updateViewWithPropertyData();

        if(Utils.isInternetAvailable(Objects.requireNonNull(getContext()))) {
            this.fetchLocationFromAddress();
            linearStaticMap.setVisibility(View.VISIBLE);
        }

        mortgageBtn.setOnClickListener(v -> callback.onMortgageSimulatorButtonClicked(Double.parseDouble(priceView.getText().toString())));

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

    /****************************************
    **** Set default value for each view ****
    ****************************************/

    // Method that update views with data get from the property
    @SuppressLint("SetTextI18n")
    private void updateViewWithPropertyData(){
        this.priceView.setText("" + property.getPrice());
        this.areaView.setText("" + property.getArea());
        this.nbRoomView.setText("" + property.getNbRoom());
        this.descriptionView.setText(property.getDescription());
        this.addressView.setText(property.getAddress());
        this.cityView.setText(property.getCity());
        this.nbHouseView.setText("" + property.getHouseNumber());
        this.postalCodeView.setText("" + property.getPostalCode());
        this.checkBoxSchool.setChecked(property.isCheckboxSchool());
        this.checkBoxShop.setChecked(property.isCheckboxShop());
        this.checkBoxParc.setChecked(property.isCheckboxParc());
        this.checkBoxTransport.setChecked(property.isCheckboxPublicTransport());
        this.saleDateView.setText(Utils.getTodayDate());
        this.entryDateView.setText(Utils.formatIntDateToString(property.getEntryDate()));
        this.spinnerType.setSelection(property.getType());
        this.spinnerStatus.setSelection(property.getStatus());

        this.getAgent();
    }

    private void setTextAgent(User user){
        textAgent.setText(Utils.uppercaseFirstLetter(user.getUsername()));
    }

    /***************************
    **** Add action to view ****
    ***************************/

    // Method that add a listener to a view
    private void addOnViewClickListener(View view){
        view.setOnClickListener(v -> actionForEachView(view));
    }

    // Define actions to do for each view which implements an OnClickListener
    private void actionForEachView(View view){
        switch(view.getId()){
            case R.id.fragment_edit_property_linear_add_photo:
                this.setOnClickListenerToPhotoLayout();
                break;
            case R.id.fragment_edit_property_container_date_sale:
                this.configureDatePickerDialog(relativeSaleDate);
                break;
            case R.id.fragment_edit_property_container_entry_date:
                this.configureDatePickerDialog(relativeEntryDate);
                break;
        }
    }

    // Method that configure a DatePickerDialog when correct view is clicked
    private void configureDatePickerDialog(RelativeLayout dateLayout){
        DatePickerDialog.OnDateSetListener onDateSetListener = (datePicker, year, month, day) -> setDate(dateLayout, year, month, day);

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

    private void setDate(RelativeLayout dateLayout, int year, int month, int day){
        String stringSelectedDate = Utils.formatStringDate(year, month, day);
        switch(dateLayout.getId()){
            case R.id.fragment_edit_property_container_entry_date:
                valueEntryDate = Utils.formatIntDate(year, month, day);
                entryDateView.setText(stringSelectedDate);
            case R.id.fragment_edit_property_container_date_sale:
                valueSaleDate = Utils.formatIntDate(year, month, day);
                saleDateView.setText(stringSelectedDate);
        }
    }

    // Method that configure the spinner
    private void setSpinner(Spinner spinner, int arrayId){
        if(getContext() != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    arrayId,
                    R.layout.my_spinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == 1) {
            relativeSaleDate.setVisibility(View.VISIBLE);
            saleDateTitle.setVisibility(View.VISIBLE);
        }
        else {
            relativeSaleDate.setVisibility(View.GONE);
            saleDateTitle.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    // Method that add a listener for each checkbox and save the current state of the checkbox
    private void addListenerToCheckbox(final CheckBox checkBox){
        checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            switch(compoundButton.getId()){
                case R.id.fragment_edit_property_checkbox_school:
                    valueSchool = isChecked;
                    break;
                case R.id.fragment_edit_property_checkbox_shop:
                    valueShop = isChecked;
                    break;
                case R.id.fragment_edit_property_checkbox_parc:
                    valueParc = isChecked;
                    break;
                case R.id.fragment_edit_property_checkbox_public_transport:
                    valueTransport = isChecked;
                    break;
            }
        });
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

    // Method that remove the photo on long click on it
    private void removePhotoOnLongClick(){
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_property_item)
                .setOnItemLongClickListener((recyclerView, position, v) -> {
                    if(photoList.size() > 1) this.removePhotoInRecyclerView(adapter.getPhoto(position));
                    return true;
                });
    }

    // Method that remove the photo in the recycler view
    private void removePhotoInRecyclerView(Photo photo){
        photoListToRemoveInDb.add(photo);
        photoList.remove(photo.getPosition() - 1);
        for(int i = 0; i < photoList.size(); i++) {
            photoList.get(i).setPosition(i + 1);
        }
        for(int i = 0; i < photoListToAddInDb.size(); i++) {
            if(photo.getPosition() == photoListToAddInDb.get(i).getPosition()) {
                photoListToAddInDb.remove(i);
            }
            if(photoListToAddInDb.size() > 0) photoListToAddInDb.get(i).setPosition(getPhotoPosition() - 1);
        }
        adapter.updateData(photoList);
    }

    /******************
    **** Manage DB ****
    ******************/

    private void configureViewModel(){
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(getContext());
        this.propertyViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PropertyViewModel.class);
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

    // Method that delete a photo in the DB
    private void deletePhotoInDb(List<Photo> photoList){
        if(photoList.size() != 0) {
            for (int i = 0; i < photoList.size(); i++) {
                this.propertyViewModel.deletePhoto(photoList.get(i).getId());
            }
            for (int i = 0; i < this.photoList.size(); i++) {
                this.propertyViewModel.updatePhoto(this.photoList.get(i));
            }
        }
    }

    // Method that create new photo in DB for each photos added by the user
    private void updatePhotoProperty(){
        if(photoListToAddInDb.size() != 0) {
            for (int i = 0; i < photoListToAddInDb.size(); i++) {
                this.propertyViewModel.createPhoto(photoListToAddInDb.get(i));
            }
            this.getPhotos();
            photoListToAddInDb = new ArrayList<>();
        }
    }

    // Method call in parent activity to get the property if the user wants to save the changes
    private Property getUpdateProperty(){
        if(priceView.getText().toString().length() != 0) property.setPrice(Double.parseDouble(priceView.getText().toString()));
        if(descriptionView.getText().toString().length() != 0) property.setDescription(descriptionView.getText().toString());
        if(addressView.getText().toString().length() != 0) property.setAddress(addressView.getText().toString());
        if(areaView.getText().toString().length() != 0) property.setArea(Integer.parseInt(areaView.getText().toString()));
        if(nbRoomView.getText().toString().length() != 0) property.setNbRoom(Integer.parseInt(nbRoomView.getText().toString()));
        if(spinnerType.getSelectedItemPosition() != 0) property.setType(spinnerType.getSelectedItemPosition());
        if(spinnerStatus.getSelectedItemPosition() != 0) property.setStatus(spinnerStatus.getSelectedItemPosition());
        if(cityView.getText().toString().length() != 0) property.setCity(cityView.getText().toString());
        if(nbHouseView.getText().toString().length() != 0) property.setHouseNumber(Integer.parseInt(nbHouseView.getText().toString()));
        if(postalCodeView.getText().toString().length() != 0) property.setPostalCode(Integer.parseInt(postalCodeView.getText().toString()));
        property.setSaleDate(valueSaleDate);
        property.setEntryDate(valueEntryDate);
        property.setCheckboxSchool(valueSchool);
        property.setCheckboxShop(valueShop);
        property.setCheckboxParc(valueParc);
        property.setCheckboxPublicTransport(valueTransport);
        return property;
    }

    // Method that update data in the DB if the user wants to save the changes
    public void updateDB(){
        this.updatePhotoProperty();
        this.deletePhotoInDb(photoListToRemoveInDb);

        this.propertyViewModel.updateProperty(getUpdateProperty());
    }

    private void getAgent(){
        this.propertyViewModel.getPropertyAgent(property.getUserId()).observe(this, this::setTextAgent);
    }

    /******************************
    **** Add photo to property ****
    ******************************/

    private void setOnClickListenerToPhotoLayout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.add_property_adding_photo_message))
                .setPositiveButton(getString(R.string.add_property_adding_photo_camera), (dialog, which) -> this.takePictureWithCamera())
                .setNegativeButton(getString(R.string.add_property_adding_photo_gallery), (dialog, which) -> this.chooseImageInGallery())
                .show();
    }

    // Method that start a new activity where user can take a picture from the camera and save it in the external storage
    private void takePictureWithCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            } catch (IOException e){
                Toast.makeText(getContext(), "error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
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

    // Method that start a new activity where user can choose a photo in his gallery
    private void chooseImageInGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_CHOOSE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        this.handleResponse(requestCode, resultCode, data);
    }

    // Creating an AlertDialog when onActivityResult to allow user to enter a description for the photo
    private void handleResponse(int requestCode, int resultCode, Intent data){
        @SuppressLint("InflateParams") View dialogView = getLayoutInflater().inflate(R.layout.custom_alert_builder, null);
        photoDescriptionEdit = dialogView.findViewById(R.id.alert_dialog_builder_edit_text);
        if(resultCode == RESULT_OK){
            AlertDialog.Builder builderDescription = new AlertDialog.Builder(getContext());
            builderDescription.setMessage(getString(R.string.add_property_adding_photo_description))
                    .setView(dialogView)
                    .setPositiveButton(getString(R.string.add_property_adding_photo_description_positive), (dialog, which) -> {
                        photoDescriptionStr = photoDescriptionEdit.getText().toString();
                        if(requestCode == RC_CHOOSE_PHOTO) this.createNewPhoto(data, 1);
                        else if(requestCode == RC_IMAGE_CAPTURE) this.createNewPhoto(data, 2);
                    })
                    .show();
        }
    }

    // Method that display in an ImageView the new photo
    private void createNewPhoto(Intent data, int id){
        switch(id){
            case 1:
                photo = new Photo(property.getId(), photoDescriptionStr, Objects.requireNonNull(data.getData()).toString(), this.getPhotoPosition());
                break;
            case 2:
                File f = new File(photoPath);
                Uri uri = Uri.fromFile(f);
                photo = new Photo(property.getId(), photoDescriptionStr, uri.toString(), this.getPhotoPosition());
                break;
        }
        photoListToAddInDb.add(photo);
        photoList.add(photo);
    }

    // Method that return the correct position for a photo
    private int getPhotoPosition(){
        return photoList.size() + 1;
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
