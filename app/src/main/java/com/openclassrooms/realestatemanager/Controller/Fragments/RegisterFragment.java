package com.openclassrooms.realestatemanager.Controller.Fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.openclassrooms.realestatemanager.Injections.Injection;
import com.openclassrooms.realestatemanager.Injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.Model.User;
import com.openclassrooms.realestatemanager.View.PropertyViewModel;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.Util.Utils;

public class RegisterFragment extends Fragment {
    private Button signInButton; // Button which manage SignIn feature
    private TextView emailTakenTxt;
    private String emailValue = "", passwordValue = "", usernameValue = ""; // Each variables will receive a value from user input
    private PropertyViewModel propertyViewModel;

    private OnButtonClickedListener callback; // Use to create a callback to parent activity

    public interface OnButtonClickedListener{
        void onSignInButtonClicked();
    }

    public RegisterFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        EditText emailView = view.findViewById(R.id.fragment_register_edit_email);
        EditText passwordView = view.findViewById(R.id.fragment_register_edit_password);
        EditText usernameView = view.findViewById(R.id.fragment_register_edit_username);
        signInButton = view.findViewById(R.id.fragment_register_btn_sign_in);
        emailTakenTxt = view.findViewById(R.id.fragment_register_email_already_taken);

        this.configureViewModel();

        this.addTextWatcher(emailView);
        this.addTextWatcher(passwordView);
        this.addTextWatcher(usernameView);

        // Creating a new user in DB with user input
        signInButton.setOnClickListener(v ->
                this.propertyViewModel.isEmailAlreadyTaken(emailValue)
                        .observe(this, this::isEmailAlreadyTaken ));

        return view;
    }

    /******************
    **** Manage DB ****
    ******************/

    private void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(getContext());
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
    }

    private void isEmailAlreadyTaken(User user){
        if(user == null){
            User newUser = new User(emailValue, passwordValue, usernameValue);
            propertyViewModel.createUser(newUser);

            callback.onSignInButtonClicked();
        } else emailTakenTxt.setVisibility(View.VISIBLE);
    }

    /*******************************************
    **** Manage callback to parent activity ****
    *******************************************/

    // Create callback when we are sure fragment is attach to parent activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.createCallbackToParentActivity();
    }

    // Method that create our callback
    private void createCallbackToParentActivity(){
        callback = (OnButtonClickedListener) getActivity();
    }

    /***************************
    **** And action to view ****
    ***************************/

    // Method that add a TextWatcher to an EditText
    private void addTextWatcher(final EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence inputValue, int start, int before, int count) {
                getUserInput(editText, inputValue);
                enableButton();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Define what to do with user input for each EditText
    private void getUserInput(EditText editText, CharSequence inputValue){
        if(inputValue.toString().trim().length() != 0){
            switch(editText.getId()){
                case R.id.fragment_register_edit_email:
                    if(Utils.isEmailCorrect(inputValue.toString())) emailValue = inputValue.toString();
                    else emailValue = "";
                    emailTakenTxt.setVisibility(View.GONE);
                    break;
                case R.id.fragment_register_edit_password:
                    if(Utils.isPasswordCorrect(inputValue.toString())) passwordValue = inputValue.toString();
                    else passwordValue = "";
                    break;
                case R.id.fragment_register_edit_username:
                    if(Utils.isUsernameCorrect(inputValue.toString())) usernameValue = inputValue.toString();
                    else usernameValue = "";
                    break;
            }
        }
    }

    // Method that enable SignInButton according to user input
    private void enableButton(){
        if(emailValue.length() != 0 && passwordValue.length() != 0 && usernameValue.length() != 0){
            signInButton.setEnabled(true);
            signInButton.setBackground(getResources().getDrawable(R.drawable.custom_button_enabled));
        } else {
            signInButton.setEnabled(false);
            signInButton.setBackground(getResources().getDrawable(R.drawable.custom_button_disabled));
        }
    }
}
